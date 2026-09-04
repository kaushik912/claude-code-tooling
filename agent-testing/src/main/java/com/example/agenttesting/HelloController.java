package com.example.agenttesting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Hello")
public class HelloController {

    @GetMapping("/hello")
    @Operation(summary = "Returns a hello world greeting")
    public String hello() {
        return "Hello, World!";
    }
}
