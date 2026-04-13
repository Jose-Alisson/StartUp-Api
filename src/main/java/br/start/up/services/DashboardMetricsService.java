package br.start.up.services;

import br.start.up.dtos.summary.MetricSummaryDTO;
import br.start.up.enums.MetricType;
import br.start.up.model.Business;
import br.start.up.model.Metrics;
import br.start.up.repository.AccountRepository;
import br.start.up.repository.MetricsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import io.lettuce.core.json.JsonObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardMetricsService {

    private String PREFIX = "startup:dashboard:metrics";

    public static final String FEATURE_PREFIX = "startup:metrics:feature";

    @Autowired
    private MetricsRepository repository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RedisTemplate<String, Object> redis;

    @Autowired
    private ModelMapper mapper;

    @Async
    public void incrementNewUser() {
        redis.opsForValue().increment(PREFIX + ":count");
    }

    public void incrementAccess() {
    }

    @Async
    public void incrementNewBusiness(Long business){
        redis.opsForSet().add(PREFIX + ":new-business", business);
    }

    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    @Scheduled(cron = "0 5 0 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void generateDailyMetrics() {

        LocalDate yesterday = LocalDate.now(ZONE).minusDays(1);

        OffsetDateTime startOfDay = yesterday.atStartOfDay(ZONE).toOffsetDateTime();

        if (repository.existsByTypeAndCreateAt(MetricType.DAY, startOfDay)) return;

        long accesses = Optional.ofNullable((String) redis.opsForValue().get(PREFIX + ":accesses"))
                .map(Long::valueOf).orElse(0L);

        long newUsers = Optional.ofNullable((String) redis.opsForValue().get(PREFIX + ":count"))
                .map(Long::valueOf).orElse(0L);

        Metrics metrics = Metrics.builder()
                .createAt(startOfDay)
                .type(MetricType.DAY)
                .accesses(accesses)
                .countNewUsers(newUsers)
                .build();

        repository.save(metrics);

        redis.opsForValue().set(PREFIX + ":accesses", "0");
        redis.opsForValue().set(PREFIX + ":count", "0");
    }

    @Scheduled(cron = "0 10 0 * * SUN", zone = "America/Sao_Paulo")
    @Transactional
    public void generateWeeklyMetrics() {

        LocalDate today = LocalDate.now(ZONE);

        LocalDate weekStart = today
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                .minusWeeks(1)
                .with(DayOfWeek.MONDAY);

        LocalDate weekEnd = weekStart.plusDays(6);

        OffsetDateTime start = weekStart.atStartOfDay(ZONE).toOffsetDateTime();
        OffsetDateTime end = weekEnd.plusDays(1).atStartOfDay(ZONE).toOffsetDateTime(); // exclusivo

        if (repository.existsByTypeAndCreateAt(MetricType.WEEK, start)) return;

        List<Metrics> days = repository
                .findByTypeAndCreateAtBetween(MetricType.DAY, start, end);

        long totalAccesses = days.stream().mapToLong(Metrics::getAccesses).sum();
        long totalNewUsers = days.stream().mapToLong(Metrics::getCountNewUsers).sum();

        Metrics weekMetrics = Metrics.builder()
                .createAt(start)
                .type(MetricType.WEEK)
                .accesses(totalAccesses)
                .countNewUsers(totalNewUsers)
                .build();

        repository.save(weekMetrics);
    }

    @Scheduled(cron = "0 15 0 1 * *", zone = "America/Sao_Paulo")
    @Transactional
    public void generateMonthlyMetrics() {

        YearMonth lastMonth = YearMonth.now(ZONE).minusMonths(1);

        OffsetDateTime start = lastMonth.atDay(1)
                .atStartOfDay(ZONE)
                .toOffsetDateTime();

        OffsetDateTime end = lastMonth.plusMonths(1)
                .atDay(1)
                .atStartOfDay(ZONE)
                .toOffsetDateTime(); // exclusivo

        if (repository.existsByTypeAndCreateAt(MetricType.MONTH, start)) return;

        List<Metrics> days = repository
                .findByTypeAndCreateAtBetween(MetricType.DAY, start, end);

        long totalAccesses = days.stream().mapToLong(Metrics::getAccesses).sum();
        long totalNewUsers = days.stream().mapToLong(Metrics::getCountNewUsers).sum();

        Metrics monthMetrics = Metrics.builder()
                .createAt(start)
                .type(MetricType.MONTH)
                .accesses(totalAccesses)
                .countNewUsers(totalNewUsers)
                .build();

        repository.save(monthMetrics);
    }

    @Scheduled(cron = "0 20 0 1 1 *", zone = "America/Sao_Paulo")
    @Transactional
    public void generateYearlyMetrics() {

        int lastYear = Year.now(ZONE).minusYears(1).getValue();

        OffsetDateTime start = LocalDate.of(lastYear, 1, 1)
                .atStartOfDay(ZONE)
                .toOffsetDateTime();

        OffsetDateTime end = LocalDate.of(lastYear + 1, 1, 1)
                .atStartOfDay(ZONE)
                .toOffsetDateTime(); // exclusivo

        if (repository.existsByTypeAndCreateAt(MetricType.YEAR, start)) return;

        List<Metrics> days = repository
                .findByTypeAndCreateAtBetween(MetricType.DAY, start, end);

        long totalAccesses = days.stream().mapToLong(Metrics::getAccesses).sum();
        long totalNewUsers = days.stream().mapToLong(Metrics::getCountNewUsers).sum();

        Metrics yearMetrics = Metrics.builder()
                .createAt(start)
                .type(MetricType.YEAR)
                .accesses(totalAccesses)
                .countNewUsers(totalNewUsers)
                .build();

        repository.save(yearMetrics);
    }

    public List<MetricSummaryDTO> latestDays() {
        return repository.findDayMetricsLatestDays().stream().map(m -> mapper.map(m, MetricSummaryDTO.class)).toList();
    }

    public List<MetricSummaryDTO> latestWeeks() {
        return repository.findWeekMetricsLatestWeeks().stream().map(m -> mapper.map(m, MetricSummaryDTO.class)).toList();
    }

    public List<MetricSummaryDTO> latestMonths() {
        return repository.findMonthMetricsLatestMonths().stream().map(m -> mapper.map(m, MetricSummaryDTO.class)).toList();
    }

    public List<MetricSummaryDTO> latestYears() {
        return repository.findYearMetricsLatestYears().stream().map(m -> mapper.map(m, MetricSummaryDTO.class)).toList();
    }

    @Scheduled(cron = "0 0 20 ? * THU", zone = "America/Sao_Paulo")
    public void sendNotificationNewBusinessByWeek() throws FirebaseMessagingException {
        Long size =  redis.opsForSet().size(PREFIX + ":new-business");

        if (size != null && size > 0) {
            var messageStr = size == 1 ?
                    "novo empreendimento já estão no app!"
            : "%d novos empreendimentos já estão no app!".formatted(size);

            Message message = Message.builder()
                    .setTopic("newBusiness")
                    .setNotification(Notification.builder()
                            .setTitle("Novidades no app")
                            .setBody(messageStr)
                            .build())
                    .build();

            FirebaseMessaging.getInstance().send(message);

            redis.delete(PREFIX + ":new-business");
        }
    }
}
