package pl.kasprzak.dawid.myfirstwords.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final ParentsRepository parentsRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.user.name}")
    private String adminName;
    @Value("${spring.security.user.password}")
    private String password;
    @Value("${spring.security.user.roles}")
    private String roles;

    @Override
    public void run(String... args) throws Exception {


        if (!parentsRepository.existsByUsername(adminName)) {
            ParentEntity admin = new ParentEntity();
            admin.setUsername(adminName);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setAuthority(roles);
            parentsRepository.save(admin);
        }
    }
}
