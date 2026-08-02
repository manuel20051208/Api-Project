package com.example.apiproject.repositories.admin;

import com.example.apiproject.repositories.projection.ReportDashboardProjection;

import java.io.IOException;
import java.util.List;

public interface ReportExporter {
    byte[] export(List<ReportDashboardProjection> data) throws IOException;
    String getContentType();
    String getFileExtension();
}