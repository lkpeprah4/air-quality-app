package com.ghanaairwatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This is the file that starts the whole backend. Running this class's
// main() method boots up an embedded web server on port 8080.
@SpringBootApplication
public class GhanaAirwatchBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GhanaAirwatchBackendApplication.class, args);
    }

}
