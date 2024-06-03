package pl.kasprzak.dawid.myfirstwords.service.milestones;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.service.DateRangeGeneralService;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;
import pl.kasprzak.dawid.myfirstwords.model.milestones.GetAllMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.model.milestones.GetMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.milestones.GetMilestoneConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetMilestoneService {

    private final MilestonesRepository milestonesRepository;
    private final GetMilestoneConverter getMilestoneConverter;
    private final AuthorizationHelper authorizationHelper;
    private final DateRangeGeneralService dateRangeGeneralService;


    public List<GetMilestoneResponse> getByDateAchieveBefore(Long childId, LocalDate date, Authentication authentication) {
        List<MilestoneEntity> milestone = dateRangeGeneralService.getByDateAchieveBefore(childId, date, authentication,
                milestonesRepository::findByChildIdAndDateAchieveBefore);
        return milestone.stream()
                .map(getMilestoneConverter::toDto)
                .collect(Collectors.toList());
    }

    public List<GetMilestoneResponse> getByDateAchieveAfter(Long childId, LocalDate date, Authentication authentication) {
        List<MilestoneEntity> milestone = dateRangeGeneralService.getByDateAchieveAfter(childId, date, authentication,
                milestonesRepository::findByChildIdAndDateAchieveAfter);
        return milestone.stream()
                .map(getMilestoneConverter::toDto)
                .collect(Collectors.toList());
    }

    public List<GetMilestoneResponse> getWordsBetweenDays(Long childId, LocalDate startDate, LocalDate endDate, Authentication authentication) {
        List<MilestoneEntity> milestone = dateRangeGeneralService.getWordsBetweenDays(childId, startDate, endDate, authentication,
                milestonesRepository::findByChildIdAndDateAchieveBetween);
        return milestone.stream()
                .map(getMilestoneConverter::toDto)
                .collect(Collectors.toList());
    }

    public GetAllMilestoneResponse getAllMilestone(Long childId, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        List<MilestoneEntity> milestones = milestonesRepository.findByChildId(childId);
        return GetAllMilestoneResponse.builder()
                .milestones(milestones.stream()
                        .map(getMilestoneConverter::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public final GetMilestoneResponse getByTitle(String title) {
        return milestonesRepository.findByTitleContaining(title)
                .map(getMilestoneConverter::toDto)
                .orElseThrow(() -> new NoSuchElementException("Milestone not found"));
    }
}
