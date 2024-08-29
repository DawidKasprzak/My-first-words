package pl.kasprzak.dawid.myfirstwords.service.milestones;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.*;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;
import pl.kasprzak.dawid.myfirstwords.model.milestones.GetAllMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.model.milestones.GetMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.milestones.GetMilestoneConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetMilestoneService {

    private final MilestonesRepository milestonesRepository;
    private final GetMilestoneConverter getMilestoneConverter;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Service method for retrieving milestones for a child that were achieved before the given date.
     * This method validates and authorizes the parent using the AuthorizationHelper,
     * and fetches milestones achieved before the specified date.
     *
     * @param childId        the ID of the child whose milestones are to be retrieved.
     * @param date           the date before which milestones were achieved.
     * @return a list of GetMilestoneResponse DTOs containing the milestones achieved before the given date.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     */
    public List<GetMilestoneResponse> getByDateAchieveBefore(Long childId, LocalDate date) {
        authorizationHelper.validateAndAuthorizeChild(childId);
        List<MilestoneEntity> milestones = milestonesRepository.findByChildIdAndDateAchieveBefore(childId, date);
        return milestones.stream()
                .map(getMilestoneConverter::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Service method for retrieving milestones for a child that were achieved after the given date.
     * This method validates and authorizes the parent using the AuthorizationHelper,
     * and fetches milestone achieved after the specified date.
     *
     * @param childId        the ID of the child whose milestones are to be retrieved.
     * @param date           the date before which milestones were achieved.
     * @return a list of GetMilestoneResponse DTOs containing the milestones achieved after the given date.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     */
    public List<GetMilestoneResponse> getByDateAchieveAfter(Long childId, LocalDate date) {
        authorizationHelper.validateAndAuthorizeChild(childId);
        List<MilestoneEntity> milestones = milestonesRepository.findByChildIdAndDateAchieveAfter(childId, date);
        return milestones.stream()
                .map(getMilestoneConverter::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Service method for retrieving milestones for a child that were achieved between the given dates.
     * This method validates and authorizes the parent using the AuthorizationHelper,
     * and fetches milestones achieved between the specified dates.
     *
     * @param childId        the ID of the child whose milestones are to be retrieved.
     * @param startDate      the start date of the range.
     * @param endDate        the end date of the range.
     * @return a list of GetMilestoneResponse DTOs containing the milestones achieved between the given dates.
     * @throws ParentNotFoundException   if the authenticated parent is not found.
     * @throws ChildNotFoundException    if the child with the given ID is not found.
     * @throws AccessDeniedException     if the authenticated parent does not have access to the child.
     * @throws DateValidationException   if either start date or end date is null.
     * @throws InvalidDateOrderException if the start date is after the end date.
     */
    public List<GetMilestoneResponse> getMilestonesBetweenDays(Long childId, LocalDate startDate, LocalDate endDate) {
        authorizationHelper.validateAndAuthorizeChild(childId);
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

    /**
     * Service method for retrieving all milestones for a child.
     * This method validates and authorizes the parent using the AuthorizationHelper,
     * and fetches all milestones for the specified child.
     *
     * @param childId        the ID of the child whose milestones are to be retrieved.
     * @return a GetAllMilestoneResponse DTO containing a list of all milestones for the child.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     */
    public GetAllMilestoneResponse getAllMilestone(Long childId) {
        authorizationHelper.validateAndAuthorizeChild(childId);
        return GetAllMilestoneResponse.builder()
                .milestones(milestonesRepository.findAllByChildId(childId).stream()
                        .map(getMilestoneConverter::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Service method for retrieving milestones for a child by the given title.
     * This method validates and authorizes the parent using the AuthorizationHelper,
     * and fetches milestones for the specified child that match the given title.
     *
     * @param childId        the ID of the child whose milestones are to be retrieved.
     * @param title          the title to search for milestones.
     * @return a GetAllMilestoneResponse DTO containing the list of milestone details.
     * @throws ParentNotFoundException    if the authenticated parent is not found.
     * @throws ChildNotFoundException     if the child with the given ID is not found.
     * @throws AccessDeniedException      if the authenticated parent does not have access to the child.
     * @throws MilestoneNotFoundException if no milestones are found with the given title for the specified child.
     */
    public GetAllMilestoneResponse getByTitle(Long childId, String title) {
        authorizationHelper.validateAndAuthorizeChild(childId);
        List<MilestoneEntity> milestones = milestonesRepository.findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childId);
        if (milestones.isEmpty()) {
            throw new MilestoneNotFoundException("Milestone not found");
        }
        return GetAllMilestoneResponse.builder()
                .milestones(milestones.stream()
                        .map(getMilestoneConverter::toDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
