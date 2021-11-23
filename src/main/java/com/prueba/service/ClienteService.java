package com.prueba.service;

import java.util.List;

import com.prueba.models.entity.Cliente;



public interface ClienteService {
public List<Cliente> findAll();
public Cliente findById(Long id);
public Cliente save(Cliente cliente);
public void delete(Long id);
public void deleteAll(List<Cliente> lista);

}
