package pl.kasprzak.dawid.myfirstwords.service.milestones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import pl.kasprzak.dawid.myfirstwords.exception.MilestoneNotFoundException;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteMilestoneServiceTest {
    @Mock
    private AuthorizationHelper authorizationHelper;
    @Mock
    private Authentication authentication;
    @Mock
    private MilestonesRepository milestonesRepository;
    @InjectMocks
    private DeleteMilestoneService deleteMilestoneService;

    private MilestoneEntity milestoneEntity;
    private ChildEntity childEntity;

    @BeforeEach
    void setUp() {

        milestoneEntity = new MilestoneEntity();
        milestoneEntity.setId(1L);

        childEntity = new ChildEntity();
        childEntity.setId(1L);
        milestoneEntity.setChild(childEntity);
    }

    /**
     * Unit test for deleteMilestone method in DeleteMilestoneService.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that a milestone is successfully deleted from the child's account.
     */
    @Test
    void when_deleteMilestone_then_milestoneShouldBeDeletedFromChildAccount() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndId(childEntity.getId(), milestoneEntity.getId())).thenReturn(Optional.of(milestoneEntity));

        deleteMilestoneService.deleteMilestone(childEntity.getId(), milestoneEntity.getId(), authentication);

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(milestonesRepository, times(1)).delete(milestoneEntity);
    }

    /**
     * Unit test for deleteMilestone method in DeleteMilestoneService.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that a MilestoneNotFoundException is thrown when the milestone is not found.
     */
    @Test
    void when_deleteMilestoneAndMilestoneNotFound_then_throwMilestoneNotFoundException() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndId(childEntity.getId(), milestoneEntity.getId())).thenReturn(Optional.empty());

        MilestoneNotFoundException milestoneNotFoundException = assertThrows(MilestoneNotFoundException.class,
                () -> deleteMilestoneService.deleteMilestone(childEntity.getId(), milestoneEntity.getId(), authentication));

        assertEquals("Milestone not found", milestoneNotFoundException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(milestonesRepository, never()).delete(any());
    }
}