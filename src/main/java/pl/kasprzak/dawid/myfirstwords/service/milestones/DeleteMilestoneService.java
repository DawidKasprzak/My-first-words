package pl.kasprzak.dawid.myfirstwords.service.milestones;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.MilestoneNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.AdminMissingParentIDException;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

@Service
@RequiredArgsConstructor
public class DeleteMilestoneService {

    private final MilestonesRepository milestonesRepository;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Service method for deleting a milestone identified by the given milestone ID for a specific child.
     * This method validates and authorizes the parent or admin using AuthorizationHelper.
     * If the authenticated user is a parent, the `parentID` parameter can be null,
     * and the authorization will be based on the current user's session.
     * If the authenticated user is an admin, the `parentID` parameter must be provided
     * to specify the parent associated with the child.
     * Once authorized, the method finds the milestone associated with the given child ID and milestone ID,
     * and deletes it from the repository.
     *
     * @param childId     the ID of the child to whom the milestone belongs.
     * @param milestoneId the ID of the milestone to be deleted.
     * @throws AdminMissingParentIDException if the authenticated user is an admin and the parentID is null.
     * @throws ParentNotFoundException    if the specified parent (for admin) or authenticated parent (for regular user) is not found.
     * @throws ChildNotFoundException     if the child with the given ID is not found.
     * @throws AccessDeniedException      if the authenticated parent does not have access to the child.
     * @throws MilestoneNotFoundException if the milestone with the given ID is not found for the specified child.
     */
    public void deleteMilestone(Long childId, Long milestoneId, Long parentID) {
        authorizationHelper.validateAndAuthorizeForAdminOrParent(childId, parentID);
        MilestoneEntity milestoneEntity = milestonesRepository.findByChildIdAndId(childId, milestoneId)
                .orElseThrow(() -> new MilestoneNotFoundException("Milestone not found"));
        milestonesRepository.delete(milestoneEntity);
    }
}
