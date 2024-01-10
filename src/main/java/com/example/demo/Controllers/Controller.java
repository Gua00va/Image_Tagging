package com.example.demo.Controllers;

import com.example.demo.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Collection;

@RestController
@RequestMapping("/api")
public class Controller {
    @Autowired
    ServiceClass serviceClass;

    @GetMapping("/getFiles/{folderPath}")
    private ResponseEntity<String> getFiles(@PathVariable String folderPath) {
//        ResponseEntity.ok(serviceClass.findAllFiles(folderPath));
        String newPath = folderPath.replace('_', '/');
        return ResponseEntity.ok(serviceClass.print(newPath));
    }

    @GetMapping("/hi")
    private ResponseEntity<String> getGreeting() {
        return ResponseEntity.ok("Hello");
    }

    //endpoint that returns key value csv
}
