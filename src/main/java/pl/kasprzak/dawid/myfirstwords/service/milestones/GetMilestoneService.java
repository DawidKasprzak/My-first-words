package pl.kasprzak.dawid.myfirstwords.service.milestones;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.DateValidationException;
import pl.kasprzak.dawid.myfirstwords.exception.InvalidDateOrderException;
import pl.kasprzak.dawid.myfirstwords.exception.MilestoneNotFoundException;
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

    public List<GetMilestoneResponse> getByDateAchieveBefore(Long childId, LocalDate date, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        List<MilestoneEntity> milestones = milestonesRepository.findByChildIdAndDateAchieveBefore(childId, date);
        return milestones.stream()
                .map(getMilestoneConverter::toDto)
                .collect(Collectors.toList());
    }

    public List<GetMilestoneResponse> getByDateAchieveAfter(Long childId, LocalDate date, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        List<MilestoneEntity> milestones = milestonesRepository.findByChildIdAndDateAchieveAfter(childId, date);
        return milestones.stream()
                .map(getMilestoneConverter::toDto)
                .collect(Collectors.toList());
    }

    public List<GetMilestoneResponse> getWordsBetweenDays(Long childId, LocalDate startDate, LocalDate endDate, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        if (startDate == null || endDate == null) {
            throw new DateValidationException("Start date and end date must not be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateOrderException("Start date must be before or equal to end date");
        }
        List<MilestoneEntity> milestones = milestonesRepository.findByChildIdAndDateAchieveBetween(childId, startDate, endDate);
        return milestones.stream()
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

    public GetMilestoneResponse getByTitle(Long childId, String title, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        return milestonesRepository.findByTitleContaining(title)
                .map(getMilestoneConverter::toDto)
                .orElseThrow(() -> new MilestoneNotFoundException("Milestone not found"));
    }
}
