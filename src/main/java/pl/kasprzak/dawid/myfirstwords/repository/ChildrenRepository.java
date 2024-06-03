package pl.kasprzak.dawid.myfirstwords.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;

import java.util.Optional;

public interface ChildrenRepository extends JpaRepository<ChildEntity, Long> {
    Optional<ChildEntity> findByName(String name);

}
