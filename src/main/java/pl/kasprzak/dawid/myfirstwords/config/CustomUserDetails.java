package pl.kasprzak.dawid.myfirstwords.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

@Component
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetailsService {

    private final ParentsRepository parentsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ParentEntity parentEntity = parentsRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("user not found"));
        return User.builder()
                .username(username)
                .password(parentEntity.getPassword())
                .authorities("Role_User")
                .build();

    }
}
