package pl.kasprzak.dawid.myfirstwords.repository;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WordsRepository extends JpaRepository<WordEntity, Long> {

    Optional<WordEntity> findByWordIgnoreCaseAndChildId(String word, Long childId);

    Optional findByChildIdAndId(Long childId, Long id);

    List<WordEntity> findAllByChildId(Long childId);

    List<WordEntity> findByChildIdAndDateAchieveAfter(Long childId, LocalDate date);

    List<WordEntity> findByChildIdAndDateAchieveBefore(Long childId, LocalDate date);

    List<WordEntity> findByChildIdAndDateAchieveBetween(Long childId, LocalDate startDate, LocalDate endDate);


}
