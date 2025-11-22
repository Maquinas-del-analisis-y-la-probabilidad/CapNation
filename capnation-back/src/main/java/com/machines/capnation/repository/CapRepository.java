package com.machines.capnation.repository;

import com.machines.capnation.model.Cap;

import java.util.List;

public interface CapRepository {
    List<Cap> findAll(); // finds all caps stored in file

    Cap save(Cap cap); // save a new cap in the file and return it

    Cap findById(Long id);

    List<Cap> findByBrand(String brand);
}
