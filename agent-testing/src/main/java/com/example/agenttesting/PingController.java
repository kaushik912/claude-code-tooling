package com.example.agenttesting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Ping")
public class PingController {

    @GetMapping("/api/v1/ping")
    @Operation(summary = "Returns pong to confirm the service is up and running")
    public String ping() {
        return "pong";
    }
}
