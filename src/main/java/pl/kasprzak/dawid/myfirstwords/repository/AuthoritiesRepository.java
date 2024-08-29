package pl.kasprzak.dawid.myfirstwords.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.AuthorityEntity;

import java.util.Optional;

public interface AuthoritiesRepository extends JpaRepository<AuthorityEntity, Long> {

    Optional<AuthorityEntity> findByAuthority(String authorityName);
}
