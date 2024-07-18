package pl.kasprzak.dawid.myfirstwords.service.milestones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
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
    private Authentication authentication;
    @Mock
    private MilestonesRepository milestonesRepository;
    @InjectMocks
    private UpdateMilestoneService updateMilestoneService;
    private ParentEntity parentEntity;
    private ChildEntity childEntity;
    private MilestoneEntity existingMilestone;
    private UpdateMilestoneRequest request;


    @BeforeEach
    void setUp() {
        parentEntity = new ParentEntity();
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

    @Test
    void when_updateMilestone_then_milestoneShouldBeUpdated() {

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndId(childEntity.getId(), existingMilestone.getId())).thenReturn(Optional.of(existingMilestone));
        when(milestonesRepository.save(existingMilestone)).thenReturn(existingMilestone);

        MilestoneEntity updateMilestone = updateMilestoneService.updateMilestone(childEntity.getId(), existingMilestone.getId(), request, authentication);

        assertNotNull(updateMilestone);
        assertEquals(request.getTitle(), updateMilestone.getTitle());
        assertEquals(request.getDescription(), updateMilestone.getDescription());
        assertEquals(request.getDateAchieve(), updateMilestone.getDateAchieve());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(milestonesRepository, times(1)).findByChildIdAndId(childEntity.getId(), existingMilestone.getId());
        verify(milestonesRepository, times(1)).save(any(MilestoneEntity.class));
    }

    @Test
    void when_updateMilestone_andMilestoneNotFound_then_throwMilestoneNotFoundException(){

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndId(childEntity.getId(), existingMilestone.getId())).thenReturn(Optional.empty());

        MilestoneNotFoundException milestoneNotFoundException = assertThrows(MilestoneNotFoundException.class,
                () -> updateMilestoneService.updateMilestone(childEntity.getId(), existingMilestone.getId(), request, authentication));

        assertEquals("Milestone not found", milestoneNotFoundException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(milestonesRepository, times(1)).findByChildIdAndId(childEntity.getId(), existingMilestone.getId());
        verify(milestonesRepository, never()).save(any(MilestoneEntity.class));
    }
}