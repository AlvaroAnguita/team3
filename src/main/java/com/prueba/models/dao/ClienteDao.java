package com.prueba.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.prueba.models.entity.Cliente;

public interface ClienteDao extends CrudRepository<Cliente, Long> {

}
