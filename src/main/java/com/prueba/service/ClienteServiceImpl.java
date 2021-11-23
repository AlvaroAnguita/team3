package com.prueba.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.prueba.models.dao.ClienteDao;
import com.prueba.models.entity.Cliente;

@Service
public class ClienteServideImpl implements ClienteService{
	@Autowired
	private ClienteDao clienteDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Cliente>findAll(){
		return (List<Cliente>) clienteDao.findAll(); 
	}
	@Override
	@Transactional(readOnly = true)
	public Cliente findById(Long id){
		return clienteDao.findById(id).orElse(null);
	}
	@Override
	@Transactional
	public Cliente save(Cliente cliente){
		return clienteDao.save(cliente);
	}
	@Override
	@Transactional
	public void delete(Long id){
		clienteDao.deleteById(id);;
	}
	
}
