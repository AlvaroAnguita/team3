package com.prueba.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prueba.models.entity.Cliente;
import com.prueba.service.ClienteService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



@RestController
@RequestMapping("/team3")
public class ClienteRestController {
	@Autowired
	private ClienteService clienteService;
	
	//Petición GET
	@GetMapping("clientes")
	@ApiOperation(value = "Devuelve el listado completo de los clientes",
	  notes = "Devuelve el listado completo con los datos de los clientes", 
	  response = Cliente.class)
	public List<Cliente> index() {
		return clienteService.findAll();
	}
	
	@GetMapping("clientes/{id}")
	@ApiOperation(value = "Devuelve la informacion del cliente dado",
	  notes = "Devolvera toda la informacion relativa al identificador del cliente dado", 
	  response = Cliente.class)
	public ResponseEntity<?>show(@ApiParam(value = "El id del cliente que se quiere mostrar",required=true) @PathVariable Long id){
		Cliente cliente= null;
		Map<String,Object> response= new HashMap<>();
		
		try {
			cliente= clienteService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje","Error al realizar consulta en base de datos");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(cliente==null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);
	}
	
	@GetMapping("uploads/img/{nombreFoto:.+}")
	@ApiOperation(value = "Muestra la imagen que se ha pasado",
	  notes = "Dado el nombre de la imagen se mostrara la imagen en el navegador", 
	  response = Cliente.class)
	public ResponseEntity<Resource> verFoto(@ApiParam(value = "Nombre de la foto a mostrar",required=true) @PathVariable String nombreFoto){
		Path rutaArchivo= Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();
		
		Resource recurso = null;
		
		try {
			recurso=new UrlResource(rutaArchivo.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		if(!recurso.exists() && !recurso.isReadable()) {
			throw new RuntimeException("Error no se puede cargar la imagen " + nombreFoto);
		}
		
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\""+recurso.getFilename()+"\"");
		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}
	
	@PostMapping("clientes")
	@ApiOperation(value = "Crea un cliente",
    notes = "Se recibe la informacion relativa a un cliente para crearlo y almacenarlo en la base de datos", 
    response = Cliente.class)
	public ResponseEntity<?>create(@ApiParam(value = "La informacion del cliente que se va a crear",required=true) @RequestBody Cliente cliente){
		Cliente clienteNew=null;
		Map<String, Object> response= new HashMap<>();
		
		try {
			clienteNew= clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar insert en base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje","El cliente ha sido creado con éxito!");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	@PostMapping("clientes/upload")
	@ApiOperation(value = "Sube una imagen de un usuario dado",
    notes = "Dada una imagen y el identificador del cliente al cual se le asociara la imagen y se subira a la carpeta uploads", 
    response = Cliente.class)
	public ResponseEntity<?>upload(@ApiParam(value = "El archivo que se va a subir",required=true)@RequestParam("archivo") MultipartFile archivo,@ApiParam(value = "El id del cliente al cual se asociara la imagen",required=true) @RequestParam("id") Long id){
		Map<String, Object> response= new HashMap<>();
		
		Cliente cliente= clienteService.findById(id);
		
		if(!archivo.isEmpty()) {
			String nombreArchivo= UUID.randomUUID().toString()+"_"+archivo.getOriginalFilename().replace(" ","");
			Path rutaArchivo= Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
			
			try {
				Files.copy(archivo.getInputStream(),rutaArchivo);
			} catch (IOException e) {
				response.put("mensaje","Error al subir la imagen del cliente");
				response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			String nombreFotoAnterior= cliente.getImagen();
			
			if(nombreFotoAnterior !=null && nombreFotoAnterior.length()>0) {
			Path rutaFotoAnterior= Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
			File archivoFotoAnterior= rutaFotoAnterior.toFile();
			
			if(archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
				archivoFotoAnterior.delete();
			}
		}
			cliente.setImagen(nombreArchivo);
			
			clienteService.save(cliente);
			
			response.put("cliente", cliente);
			response.put("mensaje","Has subido correctamente la imagen: " + nombreArchivo);
		}
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	
	@PutMapping("clientes/{id}")
	@ApiOperation(value = "Actualiza la informacion de un cliente",
    notes = "Dado un cliente y su informacion se actualizara la informacion necesaria en la base de datos", 
    response = Cliente.class)
	public ResponseEntity<?> update(@ApiParam(value = "La informacion actualizada del cliente que se desea actualizar",required=true)@RequestBody Cliente cliente,@ApiParam(value = "Identificador del cliente a actualizar",required=true) @PathVariable Long id){
		Cliente clienteActual=clienteService.findById(id);
		
		Cliente clienteUpdate=null;
		Map<String,Object> response= new HashMap<>();
		
		if(clienteActual==null) {
			response.put("mensaje","Error: no se pudo editar, el cliente ID: ".concat(id.toString().concat("no existe el id en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		try {
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setTelefono(cliente.getTelefono());
			clienteActual.setDireccion(cliente.getDireccion());
			clienteActual.setCodigoP(cliente.getCodigoP());
			
			
			clienteUpdate=clienteService.save(clienteActual);
		} catch (DataAccessException e){
			response.put("mensaje","Error al actualizar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje","El cliente ha sido creado con éxito!");
		response.put("cliente", clienteUpdate);
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}

	
	@DeleteMapping("clientes/{id}")
	@ApiOperation(value = "Elimina un cliente",
    notes = "Dado el identificador de un cliente, se eliminara ese cliente ", 
    response = Cliente.class)
	public ResponseEntity<?> delete(@ApiParam(value = "Identificador del cliente que se va a eliminar",required=true)@PathVariable Long id){
		Map<String,Object> response= new HashMap<>();
		
		try {
			clienteService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje","Error al eliminar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ha sido eliminado con éxito");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@DeleteMapping("clientes")
	@ApiOperation(value = "Elimina todos los clientes",
    notes = "Se eliminaran todos los clientes de la base de datos", 
    response = Cliente.class)
	public ResponseEntity<?> deleteAll(){
		Map<String,Object> response= new HashMap<>();
		
		try {
			clienteService.deleteAll(clienteService.findAll());
		} catch (DataAccessException e) {
			response.put("mensaje","Error al eliminar los clientes de la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "los clientes han sido borrados");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		
	}
	
	
}
