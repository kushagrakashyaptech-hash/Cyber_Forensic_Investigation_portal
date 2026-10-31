
package com.cyberforensic.portal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public String checkHealth() {
        return "Cyber Forensic Investigation Portal API is running!";
    }
}
