package pl.kasprzak.dawid.myfirstwords.service.milestones;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.MilestoneNotFoundException;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeleteMilestoneService {

    private final MilestonesRepository milestonesRepository;
    private final AuthorizationHelper authorizationHelper;

    public void deleteMilestone(Long childId, Long milestoneId, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        Optional<MilestoneEntity> milestone = milestonesRepository.findByChildIdAndId(childId, milestoneId);
        MilestoneEntity milestoneEntity = milestone.orElseThrow(() -> new MilestoneNotFoundException("Milestone not found"));
        milestonesRepository.delete(milestoneEntity);
    }
}
