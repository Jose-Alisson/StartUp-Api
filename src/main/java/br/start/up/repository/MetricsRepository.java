package br.start.up.repository;

import br.start.up.enums.MetricType;
import br.start.up.model.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public interface MetricsRepository extends JpaRepository<Metrics, Long> {

    boolean existsByTypeAndCreateAt(MetricType type, OffsetDateTime createAt);

    // Buscar todos os DAYs entre duas datas
    List<Metrics> findByTypeAndCreateAtBetween(MetricType type, OffsetDateTime start, OffsetDateTime end);

    // Opcional: buscar DAYs da semana passada
    @Query("SELECT m FROM Metrics m WHERE m.type = 'DAY' AND m.createAt >= :start AND m.createAt <= :end")
    List<Metrics> findDayMetricsBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    @Query(value = "SELECT * FROM Metrics m WHERE m.type = 'DAY' AND m.create_at > CURRENT_DATE - INTERVAL '7 days' ORDER BY m.create_at ASC", nativeQuery = true)
    List<Metrics> findDayMetricsLatestDays();

    @Query(value = "SELECT * FROM Metrics m WHERE m.type = 'WEEK' AND m.create_at > CURRENT_DATE - INTERVAL '1 months'", nativeQuery = true)
    List<Metrics> findWeekMetricsLatestWeeks();

    @Query(value = "SELECT * FROM Metrics m WHERE m.type = 'MONTH' AND m.create_at > CURRENT_DATE - INTERVAL '1 years'", nativeQuery = true)
    List<Metrics> findMonthMetricsLatestMonths();

    @Query(value = "SELECT * FROM Metrics m WHERE m.type = 'YEAR' AND m.create_at > CURRENT_DATE - INTERVAL '10 years'", nativeQuery = true)
    List<Metrics> findYearMetricsLatestYears();
}
