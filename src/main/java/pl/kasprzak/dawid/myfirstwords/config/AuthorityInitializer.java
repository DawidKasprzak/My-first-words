package pl.kasprzak.dawid.myfirstwords.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AuthorityInitializer implements CommandLineRunner {


    @Override
    public void run(String... args) throws Exception {


    }
}
