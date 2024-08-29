package pl.kasprzak.dawid.myfirstwords.service.milestones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.model.milestones.CreateMilestoneRequest;
import pl.kasprzak.dawid.myfirstwords.model.milestones.CreateMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.milestones.CreateMilestoneConverter;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMilestoneServiceTest {

    @Mock
    private AuthorizationHelper authorizationHelper;
    @Mock
    private MilestonesRepository milestonesRepository;
    @Mock
    private CreateMilestoneConverter createMilestoneConverter;
    @InjectMocks
    private CreateMilestoneService createMilestoneService;

    private ChildEntity childEntity;
    private CreateMilestoneRequest createMilestoneRequest;
    private MilestoneEntity milestoneEntity;
    private CreateMilestoneResponse createMilestoneResponse;

    @BeforeEach
    void setUp() {
        childEntity = new ChildEntity();
        childEntity.setId(1L);
        childEntity.setName("childName");

        createMilestoneRequest = CreateMilestoneRequest.builder()
                .title("first word")
                .description("first word - dad")
                .build();

        createMilestoneResponse = CreateMilestoneResponse.builder()
                .id(1L)
                .title(createMilestoneRequest.getTitle())
                .description(createMilestoneRequest.getDescription())
                .build();

        milestoneEntity = new MilestoneEntity();
        milestoneEntity.setId(createMilestoneResponse.getId());
        milestoneEntity.setTitle(createMilestoneResponse.getTitle());
        milestoneEntity.setDescription(createMilestoneResponse.getDescription());
        milestoneEntity.setChild(childEntity);

    }

    /**
     * Unit test for addMilestone method in CreateMilestoneService.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that a milestone is successfully added to the child's account.
     */
    @Test
    void when_addMilestone_then_milestoneShouldBeAddedToTheChild() {

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);
        when(createMilestoneConverter.fromDto(createMilestoneRequest)).thenReturn(milestoneEntity);
        when(milestonesRepository.save(milestoneEntity)).thenReturn(milestoneEntity);
        when(createMilestoneConverter.toDto(milestoneEntity)).thenReturn(createMilestoneResponse);

        CreateMilestoneResponse response = createMilestoneService.addMilestone(childEntity.getId(), createMilestoneRequest);

        assertEquals(createMilestoneResponse, response);
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(createMilestoneConverter, times(1)).fromDto(createMilestoneRequest);
        verify(milestonesRepository, times(1)).save(milestoneEntity);
        verify(createMilestoneConverter, times(1)).toDto(milestoneEntity);
    }
}