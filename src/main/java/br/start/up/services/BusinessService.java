package br.start.up.services;

import br.start.up.dtos.detail.BusinessDetailDTO;
import br.start.up.dtos.request.BusinessRequestDTO;
import br.start.up.dtos.summary.BusinessSummaryDTO;
import br.start.up.model.*;
import br.start.up.repository.BusinessRepository;
import br.start.up.repository.CategoryRepository;
import br.start.up.specification.BusinessSpecification;
import br.start.up.utils.OptionalCascade;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Service
public class BusinessService {

    @Autowired
    private BusinessRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper mapper;

    public BusinessSummaryDTO create(BusinessRequestDTO business) {
        var bCategory = business.getCategory();

        var b = Business.builder()
                .name(business.getName())
                .category(categoryRepository.findByName(business.getName()))
                .description(business.getDescription())
                .imageUrl(business.getImageUrl())
                .initialInvestment(business.getInitialInvestment())
                .monthlyProfit(business.getMonthlyProfit())
                .profitMargin(business.getProfitMargin())
                .isFeatured(business.isFeatured())
                .ricks(business.getRicks().stream().map(r -> mapper.map(r, Risk.class)).toList())
                .legalStructure(mapper.map(business.getLegalStructure(), LegalStructure.class))
                .checkList(business.getCheckList().stream().map(i -> mapper.map(i, CheckListItem.class)).toList())
                .tips(business.getTips())
                .moreData(business.getMoreData())
                .visible(true)
                .createAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        return  mapper.map(repository.save(b), BusinessSummaryDTO.class);
    }


    public BusinessSummaryDTO update(String id, BusinessRequestDTO business) {
        Business current = repository.findOne(BusinessSpecification.idOrName(id)).orElseThrow(() -> notFound(id));

        var b = current.toBuilder()
                .name(business.getName())
                .category(categoryRepository.findByName(business.getCategory()))
                .description(business.getDescription())
                .imageUrl(business.getImageUrl())
                .initialInvestment(business.getInitialInvestment())
                .monthlyProfit(business.getMonthlyProfit())
                .profitMargin(business.getProfitMargin())
                .isFeatured(business.isFeatured())
                .legalStructure(mapper.map(business.getLegalStructure(), LegalStructure.class))
                .ricks(business.getRicks().stream().map(r -> mapper.map(r, Risk.class)).toList())
                .checkList(business.getCheckList().stream().map(i -> mapper.map(i, CheckListItem.class)).toList())
                .moreData(business.getMoreData())
                .tips(business.getTips())
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        return mapper.map(repository.save(b), BusinessSummaryDTO.class);
    }


    public void delete(String id){
        boolean exist = repository.existByIdOrName(id);
        if(exist){
            repository.setDeleted(id, true);
        }
        throw notFound(id);
    }

    public BusinessSummaryDTO read(String id) {
        return mapper.map(repository.findOne(BusinessSpecification.idOrName(id)).orElseThrow(() -> notFound(id)), BusinessSummaryDTO.class);
    }

    public Page<BusinessSummaryDTO> readAll(Pageable pageable) {
        return repository.findAll(pageable).map(b -> mapper.map(b, BusinessSummaryDTO.class));
    }

    public BusinessDetailDTO readDetail(String id) {
        return mapper.map(repository.findOne(BusinessSpecification.idOrName(id)).orElseThrow(() -> notFound(id)), BusinessDetailDTO.class);
    }

    public Page<BusinessDetailDTO> readAllDetail(Pageable pageable) {
        return repository.findAll(pageable).map(b -> mapper.map(b, BusinessDetailDTO.class));
    }

    public void addItemToCheckListByBusinessId(String id, CheckListItem item){
        Business b_ = repository.findOne(BusinessSpecification.idOrName(id)).orElseThrow(() -> notFound(id));
        var checkList = b_.getCheckList();
        checkList.add(item);
        repository.save(b_.toBuilder().checkList(checkList).build());
    }

    public void removeItemToCheckListByBusinessId(String id, Long checkListItemId){
        Business b_ = repository.findOne(BusinessSpecification.idOrName(id)).orElseThrow(() -> notFound(id));
        repository.save(b_.toBuilder().checkList(b_.getCheckList().stream().filter(i -> !Objects.equals(i.getId(), checkListItemId)).toList()).build());
    }

    private ResponseStatusException notFound(String id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "The Business by id %s is not found".formatted(id));
    }
}
