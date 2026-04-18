package com.example.apiproject.controllers.general;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Sale", description = "Endpoints for handling sales")
@RestController
@RequestMapping("/api/sale")
public class saleController {
}
