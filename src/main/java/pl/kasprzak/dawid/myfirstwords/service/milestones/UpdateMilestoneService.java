package pl.kasprzak.dawid.myfirstwords.service.milestones;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.MilestoneNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.milestones.UpdateMilestoneRequest;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

@Service
@RequiredArgsConstructor
public class UpdateMilestoneService {

    private final AuthorizationHelper authorizationHelper;
    private final MilestonesRepository milestonesRepository;

    /**
     * Service method for updating a milestone for a given child.
     * This method validates and authorizes the parent using the AuthorizationHelper,
     * retrieves the milestone by its ID and child's ID, updates the milestone details
     * with the provided request data, and saves the updated milestone back to the repository.
     *
     * @param childId     the ID of the child whose milestone is to be updated.
     * @param milestoneId the ID of the milestone to be updated.
     * @param request     the UpdateMilestoneRequest object containing the new milestone data.
     * @return the update MilestoneEntity.
     * @throws ParentNotFoundException    if the authenticated parent is not found.
     * @throws ChildNotFoundException     if the child with the given ID is not found.
     * @throws AccessDeniedException      if the authenticated parent does not have access to the child.
     * @throws MilestoneNotFoundException if the milestone with the given ID is not found fot the specified child.
     */
    public MilestoneEntity updateMilestone(Long childId, Long milestoneId, UpdateMilestoneRequest request) {
        authorizationHelper.validateAndAuthorizeChild(childId);
        MilestoneEntity milestone = milestonesRepository.findByChildIdAndId(childId, milestoneId)
                .orElseThrow(() -> new MilestoneNotFoundException("Milestone not found"));

        milestone.setTitle(request.getTitle());
        milestone.setDescription(request.getDescription());
        milestone.setDateAchieve(request.getDateAchieve());
        return milestonesRepository.save(milestone);
    }
}
