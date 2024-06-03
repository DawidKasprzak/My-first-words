package pl.kasprzak.dawid.myfirstwords.service.milestones;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
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

    public CreateMilestoneResponse addMilestone(Long childId, CreateMilestoneRequest request, Authentication authentication) {
        ChildEntity child = authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        MilestoneEntity milestoneToSave = createMilestoneConverter.fromDto(request);
        milestoneToSave.setChild(child);
        MilestoneEntity savedEntity = milestonesRepository.save(milestoneToSave);
        return createMilestoneConverter.toDto(savedEntity);
    }
}
