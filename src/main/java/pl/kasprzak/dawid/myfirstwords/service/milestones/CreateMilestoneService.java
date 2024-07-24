package pl.kasprzak.dawid.myfirstwords.service.milestones;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;
import pl.kasprzak.dawid.myfirstwords.model.milestones.CreateMilestoneRequest;
import pl.kasprzak.dawid.myfirstwords.model.milestones.CreateMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.milestones.CreateMilestoneConverter;

@Service
@RequiredArgsConstructor
public class CreateMilestoneService {

    private final MilestonesRepository milestonesRepository;
    private final CreateMilestoneConverter createMilestoneConverter;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Service method for adding a new milestone for a specific child.
     * This method validates and authorizes the parent using AuthorizationHelper, and if authorized,
     * converts the CreateMilestoneRequest DTO to a MilestoneEntity, sets the child for the milestone, saves the
     * milestone entity to the repository, and converts the saved entity to a CreateMilestoneResponse DTO.
     *
     * @param childId        the ID of the child to whom the milestone will be added.
     * @param request        the CreateMilestoneRequest containing the milestone details.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a CreateMilestoneResponse DTO containing the details of the newly created milestone.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     */
    public CreateMilestoneResponse addMilestone(Long childId, CreateMilestoneRequest request, Authentication authentication) {
        ChildEntity child = authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        MilestoneEntity milestoneToSave = createMilestoneConverter.fromDto(request);
        milestoneToSave.setChild(child);
        MilestoneEntity savedEntity = milestonesRepository.save(milestoneToSave);
        return createMilestoneConverter.toDto(savedEntity);
    }
}
