package com.example.apiproject.controllers.admin;


import com.example.apiproject.repositories.projection.ClientSummaryProjection;
import com.example.apiproject.services.user.admin.ClientsSummaryViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Client Dashboard Summaries", description = "Endpoints for fetching summarized client data")
@RestController
@RequestMapping("api/client-show-summary")
@RequiredArgsConstructor
public class ClientsSummaryViewController {
    public final ClientsSummaryViewService clientsSummaryViewService;

    @Operation(summary = "Get all clients summaries")
    @GetMapping("/getNames/{userId}")
    public Page<ClientSummaryProjection> showAll(
            @PathVariable("userId") Long userId
    ){
        return clientsSummaryViewService.showAllForSales(userId);
    }

    @Operation(summary = "Search client summaries by exact name")
    @GetMapping("/name/{userId}")
    public Page<ClientSummaryProjection> showByName(
            @PathVariable Long userId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "10") int page){
        return clientsSummaryViewService.showClientsSummaryByName(userId,name, page);
    }

    @Operation(summary = "Search client summaries by exact email")
    @GetMapping("/email/{userId}")
    public Page<ClientSummaryProjection> showByEmail(
            @PathVariable("userId") Long userId,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "10") int page){
        return clientsSummaryViewService.showClientsSummaryByEmail(userId,email, page);
    }
}