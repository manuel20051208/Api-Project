package com.apiproject.services.admin;

import com.apiproject.enums.FileTypes;
import com.apiproject.repositories.reportGenerator.ReportService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReportServiceFactory {

    private final Map<FileTypes, ReportService> reportService;

    public ReportServiceFactory(List<ReportService> reportService) {
        this.reportService = reportService.stream()
                .collect(Collectors.toMap(ReportService::getType, Function.identity()));
    }

    public ReportService getService(FileTypes type) {
        ReportService reporter = reportService.get(type);
        if (reporter == null) {
            throw new IllegalArgumentException("Tipo de reporte no soportado: " + type);
        }
        return reporter;
    }
}
