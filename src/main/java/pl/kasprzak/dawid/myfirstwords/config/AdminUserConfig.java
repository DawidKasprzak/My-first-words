package pl.kasprzak.dawid.myfirstwords.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

@Configuration
@RequiredArgsConstructor
public class AdminUserConfig implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

      //  ParentEntity admin = new ParentEntity();
      //  admin.setUsername("admin");
      //  admin.setPassword(passwordEncoder.encode("password"));

    }
}
