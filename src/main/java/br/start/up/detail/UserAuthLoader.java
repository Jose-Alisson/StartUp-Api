package br.start.up.detail;

import br.start.up.dtos.cache.AuthCacheDTO;

public interface UserAuthLoader {
    AuthCacheDTO loadUserById(Long id);
}
