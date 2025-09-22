package com.machines.capnation.controller;


import com.machines.capnation.model.Cap;
import com.machines.capnation.service.CapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RequestMapping("/cap")
@Controller
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
}
