package br.start.up.detail;

import br.start.up.model.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetail implements UserDetails {

    private Account account;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>(account.getAuthorities().stream().map(SimpleGrantedAuthority::new).toList());
        authorities.add(new SimpleGrantedAuthority(account.getRole()));
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return Optional.of(account.isActive()).orElse(true);
    }

    @Override
    public boolean isAccountNonLocked() {
        return Optional.of(account.isActive()).orElse(true);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return Optional.of(account.isActive()).orElse(true);
    }

    @Override
    public boolean isEnabled() {
        return Optional.of(account.isActive()).orElse(true);
    }
}
