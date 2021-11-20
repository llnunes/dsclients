package com.devsuperior.dsclients.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsclients.dtos.ClientDTO;
import com.devsuperior.dsclients.entities.Client;
import com.devsuperior.dsclients.repositories.ClientRepository;
import com.devsuperior.dsclients.services.exceptions.DataBaseException;
import com.devsuperior.dsclients.services.exceptions.ResourceNotFoundException;

@Service
public class ClientService {

	@Autowired
	private ClientRepository repository;
	
	@Transactional(readOnly = false)
	public Page<ClientDTO> findAllPaged(PageRequest pageRequest) {
		Page<Client> list = repository.findAll(pageRequest);
		return list.map(c -> new ClientDTO(c));
	}
	
	@Transactional(readOnly = false)
	public ClientDTO findById(Long id) {
		Optional<Client> opt = repository.findById(id);
		Client client = opt.orElseThrow(() -> new ResourceNotFoundException("Erro ao recuperar Client"));
		return new ClientDTO(client);
	}
	
	@Transactional
	public ClientDTO insert(ClientDTO dto) {
		Client obj = new Client();
		copyDtoToEntity(dto, obj);
		repository.save(obj);
		return new ClientDTO(obj);
	}
	
	@Transactional
	public ClientDTO update(Long id, ClientDTO dto) {
		try {
			Client obj = repository.getOne(id);
			copyDtoToEntity(dto, obj);			
			repository.save(obj);
			return new ClientDTO(obj);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id não encontrado: " + id);
		}
	}
	
	public void delete(Long id) {
		try {			
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id não encontrado: " + id);
		} catch (DataIntegrityViolationException di) {
			throw new DataBaseException("Violação de Integridade: " + id);
		} 
	}
	
	private void copyDtoToEntity(ClientDTO dto, Client obj) {		
		obj.setName(dto.getName());
		obj.setCpf(dto.getCpf());
		obj.setIncome(dto.getIncome());
		obj.setBirthDate(dto.getBirthDate());
		obj.setChildren(dto.getChildren());
	}
}
