package pl.kasprzak.dawid.myfirstwords.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

import java.util.Optional;

public interface ParentsRepository extends JpaRepository<ParentEntity, Long> {
    Optional<ParentEntity> findByUsername(String username);
}
