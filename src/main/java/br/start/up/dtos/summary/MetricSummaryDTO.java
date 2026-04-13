package br.start.up.dtos.summary;

import lombok.Data;

@Data
public class MetricSummaryDTO {

    private String createAt;

    private Long accesses;

    private long countNewUsers;
}
