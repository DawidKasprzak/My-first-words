package pl.kasprzak.dawid.myfirstwords.service.milestones;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.MilestoneNotFoundException;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

@Service
@RequiredArgsConstructor
public class DeleteMilestoneService {

    private final MilestonesRepository milestonesRepository;
    private final AuthorizationHelper authorizationHelper;

    public void deleteMilestone(Long childId, Long milestoneId, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        MilestoneEntity milestone = milestonesRepository.findById(milestoneId)
                .orElseThrow(() -> new MilestoneNotFoundException("Milestone not found"));
        if (!milestone.getChild().getId().equals(childId)) {
            throw new AccessDeniedException("This milestone does not belong to this child");
        }
        milestonesRepository.delete(milestone);
    }
}
