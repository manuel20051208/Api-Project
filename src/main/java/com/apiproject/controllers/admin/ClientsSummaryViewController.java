package com.apiproject.controllers.admin;


import com.apiproject.repositories.projection.ClientSummaryProjection;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.ClientsSummaryViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Client Dashboard Summaries", description = "Endpoints for fetching summarized client data")
@RestController
@RequestMapping("api/client-show-summary")
@RequiredArgsConstructor
public class ClientsSummaryViewController {
    public final ClientsSummaryViewService clientsSummaryViewService;

    @Operation(summary = "Get all clients summaries (paginated, page is 0-based)")
    @GetMapping("/getNames")
    public Page<ClientSummaryProjection> showAll(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ){
        return clientsSummaryViewService.showAllForSales(authenticatedUser.id(), page, size);
    }

    @Operation(summary = "Search client summaries by name (paginated, page is 0-based)")
    @GetMapping("/name")
    public Page<ClientSummaryProjection> showByName(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return clientsSummaryViewService.showClientsSummaryByName(authenticatedUser.id(),name, page, size);
    }

    @Operation(summary = "Search client summaries by email (paginated, page is 0-based)")
    @GetMapping("/email")
    public Page<ClientSummaryProjection> showByEmail(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return clientsSummaryViewService.showClientsSummaryByEmail(authenticatedUser.id(),email, page, size);
    }
}
