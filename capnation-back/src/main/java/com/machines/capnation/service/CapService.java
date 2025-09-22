package com.machines.capnation.service;

import com.machines.capnation.model.Cap;

import java.util.List;

public interface CapService {
    List<Cap> findAll();

    Cap save(Cap cap);
}
