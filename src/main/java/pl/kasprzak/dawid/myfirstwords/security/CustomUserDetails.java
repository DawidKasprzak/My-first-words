package pl.kasprzak.dawid.myfirstwords.security;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetailsService {

    private final ParentsRepository parentsRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ParentEntity parentEntity = parentsRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));

        List<SimpleGrantedAuthority> authorities = parentEntity.getAuthorities()
                .stream()
                .map(authorityEntity -> new SimpleGrantedAuthority(authorityEntity.getAuthority()))
                .toList();

        return User.builder()
                .username(username)
                .password(parentEntity.getPassword())
                .authorities(authorities)
                .build();

    }
}
