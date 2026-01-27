package br.start.up.controller;

import br.start.up.dtos.detail.BusinessDetailDTO;
import br.start.up.dtos.request.BusinessRequestDTO;
import br.start.up.dtos.summary.BusinessSummaryDTO;
import br.start.up.model.Business;
import br.start.up.model.CheckListItem;
import br.start.up.services.BusinessService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/businesses")
public class BusinessController {

    @Autowired
    private BusinessService service;

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @PostMapping("/create")
    public BusinessSummaryDTO createBusiness(@Valid @RequestBody BusinessRequestDTO business){
        return service.create(business);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @PutMapping("/{id}/update")
    public BusinessSummaryDTO updateBusiness(@PathVariable("id") String id, @Valid @RequestBody BusinessRequestDTO business){
        return service.update(id, business);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @DeleteMapping("/{id}/delete")
    public String deleteBusiness(@PathVariable("id") String id){
        service.delete(id);
        return "The Business by %s was deleted".formatted(id);
    }

    @GetMapping({"/{id}", "/{id}/"})
    public BusinessSummaryDTO getBusiness(@PathVariable("id") String id){
        return service.read(id);
    }

    @GetMapping("/{id}/detail")
    public BusinessDetailDTO getBusinessDetail(@PathVariable("id") String id){
        return service.readDetail(id);
    }

    @GetMapping("/")
    public Page<BusinessSummaryDTO> getAllBusiness(Pageable pageable){
        return service.readAll(pageable);
    }

    @GetMapping("/detail")
    public Page<BusinessDetailDTO> getAllBusinessDetail(Pageable pageable){
        return service.readAllDetail(pageable);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @PutMapping("/{id}/add-item-to-check-list")
    public void addItemToCheckListByBusiness(@PathVariable("id") String id, @RequestBody CheckListItem item) {
        service.addItemToCheckListByBusinessId(id, item);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @DeleteMapping("/{id}/remove-item-to-check-list")
    public void removeItemToCheckListByBusiness(@PathVariable("id") String id, @RequestParam("item-id") Long itemId) {
        service.removeItemToCheckListByBusinessId(id, itemId);
    }
}
