package br.start.up.controller;

import br.start.up.dtos.request.LegalRequestDTO;
import br.start.up.dtos.summary.LegalSummaryDTO;
import br.start.up.services.LegalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/legals")
public class LegalController {

    @Autowired
    private LegalService service;

    @PostMapping("/create")
    public LegalSummaryDTO createLegal(@RequestBody LegalRequestDTO legal){
        return service.create(legal);
    }

    @PutMapping("/{id}/update")
    public LegalSummaryDTO createUpdate(@PathVariable("id") String irOrName, @RequestBody LegalRequestDTO legal){
        return service.update(irOrName, legal);
    }

    @GetMapping("/{id}/")
    public LegalSummaryDTO read(@PathVariable String idOrName){
        return service.read(idOrName);
    }

    @GetMapping("/")
    public Page<LegalSummaryDTO> read(Pageable pageable){
        return service.readAll(pageable);
    }
}
