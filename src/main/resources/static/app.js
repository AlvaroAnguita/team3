"use strict";
let data = [];
fetch('http://localhost:8080/team3/clientes')
	.then((res) => res.json())
	.then((data) => {
		data = data;
		console.log(data);
		renderAlumnos();
	});


const nombre = document.querySelector(".js-nombre");
const apellidos = document.querySelector(".js-apellido");
const email = document.querySelector(".js-email");
const telefono = document.querySelector(".js-telefono");
const direccion = document.querySelector(".js-direccion");
const codigoP = document.querySelector(".js-codigoP");
const imagen = document.querySelector(".js-imagen");
const dni = document.querySelector(".js-dni");

const alumno = document.querySelector(".js-alumno");
let alumnosHTML = "";

const renderAlumnos = () => {

	for (const iterator of data) {
		nombre.innerHTML = iterator.nombre;
		apellidos.innerHTML = iterator.apellido;
		email.innerHTML = iterator.email;
		telefono.innerHTML = iterator.telefono;
		direccion.innerHTML = iterator.direccion;
		codigoP.innerHTML = iterator.codigoP;
		imagen.innerHTML = iterator.imagen;
		dni.innerHTML = iterator.dni;
	};
}

const borrarAlum = document.querySelector(".js-borrar");

const borrarAlumno = () => {
	fetch('http://localhost:8080/team3/clientes/1', {
		method: "DELETE"
	})
		.then((res) => res.json())
		.then((data) => {
			data = data;
			console.log(data.mensaje);

		});
}
borrarAlum.addEventListener("click", borrarAlumno);