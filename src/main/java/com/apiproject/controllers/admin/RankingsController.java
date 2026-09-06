package com.apiproject.controllers.admin;

import com.apiproject.DTOs.General.TheThreeBestClients;
import com.apiproject.DTOs.General.TheThreeBestProducts;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.RankingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Rankings", description = "Top clients and products rankings")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class RankingsController {

    private final RankingsService rankingsService;

    @Operation(summary = "Get the top 3 best clients by number of purchases")
    @GetMapping("/best-clients")
    public List<TheThreeBestClients> getBestClients(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return rankingsService.getBestClients(authenticatedUser.id());
    }

    @Operation(summary = "Get the top 3 best products by number of purchases")
    @GetMapping("/best-products")
    public List<TheThreeBestProducts> getBestProducts(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return rankingsService.getBestProducts(authenticatedUser.id());
    }
}
