package com.example.apiproject.controllers.client;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Client", description = "Endpoints for managing clients")
@RestController
@RequestMapping("/api/client")
public class ClientControllers {
}