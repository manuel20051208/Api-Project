package com.apiproject.repositories.reportGenerator;

import com.apiproject.enums.FileTypes;
import com.apiproject.repositories.projection.ReportDashboardProjection;

import java.io.IOException;
import java.util.List;

public interface ReportService {
    byte[] export(List<ReportDashboardProjection> data) throws IOException;
    String getContentType();
    FileTypes getType();
    String getFileExtension();
    String getFileName();
}