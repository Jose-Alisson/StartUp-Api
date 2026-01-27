package br.start.up.services;

import br.start.up.dtos.request.LegalRequestDTO;
import br.start.up.dtos.summary.LegalSummaryDTO;
import br.start.up.enums.LegalType;
import br.start.up.model.Legal;
import br.start.up.repository.LegalRepository;
import br.start.up.specification.LegalSpecification;
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
public class LegalService {

    @Autowired
    private LegalRepository repository;

    @Autowired
    private ModelMapper mapper;

    public LegalSummaryDTO create(LegalRequestDTO legal){
        Legal l_ = Legal.builder()
                .name(legal.getName())
                .type(LegalType.valueOf(legal.getType()))
                .description(legal.getDescription())
                .mandatory(legal.isMandatory())
                .createAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        return mapper.map(repository.save(l_), LegalSummaryDTO.class);
    }

    public LegalSummaryDTO update(String idOrName ,LegalRequestDTO legal){
        Legal l_ = repository.findOne(LegalSpecification.idOrName(idOrName)).orElseThrow(() -> notFound(idOrName));

        Legal legal_ = l_.toBuilder()
                .name(legal.getName())
                .type(LegalType.valueOf(legal.getType()))
                .description(legal.getDescription())
                .mandatory(legal.isMandatory())
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        return mapper.map(repository.save(legal_), LegalSummaryDTO.class);
    }

    public LegalSummaryDTO read(String idOrName){
        return mapper.map(repository.findOne(LegalSpecification.idOrName(idOrName)).orElseThrow(() -> notFound(idOrName)), LegalSummaryDTO.class);
    }

    public Page<LegalSummaryDTO> readAll(Pageable pageable){
        return repository.findAll(pageable).map(l -> mapper.map(l, LegalSummaryDTO.class));
    }

    private ResponseStatusException notFound(String id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "The Legal by id or name %s is not found".formatted(id));
    }
}
