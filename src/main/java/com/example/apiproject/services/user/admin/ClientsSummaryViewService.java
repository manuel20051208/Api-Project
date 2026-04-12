package com.example.apiproject.services.user.admin;

import com.example.apiproject.entities.admin.ClientsSummaryView;
import com.example.apiproject.repositories.admin.ClientsSummaryViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientsSummaryViewService {
    public final ClientsSummaryViewRepository clientsSummaryViewRepository;

    public List<ClientsSummaryView> showClientsSummaryByName(String clientName){
        return clientsSummaryViewRepository.findAllByClientNameIgnoreCase(clientName);
    }

    public List<ClientsSummaryView> showClientsSummaryByEmail(String clientEmail){
        return clientsSummaryViewRepository.findAllByClientEmailIgnoreCase(clientEmail);
    }

    public List<ClientsSummaryView> showAll(){
        return clientsSummaryViewRepository.findAll();
    }
}
