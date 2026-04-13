package br.start.up.services;

import br.start.up.dtos.detail.BusinessDetailDTO;
import br.start.up.dtos.request.BusinessRequestDTO;
import br.start.up.dtos.request.BusinessWithIdRequestDTO;
import br.start.up.dtos.summary.BusinessInflation;
import br.start.up.dtos.summary.BusinessSummaryDTO;
import br.start.up.indexes.impl.BusinessIndexer;
import br.start.up.model.*;
import br.start.up.repository.BusinessRepository;
import br.start.up.repository.CategoryRepository;
import br.start.up.repository.LegalRepository;
import br.start.up.specification.BusinessSpecification;
import br.start.up.utils.OptionalCascade;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import static br.start.up.services.DashboardMetricsService.FEATURE_PREFIX;

@Service
public class BusinessService {

    @Autowired
    private BusinessRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LegalRepository legalRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private RedisTemplate<String, Object> redis;

    @Autowired
    private IndicadoresServices indicadoresS;

    @Autowired
    private DashboardMetricsService metricsService;

    @Autowired
    private LuceneService luceneService;

    @Autowired
    private BusinessIndexer indexer;

    public BusinessSummaryDTO create(BusinessRequestDTO business) {
        Category category = categoryRepository.findByName(business.getCategory());

        var legals = business.getLegalStructure()
                .getRequirements().stream()
                .map(r -> mapper.map(r, Legal.class))
                .map(l -> legalRepository.getReferenceById(l.getId()))
                .toList();

        var b = Business.builder()
                .name(business.getName())
                .category(category)
                .description(business.getDescription())
                .imageUrl(business.getImageUrl())
                .initialInvestment(business.getInitialInvestment())
                .monthlyProfit(business.getMonthlyProfit())
                .profitMargin(business.getProfitMargin())
                .isFeatured(business.isFeatured())
                .ricks(business.getRicks().stream().map(r -> mapper.map(r, Risk.class)).toList())
                .legalStructure(mapper.map(business.getLegalStructure(), LegalStructure.class).toBuilder().requirements(legals).build())
                .checkList(business.getCheckList().stream().map(i -> mapper.map(i, CheckListItem.class)).toList())
                .tips(business.getTips())
                .visible(true)
                .createAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        var saved = repository.save(b);

        if(saved.getCategory() != null){
            categoryRepository.incrementAffiliation(saved.getCategory().getId());
        }

        metricsService.incrementNewBusiness(saved.getId());

        return mapper.map(saved, BusinessSummaryDTO.class);
    }

    @Transactional
    public List<BusinessSummaryDTO> createAll(List<BusinessRequestDTO> businesses) {
        return repository.saveAll(businesses.stream().map(business -> {
            Category category = categoryRepository.findByName(business.getCategory());

            var legals = business.getLegalStructure()
                    .getRequirements().stream()
                    .map(r -> mapper.map(r, Legal.class))
                    .map(l -> legalRepository.getReferenceById(l.getId()))
                    .toList();

            return Business.builder()
                    .name(business.getName())
                    .category(category)
                    .description(business.getDescription())
                    .imageUrl(business.getImageUrl())
                    .initialInvestment(business.getInitialInvestment())
                    .monthlyProfit(business.getMonthlyProfit())
                    .profitMargin(business.getProfitMargin())
                    .isFeatured(business.isFeatured())
                    .ricks(business.getRicks().stream().map(r -> mapper.map(r, Risk.class)).toList())
                    .legalStructure(mapper.map(business.getLegalStructure(), LegalStructure.class).toBuilder().requirements(legals).build())
                    .checkList(business.getCheckList().stream().map(i -> mapper.map(i, CheckListItem.class)).toList())
                    .tips(business.getTips())
                    .visible(true)
                    .createAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                    .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                    .build();
        }).toList()).stream().map(m -> mapper.map(m, BusinessSummaryDTO.class)).toList();
    }


    public BusinessSummaryDTO update(String id, BusinessRequestDTO business) {
        Business current = repository.findOne(BusinessSpecification.idOrName(id)).orElseThrow(() -> notFound(id));

        var legals = business.getLegalStructure()
                .getRequirements().stream()
                .map(r -> mapper.map(r, Legal.class))
                .map(l -> legalRepository.getReferenceById(l.getId()))
                .toList();

        var b = current.toBuilder()
                .name(business.getName())
                .category(categoryRepository.findByName(business.getCategory()))
                .description(business.getDescription())
                .imageUrl(business.getImageUrl())
                .initialInvestment(business.getInitialInvestment())
                .monthlyProfit(business.getMonthlyProfit())
                .profitMargin(business.getProfitMargin())
                .isFeatured(business.isFeatured())
                .legalStructure(mapper.map(business.getLegalStructure(), LegalStructure.class).toBuilder().requirements(legals).build())
                .ricks(business.getRicks().stream().map(r -> mapper.map(r, Risk.class)).toList())
                .checkList(business.getCheckList().stream().map(i -> mapper.map(i, CheckListItem.class)).toList())
                .tips(business.getTips())
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        return mapper.map(repository.save(b), BusinessSummaryDTO.class);
    }

    @Transactional
    public List<BusinessSummaryDTO> updateAll(List<BusinessWithIdRequestDTO> businessesRequests) {
        var _businessesIds = businessesRequests.stream().map(BusinessWithIdRequestDTO::getId).toList();

        List<Business> businesses = repository.findAllById(_businessesIds).stream().map(b -> {
            BusinessWithIdRequestDTO current = businessesRequests.stream().filter(bRequest -> Objects.equals(bRequest.getId(), b.getId())).findFirst().orElseThrow(() -> new RuntimeException("Business request not found"));

            var legals = current.getLegalStructure()
                    .getRequirements().stream()
                    .map(r -> mapper.map(r, Legal.class))
                    .map(l -> legalRepository.getReferenceById(l.getId()))
                    .toList();

            return b.toBuilder()
                    .name(current.getName())
                    .category(categoryRepository.findByName(current.getCategory()))
                    .description(current.getDescription())
                    .imageUrl(current.getImageUrl())
                    .initialInvestment(current.getInitialInvestment())
                    .monthlyProfit(current.getMonthlyProfit())
                    .profitMargin(current.getProfitMargin())
                    .isFeatured(current.isFeatured())
                    .legalStructure(mapper.map(current.getLegalStructure(), LegalStructure.class).toBuilder().requirements(legals).build())
                    .ricks(current.getRicks().stream().map(r -> mapper.map(r, Risk.class)).toList())
                    .checkList(current.getCheckList().stream().map(i -> mapper.map(i, CheckListItem.class)).toList())
                    .tips(current.getTips())
                    .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                    .build();
        }).toList();
        return repository.saveAll(businesses).stream().map(m -> mapper.map(m, BusinessSummaryDTO.class)).toList();
    }


    public void delete(String id) {
        boolean exist = repository.existByIdOrName(id);
        if (exist) {
            repository.setDeleted(id, true);
            return;
        }
        throw notFound(id);
    }

    public BusinessSummaryDTO read(String id) {
        return mapper.map(businessWithInflation(repository.findOne(BusinessSpecification.idOrName(id)).orElseThrow(() -> notFound(id))), BusinessSummaryDTO.class);
    }

    public Page<BusinessSummaryDTO> readAll(Pageable pageable) {
        return repository.findAll(pageable).map(b -> mapper.map(businessWithInflation(b), BusinessSummaryDTO.class));
    }

    public List<BusinessSummaryDTO> readAllByIds(List<Long> ids) {
        return repository.findAllById(ids).stream().map(b -> mapper.map(businessWithInflation(b), BusinessSummaryDTO.class)).toList();
    }

    public BusinessDetailDTO readDetail(String id) {
        return mapper.map(repository.findOne(BusinessSpecification.idOrName(id)).orElseThrow(() -> notFound(id)), BusinessDetailDTO.class);
    }

    public Page<BusinessDetailDTO> readAllDetail(Pageable pageable) {
        return repository.findAll(pageable).map(b -> mapper.map(b, BusinessDetailDTO.class));
    }

    public void addItemToCheckListByBusinessId(String id, CheckListItem item) {
        Business b_ = repository.findOne(BusinessSpecification.idOrName(id)).orElseThrow(() -> notFound(id));
        var checkList = b_.getCheckList();
        checkList.add(item);
        repository.save(b_.toBuilder().checkList(checkList).build());
    }

    public void removeItemToCheckListByBusinessId(String id, Long checkListItemId) {
        Business b_ = repository.findOne(BusinessSpecification.idOrName(id)).orElseThrow(() -> notFound(id));
        repository.save(b_.toBuilder().checkList(b_.getCheckList().stream().filter(i -> !Objects.equals(i.getId(), checkListItemId)).toList()).build());
    }


    public Page<BusinessSummaryDTO> search(String term, Pageable pageable) {
        try {
            List<Long> ids = luceneService.search(
                            term,
                            new String[]{"name", "description"},
                            Map.of("name", 2f, "description", 1f),
                            100,
                            false
                    ).stream()
                    .map(map -> Long.parseLong(map.get("id")))
                    .distinct()
                    .toList();

            if (ids.isEmpty()) {
                return Page.empty(pageable);
            }

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), ids.size());

            if (start >= ids.size()) {
                return Page.empty(pageable);
            }

            List<Long> pagedIds = ids.subList(start, end);

            List<Business> entities = repository.findAllById(pagedIds);

            Map<Long, Business> map = entities.stream()
                    .collect(Collectors.toMap(Business::getId, e -> e));

            List<BusinessSummaryDTO> content = pagedIds.stream()
                    .map(map::get)
                    .filter(Objects::nonNull)
                    .map(b -> mapper.map(businessWithInflation(b), BusinessSummaryDTO.class))
                    .toList();

            return new PageImpl<>(content, pageable, ids.size());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void indexer() {
        var business = repository.findAll();
        try {
            luceneService.index(business.stream().map(b -> indexer.toDocument(b)).toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseStatusException notFound(String id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "The Business by id %s is not found".formatted(id));
    }

    private Business businessWithInflation(Business business) {
        BigDecimal cem = BigDecimal.valueOf(100);
        BigDecimal um  = BigDecimal.ONE;

        BigDecimal ipca = new BigDecimal(indicadoresS.getIPCA().valor())
                .divide(cem, 6, RoundingMode.HALF_UP);

        BigDecimal valorInvestidoCorrigido = business.getInitialInvestment()
                .multiply(um.add(ipca))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal valorLucroCorrigido = business.getMonthlyProfit()
                .divide(um.add(ipca), 2, RoundingMode.HALF_UP);

        return business.toBuilder()
                .inflation(
                        BusinessInflation.builder()
                                .growth(ipca)
                                .initialInvestment(valorInvestidoCorrigido)
                                .monthlyProfit(valorLucroCorrigido)
                                .build())
                .build();
    }

    @Scheduled(cron = "0 0 0 * * SUN")
    @Transactional
    public void businessRanking() {
        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("America/Sao_Paulo"));

        int week = now.get(WeekFields.ISO.weekOfWeekBasedYear());
        int year = now.get(WeekFields.ISO.weekBasedYear());

        String weekKey = year + "-W" + String.format("%02d", week);

        Set<ZSetOperations.TypedTuple<Object>> top =
                redis.opsForZSet().reverseRangeWithScores(
                        FEATURE_PREFIX + ":click:week:" + weekKey,
                        0, 8
                );

        if (top == null || top.isEmpty()) {
            return;
        }

        Set<Long> topBusinessIds = top.stream()
                .map(t -> t.getValue().toString())
                .map(v -> v.replace("business:", ""))
                .map(Long::valueOf)
                .collect(Collectors.toSet());

        repository.removeFeaturedOutsideTop(topBusinessIds);
        repository.markAsFeatured(topBusinessIds);

        redis.delete(weekKey);
    }
}
