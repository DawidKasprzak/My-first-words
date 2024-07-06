package pl.kasprzak.dawid.myfirstwords.service.milestones;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.MilestoneNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.milestones.UpdateMilestoneRequest;
import pl.kasprzak.dawid.myfirstwords.model.milestones.UpdateMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

@Service
@RequiredArgsConstructor
public class UpdateMilestoneService {

    private final AuthorizationHelper authorizationHelper;
    private final MilestonesRepository milestonesRepository;


    public MilestoneEntity updateMilestone(Long childId, Long milestoneId, UpdateMilestoneRequest request,
                                           Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        MilestoneEntity milestone = milestonesRepository.findById(milestoneId)
                .orElseThrow(() -> new MilestoneNotFoundException("milestone not found"));
        if (!milestone.getChild().getId().equals(childId)) {
            throw new AccessDeniedException("Milestone does not belong to the specified child");
        }
        milestone.setTitle(request.getTitle());
        milestone.setDescription(request.getDescription());
        milestone.setDateAchieve(request.getDateAchieve());
        return milestonesRepository.save(milestone);
    }
}
