package com.example.buggyproject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Hello")
@RestController
public class HelloController {

    @Operation(summary = "Health check")
    @GetMapping("/hello")
    public String hello() {
        return "Hello, buggy-project!";
    }
}
