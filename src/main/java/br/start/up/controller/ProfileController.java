package br.start.up.controller;

import br.start.up.dtos.request.ProfileRequestDTO;
import br.start.up.services.ProfileService;
import jakarta.validation.Valid;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    @Autowired
    public ProfileService service;

    @PutMapping("/{email}/update")
    @PreAuthorize("hasAnyAuthority('admin', 'manager') or #email == authentication.name")
    public ResponseEntity<?> update(@PathVariable("email") String email, @Valid @RequestBody ProfileRequestDTO requestDTO){
        return ResponseEntity.ok(service.update(email, requestDTO));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(){
        return ResponseEntity.ok(service.read());
    }

    @PatchMapping("/add-favorite-business")
    public ResponseEntity<?> addFavorite(@RequestParam("id") String id){
        return ResponseEntity.ok(service.addFavorite(id));
    }

    @PatchMapping("/remove-favorite-business")
    public ResponseEntity<?> removeFavorite(@RequestParam("id") String id){
        return ResponseEntity.ok(service.removeFavorite(id));
    }

    @PostMapping({"/icon", "/icon/send"})
    public ResponseEntity<?> uploadIcon(@RequestParam("file") MultipartFile file){
        var message = new HashMap<>();
        message.put("message", "tipo de arquivo não compativel ou excede 5MB");

        try {
            Tika tika = new Tika();
            String type = tika.detect(file.getInputStream());
            if(List.of("image/png", "image/jpeg", "image/webp").contains(type) && file.getSize() < 5_000_000){
                return ResponseEntity.ok(service.saveIcon(file));
            } else {
                return ResponseEntity.badRequest().body(message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
