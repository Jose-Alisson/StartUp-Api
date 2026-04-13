package br.start.up.controller;

import br.start.up.dtos.request.CategoryRequestDTO;
import br.start.up.dtos.summary.CategorySummaryDTO;
import br.start.up.model.Category;
import br.start.up.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @PostMapping("/create")
    public CategorySummaryDTO createCategory(@Valid @RequestBody CategoryRequestDTO category){
        return service.create(category);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @PostMapping("/create-all")
    public List<CategorySummaryDTO> createAllCategories(@RequestBody List<@Valid CategoryRequestDTO> categories){
        return service.createAll(categories);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @PutMapping("/{id}/update")
    public CategorySummaryDTO updateCategory(@PathVariable("id") String id, @Valid @RequestBody CategoryRequestDTO category){
        return service.update(id, category);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @PutMapping("/update-all")
    public List<CategorySummaryDTO> updateAllCategories(@Valid @RequestBody List<CategoryRequestDTO> categories){
        return service.updateAll(categories);
    }

    @GetMapping("/{id}/")
    public CategorySummaryDTO readCategory(@PathVariable("id") String id){
        return service.read(id);
    }

    @GetMapping("/")
    public Page<CategorySummaryDTO> readAllCategory(Pageable pageable){
        return service.readAll(pageable);
    }

    @GetMapping("/search")
    public Page<CategorySummaryDTO> search(@RequestParam("term") String query, Pageable pageable){
        return service.search(query, pageable);
    }
}
