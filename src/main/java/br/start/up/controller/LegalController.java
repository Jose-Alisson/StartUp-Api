package br.start.up.controller;

import br.start.up.dtos.request.LegalRequestDTO;
import br.start.up.dtos.request.LegalRequestWithIdDTO;
import br.start.up.dtos.summary.CategorySummaryDTO;
import br.start.up.dtos.summary.LegalSummaryDTO;
import br.start.up.services.LegalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/legals")
public class LegalController {

    @Autowired
    private LegalService service;

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @PostMapping("/create")
    public LegalSummaryDTO createLegal(@Valid @RequestBody LegalRequestDTO legal){
        return service.create(legal);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @PostMapping("/create-all")
    public List<LegalSummaryDTO> createAllLegal(@Valid @RequestBody List<LegalRequestDTO> legals){
        return service.createAll(legals);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @PutMapping("/{id}/update")
    public LegalSummaryDTO createUpdate(@PathVariable("id") String irOrName, @Valid @RequestBody LegalRequestDTO legal){
        return service.update(irOrName, legal);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @PutMapping("/update-all")
    public List<LegalSummaryDTO> updateAllLegal(@RequestBody List<@Valid LegalRequestWithIdDTO> legals){
        return service.updateAll(legals);
    }

    @GetMapping("/{id}/")
    public LegalSummaryDTO read(@PathVariable String idOrName){
        return service.read(idOrName);
    }

    @GetMapping("/")
    public Page<LegalSummaryDTO> read(Pageable pageable){
        return service.readAll(pageable);
    }

    @GetMapping("/search")
    public Page<LegalSummaryDTO> search(
            @RequestParam(value = "term", required = false)
            String term, Pageable pageable){

        if(term != null && !term.isBlank()){
            return service.search(term, pageable);
        }
        return service.readAll(pageable);
    }

}
