package com.example.assesement.controller;

import com.example.assesement.service.GridSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simulation")
@CrossOrigin(origins = "*")
public class SimulationController {

    @Autowired
    private GridSimulationService gridSimulationService;

    @PostMapping("/step")
    public ResponseEntity<Map<String, Object>> stepSimulation() {
        gridSimulationService.stepSimulation();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Simulated one coordinate step: active agents have moved closer to their destinations");
        return ResponseEntity.ok(response);
    }
}
