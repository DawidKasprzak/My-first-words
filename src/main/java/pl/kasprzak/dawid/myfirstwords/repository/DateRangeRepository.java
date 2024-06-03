package pl.kasprzak.dawid.myfirstwords.repository;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DateRangeRepository <T> {

    List<T> findByChildIdAndDateAchieveAfter(Long childId, LocalDate date);
    List<T> findByChildIdAndDateAchieveBefore(Long childId, LocalDate date);
    List<T> findByChildIdAndDateAchieveBetween(Long childId, LocalDate startDate, LocalDate endDate);
}
