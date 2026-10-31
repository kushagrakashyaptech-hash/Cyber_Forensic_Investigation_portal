
package com.cyberforensic.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CyberForensicApplication {

    public static void main(String[] args) {
        SpringApplication.run(CyberForensicApplication.class, args);

        System.out.println("====================================");
        System.out.println(" CYBER FORENSIC PORTAL STARTED!");
        System.out.println("====================================");
    }
}
