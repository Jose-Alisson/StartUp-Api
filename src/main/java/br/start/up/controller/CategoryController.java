package br.start.up.controller;

import br.start.up.dtos.request.CategoryRequestDTO;
import br.start.up.dtos.summary.CategorySummaryDTO;
import br.start.up.model.Category;
import br.start.up.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @PostMapping("/create")
    public CategorySummaryDTO createCategory(@Valid @RequestBody CategoryRequestDTO category){
        return service.create(category);
    }

    @PutMapping("/{id}/update")
    public CategorySummaryDTO updateCategory(String id, @Valid @RequestBody CategoryRequestDTO category){
        return service.update(id, category);
    }

    @GetMapping("/{id}/")
    public CategorySummaryDTO readCategory(String id){
        return service.read(id);
    }

    @GetMapping("/")
    public Page<CategorySummaryDTO> readAllCategory(Pageable pageable){
        return service.readAll(pageable);
    }
}
