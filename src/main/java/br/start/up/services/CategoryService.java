package br.start.up.services;

import br.start.up.dtos.request.CategoryRequestDTO;
import br.start.up.dtos.summary.CategorySummaryDTO;
import br.start.up.model.Category;
import br.start.up.repository.CategoryRepository;
import br.start.up.specification.CategorySpecification;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private ModelMapper mapper;

    public CategorySummaryDTO create(CategoryRequestDTO category) {
        Category ca = Category.builder()
                .name(category.getName())
                .growthRate(category.getGrowthRate())
                .imageUrl(category.getImageUrl())
                .createAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        return mapper.map(repository.save(ca), CategorySummaryDTO.class);
    }

    public List<CategorySummaryDTO> createAll(List<CategoryRequestDTO> categories){
        return repository.saveAll(categories.stream().map(category ->
             Category.builder()
                    .name(category.getName())
                     .growthRate(category.getGrowthRate())
                    .imageUrl(category.getImageUrl())
                    .createAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                    .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                    .build()
        ).toList()).stream().map(m -> mapper.map(m, CategorySummaryDTO.class)).toList();
    }

    public CategorySummaryDTO update(String id, CategoryRequestDTO category) {
        Category ca_ = repository.findOne(CategorySpecification.idOrName(id)).orElseThrow(() -> notFound(id));

        Category ca = ca_.toBuilder()
                .growthRate(category.getGrowthRate())
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        return mapper.map(repository.save(ca), CategorySummaryDTO.class);
    }

    public List<CategorySummaryDTO> updateAll(List<CategoryRequestDTO> categories){
        ArrayList<Category> _categories = new ArrayList<>();

        for(CategoryRequestDTO category: categories){
            try {
                Category ca_ = repository.findOne(CategorySpecification.idOrName(category.getName())).orElseThrow(() -> notFound(category.getName()));

                Category ca = ca_.toBuilder()
                        .name(category.getName())
                        .growthRate(category.getGrowthRate())
                        .imageUrl(category.getImageUrl())
                        .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                        .build();

                _categories.add(repository.save(ca));
            } catch (Exception ex){
                System.err.println(ex.getMessage());
            }
        }

        return _categories.stream().map(m -> mapper.map(m, CategorySummaryDTO.class)).toList();
    }

    public CategorySummaryDTO read(String id) {
        return mapper.map(repository.findOne(CategorySpecification.idOrName(id)).orElseThrow(() -> notFound(id)), CategorySummaryDTO.class);
    }

    public Page<CategorySummaryDTO> readAll(Pageable pageable) {
        return repository.findAll(pageable).map(c -> mapper.map(c, CategorySummaryDTO.class));
    }


    public Page<CategorySummaryDTO> search(String query, Pageable pageable) {
        return repository.search(query, pageable).map(q -> mapper.map(q, CategorySummaryDTO.class));
    }

    private ResponseStatusException notFound(String id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "The Category by id %s not found".formatted(id));
    }
}
