package com.example.apiproject.controllers.admin;

import com.example.apiproject.entities.admin.ClientsSummaryView;
import com.example.apiproject.services.user.admin.ClientsSummaryViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Client Dashboard Summaries", description = "Endpoints for fetching summarized client data")
@RestController
@RequestMapping("api/client-show-summary")
@RequiredArgsConstructor
public class ClientsSummaryViewController {
    public final ClientsSummaryViewService clientsSummaryViewService;

    @Operation(summary = "Get all clients summaries")
    @GetMapping
    public List<ClientsSummaryView> showAll(){
        return clientsSummaryViewService.showAll();
    }

    @Operation(summary = "Search client summaries by exact name")
    @GetMapping("/name/{name}")
    public List<ClientsSummaryView> showByName(
            @PathVariable String name ){
        return clientsSummaryViewService.showClientsSummaryByName(name);
    }

    @Operation(summary = "Search client summaries by exact email")
    @GetMapping("/email/{email}")
    public List<ClientsSummaryView> showByEmail(
            @PathVariable String email ){
        return clientsSummaryViewService.showClientsSummaryByEmail(email);
    }
}