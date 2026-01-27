package br.start.up.services;

import br.start.up.dtos.request.GlossaryTermRequestDTO;
import br.start.up.dtos.summary.GlossaryTermSummaryDTO;
import br.start.up.model.GlossaryTerm;
import br.start.up.repository.GlossaryTermRepository;
import br.start.up.specification.GlossaryTermSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class GlossaryTermService {

    @Autowired
    private GlossaryTermRepository repository;

    private final ModelMapper mapper = new ModelMapper();

    public GlossaryTermSummaryDTO create(GlossaryTermRequestDTO term){

        GlossaryTerm term_ = GlossaryTerm.builder()
                .name(term.getName())
                .imageUrl(term.getImageUrl())
                .createAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        return mapper.map(repository.save(term_), GlossaryTermSummaryDTO.class);
    }

    public GlossaryTermSummaryDTO update(String idOrName, GlossaryTermRequestDTO term){
        GlossaryTerm term_ = repository.findOne(GlossaryTermSpecification.idOrName(idOrName)).orElseThrow(() -> notFound(idOrName));

        GlossaryTerm t_ = term_.toBuilder()
                .name(term.getName())
                .imageUrl(term.getImageUrl())
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        return mapper.map(repository.save(t_), GlossaryTermSummaryDTO.class);
    }

    public GlossaryTermSummaryDTO read(String idOrName){
        return mapper.map(repository.findOne(GlossaryTermSpecification.idOrName(idOrName)).orElseThrow(() -> notFound(idOrName)), GlossaryTermSummaryDTO.class);
    }

    public Page<GlossaryTermSummaryDTO> readAll(Pageable pageable){
        return repository.findAll(pageable).map(g -> mapper.map(g , GlossaryTermSummaryDTO.class));
    }

    public List<GlossaryTermSummaryDTO> readAllById(List<String> ids){
        return repository.findAllById(ids).stream().map(g -> mapper.map(g, GlossaryTermSummaryDTO.class)).toList();
    }

    private ResponseStatusException notFound(String id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "The Glossary Term by id or name %s is not found".formatted(id));
    }
}
