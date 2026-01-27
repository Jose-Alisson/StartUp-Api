package br.start.up.services;

import br.start.up.dtos.request.CategoryRequestDTO;
import br.start.up.dtos.summary.CategorySummaryDTO;
import br.start.up.model.Category;
import br.start.up.repository.CategoryRepository;
import br.start.up.specification.CategorySpecification;
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

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private ModelMapper mapper;

    public CategorySummaryDTO create(CategoryRequestDTO category) {
        Category ca = Category.builder()
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .createAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        return mapper.map(repository.save(ca), CategorySummaryDTO.class);
    }

    public CategorySummaryDTO update(String id, CategoryRequestDTO category) {
        Category ca_ = repository.findOne(CategorySpecification.idOrName(id)).orElseThrow(() -> notFound(id));

        Category ca = ca_.toBuilder()
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        return mapper.map(repository.save(ca), CategorySummaryDTO.class);
    }

    public CategorySummaryDTO read(String id) {
        return mapper.map(repository.findOne(CategorySpecification.idOrName(id)).orElseThrow(() -> notFound(id)), CategorySummaryDTO.class);
    }

    public Page<CategorySummaryDTO> readAll(Pageable pageable) {
        return repository.findAll(pageable).map(c -> mapper.map(c, CategorySummaryDTO.class));
    }

    private ResponseStatusException notFound(String id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "The Category by id %s not found".formatted(id));
    }
}
