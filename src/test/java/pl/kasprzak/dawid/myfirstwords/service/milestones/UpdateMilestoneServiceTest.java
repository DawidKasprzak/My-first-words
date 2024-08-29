package pl.kasprzak.dawid.myfirstwords.service.milestones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.exception.MilestoneNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.milestones.UpdateMilestoneRequest;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateMilestoneServiceTest {
    @Mock
    private AuthorizationHelper authorizationHelper;
    @Mock
    private MilestonesRepository milestonesRepository;
    @InjectMocks
    private UpdateMilestoneService updateMilestoneService;
    private ChildEntity childEntity;
    private MilestoneEntity existingMilestone;
    private UpdateMilestoneRequest request;


    @BeforeEach
    void setUp() {

        ParentEntity parentEntity = new ParentEntity();
        parentEntity.setId(1L);
        parentEntity.setUsername("parent");

        childEntity = new ChildEntity();
        childEntity.setId(1L);
        childEntity.setName("childName");

        existingMilestone = new MilestoneEntity();
        existingMilestone.setId(1L);
        existingMilestone.setTitle("old title");
        existingMilestone.setDescription("old description");
        existingMilestone.setDateAchieve(LocalDate.now().minusDays(1));
        existingMilestone.setChild(childEntity);

        request = UpdateMilestoneRequest.builder()
                .title("new title")
                .description("new description")
                .dateAchieve(LocalDate.now())
                .build();

    }

    /**
     * Unit test for updateMilestone method in UpdateMilestoneService.
     * Verifies that the child belongs to the authenticated parent by using the AuthorizationHelper.
     * Then, it checks that the existing milestone is retrieved, updated with the new details provided in the request,
     * and that the updated milestone is saved to the repository.
     * The test ensures that the title, description, and dateAchieve fields of the milestone are correctly updated.
     */
    @Test
    void when_updateMilestone_then_milestoneShouldBeUpdated() {

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndId(childEntity.getId(), existingMilestone.getId())).thenReturn(Optional.of(existingMilestone));
        when(milestonesRepository.save(existingMilestone)).thenReturn(existingMilestone);

        MilestoneEntity updateMilestone = updateMilestoneService.updateMilestone(childEntity.getId(), existingMilestone.getId(), request);

        assertNotNull(updateMilestone);
        assertEquals(request.getTitle(), updateMilestone.getTitle());
        assertEquals(request.getDescription(), updateMilestone.getDescription());
        assertEquals(request.getDateAchieve(), updateMilestone.getDateAchieve());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(milestonesRepository, times(1)).findByChildIdAndId(childEntity.getId(), existingMilestone.getId());
        verify(milestonesRepository, times(1)).save(any(MilestoneEntity.class));
    }

    /**
     * Unit test for updateMilestone method in UpdateMilestoneService.
     * Verifies that the child belongs to the authenticated parent by using the AuthorizationHelper.
     * Then, it checks that a MilestoneNotFoundException is thrown when the milestone with the given ID
     * is not found for the specified child.
     * The test also verifies that the appropriate error message "Milestone not found" is returned and that
     * the milestone is not saved to the repository when it does not exist.
     */
    @Test
    void when_updateMilestone_andMilestoneNotFound_then_throwMilestoneNotFoundException() {

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndId(childEntity.getId(), existingMilestone.getId())).thenReturn(Optional.empty());

        MilestoneNotFoundException milestoneNotFoundException = assertThrows(MilestoneNotFoundException.class,
                () -> updateMilestoneService.updateMilestone(childEntity.getId(), existingMilestone.getId(), request));

        assertEquals("Milestone not found", milestoneNotFoundException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(milestonesRepository, times(1)).findByChildIdAndId(childEntity.getId(), existingMilestone.getId());
        verify(milestonesRepository, never()).save(any(MilestoneEntity.class));
    }
}