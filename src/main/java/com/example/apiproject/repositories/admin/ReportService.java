package com.example.apiproject.repositories.admin;

import java.io.IOException;

public interface ReportService {
    byte[] generateReport(Long userId) throws IOException;
    String getFileName();
    String getContentType();
}
