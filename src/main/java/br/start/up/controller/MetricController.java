package br.start.up.controller;

import br.start.up.services.DashboardMetricsService;
import br.start.up.services.IndicadoresServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/metrics")
public class MetricController {

    @Autowired
    private DashboardMetricsService service;

    @Autowired
    private IndicadoresServices indicadoresServices;

    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    @GetMapping("/latest-days")
    public ResponseEntity<?> getDays(){
        return ResponseEntity.ok(service.latestDays());
    }

    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    @GetMapping("/latest-weeks")
    public ResponseEntity<?> getWeeks(){
        return ResponseEntity.ok(service.latestWeeks());
    }

    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    @GetMapping("/latest-months")
    public ResponseEntity<?> getMonths(){
        return ResponseEntity.ok(service.latestMonths());
    }

    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    @GetMapping("/latest-years")
    public ResponseEntity<?> getYears(){
        return ResponseEntity.ok(service.latestYears());
    }
}
