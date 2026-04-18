package com.example.apiproject.controllers.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User", description = "Endpoints for managing users")
@RestController
@RequestMapping("/api/user")
public class UserController {

}