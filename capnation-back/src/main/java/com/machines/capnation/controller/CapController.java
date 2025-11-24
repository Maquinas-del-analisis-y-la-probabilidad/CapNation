package com.machines.capnation.controller;


import com.machines.capnation.model.Cap;
import com.machines.capnation.service.CapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/cap")
@RestController
@CrossOrigin("*")
public class CapController {

    @Autowired
    private final CapService service;

    public CapController(CapService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<Cap> saveCap(@RequestBody Cap cap) {
        var result = service.save(cap);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<Cap>> getAllCaps() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/find")
    public ResponseEntity<Cap> getById(@RequestParam("id") Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @GetMapping("/find/brand")
    public ResponseEntity<List<Cap>> findByCap(@RequestParam("brand") String brand) {
        return ResponseEntity.ok().body(service.findByBrand(brand));
    }
}
