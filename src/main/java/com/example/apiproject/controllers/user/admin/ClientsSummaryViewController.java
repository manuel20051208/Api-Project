package com.example.apiproject.controllers.user.admin;

import com.example.apiproject.entities.admin.ClientsSummaryView;
import com.example.apiproject.services.user.admin.ClientsSummaryViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/client-show-summary")
@RequiredArgsConstructor
public class ClientsSummaryViewController {
    public final ClientsSummaryViewService clientsSummaryViewService;

    @GetMapping
    public List<ClientsSummaryView> showAll(){
        return clientsSummaryViewService.showAll();
    }

    @GetMapping("/name/{name}")
    public List<ClientsSummaryView> showByName(
            @PathVariable String name ){
        return clientsSummaryViewService.showClientsSummaryByName(name);
    }

    @GetMapping("/email/{email}")
    public List<ClientsSummaryView> showByEmail(
            @PathVariable String email ){
        return clientsSummaryViewService.showClientsSummaryByEmail(email);
    }
}