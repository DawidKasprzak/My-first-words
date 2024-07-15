package pl.kasprzak.dawid.myfirstwords.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MilestonesRepository extends JpaRepository<MilestoneEntity, Long> {
    List<MilestoneEntity> findByTitleContainingIgnoreCaseAndChildId(String title, Long childId);

    Optional<MilestoneEntity> findByChildIdAndId(Long childId, Long milestoneId);

    List<MilestoneEntity> findAllByChildId(Long id);

    List<MilestoneEntity> findByChildIdAndDateAchieveAfter(Long childId, LocalDate date);

    List<MilestoneEntity> findByChildIdAndDateAchieveBefore(Long childId, LocalDate date);

    List<MilestoneEntity> findByChildIdAndDateAchieveBetween(Long childId, LocalDate startDate, LocalDate endDate);

}
