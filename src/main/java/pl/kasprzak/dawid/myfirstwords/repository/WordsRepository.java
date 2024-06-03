package pl.kasprzak.dawid.myfirstwords.repository;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WordsRepository extends JpaRepository<WordEntity, Long>, DateRangeRepository<WordEntity> {

    Optional<WordEntity> findByWord(String word);
    Void findByChildIdAndId(Long childId, Long id);
    List<WordEntity> findAllByChildId(Long childId);



}
