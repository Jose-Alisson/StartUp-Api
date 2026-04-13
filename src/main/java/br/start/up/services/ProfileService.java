package br.start.up.services;

import br.start.up.dtos.request.ProfileRequestDTO;
import br.start.up.dtos.summary.ProfileSummaryDTO;
import br.start.up.model.Profile;
import br.start.up.repository.ProfileRepository;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository repository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private Authentication authentication;

    public ProfileSummaryDTO update(String email, ProfileRequestDTO requestDTO){
        Profile profile = repository.findProfileByEmail(email).orElseThrow(() -> notFound(email));

        Profile profile_ = profile.toBuilder()
                .username(requestDTO.getUsername())
                .cellphone(requestDTO.getCellphone())
                .build();

        return mapper.map(repository.save(profile_), ProfileSummaryDTO.class);
    }

    public ProfileSummaryDTO addFavorite(String id){
        String principal = (String) authentication.getPrincipal();

        Profile profile = repository.findProfileByEmail(principal).orElseThrow(() -> notFound(principal));

        ArrayList<String> favorite = new ArrayList<>(profile.getFavoriteBusinessIds());
        favorite.add(id);

        Profile profile_ = profile
                .toBuilder()
                .favoriteBusinessIds(favorite)
                .build();

        return mapper.map(repository.save(profile_), ProfileSummaryDTO.class);
    }

    public ProfileSummaryDTO removeFavorite(String id){
        String principal = (String) authentication.getPrincipal();

        Profile profile = repository.findProfileByEmail(principal).orElseThrow(() -> notFound(principal));

        Profile profile_ = profile
                .toBuilder()
                .favoriteBusinessIds(profile.getFavoriteBusinessIds().stream().filter(o -> !o.equals(id)).toList())
                .build();

        return mapper.map(repository.save(profile_), ProfileSummaryDTO.class);
    }

    private ResponseStatusException notFound(String email) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "The Account by email %s not found".formatted(email));
    }

    @Transactional
    public String saveIcon(MultipartFile file) {
        String principal = (String) authentication.getPrincipal();

        var extension = Optional.ofNullable(file.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".") + 1))
                .orElse("");

        var imageUrl = fileService.upload("/public", principal + "-icon." + extension, file);

        repository.updateImageUrl(imageUrl, principal);

        return imageUrl;
    }

    public ProfileSummaryDTO read() {
        return mapper.map(repository.findProfileByEmail((String) authentication.getPrincipal()).orElseThrow(), ProfileSummaryDTO.class);
    }
}
