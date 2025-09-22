package com.machines.capnation.service.impl;

import com.machines.capnation.exceptions.InvalidParametersCapException;
import com.machines.capnation.model.Cap;
import com.machines.capnation.repository.CapRepository;
import com.machines.capnation.service.CapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CapServiceImplementation implements CapService {
    @Autowired
    private final CapRepository repository;

    public CapServiceImplementation(CapRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Cap> findAll() {
        return repository.findAll();
    }

    @Override
    public Cap save(Cap cap) {
        if (cap.getPrice() <= 0) {
            throw new InvalidParametersCapException("The price of the cap is invalid");
        }
        if (cap.getStock() <= 0) {
            throw new InvalidParametersCapException("The stock of the gorra must be bigger than cero");
        }
        if (cap.getColor().isBlank()) {
            throw new InvalidParametersCapException("The color can not be void");
        }
        return repository.save(cap);
    }
}
