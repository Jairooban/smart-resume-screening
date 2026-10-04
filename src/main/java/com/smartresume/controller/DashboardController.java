package com.smartresume.controller;

import com.smartresume.dto.ApiResponse;
import com.smartresume.dto.DashboardMetricsResponse;
import com.smartresume.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Aggregated system KPIs, score distributions, and frequency charts")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get high-level summary metrics, score distribution, and top skills")
    public ResponseEntity<ApiResponse<DashboardMetricsResponse>> getDashboardMetrics() {
        DashboardMetricsResponse metrics = dashboardService.getDashboardMetrics();
        return ResponseEntity.ok(ApiResponse.ok(metrics));
    }
}
