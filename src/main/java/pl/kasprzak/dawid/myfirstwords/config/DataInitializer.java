package pl.kasprzak.dawid.myfirstwords.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.kasprzak.dawid.myfirstwords.repository.AuthoritiesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.AuthorityEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ParentsRepository parentsRepository;
    private final AuthoritiesRepository authoritiesRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {
        if(parentsRepository.findByUsername("admin").isEmpty()){
            ParentEntity admin = new ParentEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminPass"));

            AuthorityEntity adminAuthority = new AuthorityEntity();
            adminAuthority.setAuthority("ROLE_ADMIN");

            authoritiesRepository.save(adminAuthority);

            admin.setAuthorities(List.of(adminAuthority));

            parentsRepository.save(admin);
        }


    }
}
