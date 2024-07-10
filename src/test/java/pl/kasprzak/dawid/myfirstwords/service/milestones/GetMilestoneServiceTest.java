package pl.kasprzak.dawid.myfirstwords.service.milestones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.Authentication;
import pl.kasprzak.dawid.myfirstwords.model.milestones.GetMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.milestones.GetMilestoneConverter;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMilestoneServiceTest {
    @Mock
    private AuthorizationHelper authorizationHelper;
    @Mock
    private Authentication authentication;
    @Mock
    private MilestonesRepository milestonesRepository;
    @Mock
    private GetMilestoneConverter getMilestoneConverter;
    @InjectMocks
    private GetMilestoneService getMilestoneService;
    private ParentEntity parentEntity;
    private ChildEntity childEntity;
    private MilestoneEntity milestoneEntity1, milestoneEntity2, milestoneEntity3, milestoneEntity4;
    private List<MilestoneEntity> milestoneEntities;
    private LocalDate date;
    private GetMilestoneResponse exampleResponse;

    @BeforeEach
    void setUp(){
        parentEntity = new ParentEntity();
        parentEntity.setUsername("parentName");
        parentEntity.setPassword("password");

        childEntity = new ChildEntity();
        childEntity.setName("childName");
        childEntity.setParent(parentEntity);

        date = LocalDate.of(2024,6,6);

        milestoneEntity1 = new MilestoneEntity();
        milestoneEntity1.setId(1L);
        milestoneEntity1.setTitle("milestoneTitle1");
        milestoneEntity1.setDateAchieve(date.minusDays(1));
        milestoneEntity1.setChild(childEntity);

        milestoneEntity2 = new MilestoneEntity();
        milestoneEntity2.setId(2L);
        milestoneEntity2.setTitle("milestoneTitle2");
        milestoneEntity2.setDateAchieve(date.minusDays(2));
        milestoneEntity2.setChild(childEntity);

        milestoneEntity3 = new MilestoneEntity();
        milestoneEntity3.setId(3L);
        milestoneEntity3.setTitle("milestoneTitle3");
        milestoneEntity3.setDateAchieve(date.plusDays(1));
        milestoneEntity3.setChild(childEntity);

        milestoneEntity4 = new MilestoneEntity();
        milestoneEntity4.setId(4L);
        milestoneEntity4.setTitle("milestoneTitle4");
        milestoneEntity4.setDateAchieve(date.plusDays(2));
        milestoneEntity4.setChild(childEntity);

        milestoneEntities = Arrays.asList(milestoneEntity1, milestoneEntity2, milestoneEntity3, milestoneEntity4);

        exampleResponse = GetMilestoneResponse.builder()
                .id(0L)
                .title("testMilestone")
                .dateAchieve(LocalDate.now())
                .build();
    }

    @Test
    void when_getByDateAchieveBefore_then_milestonesShouldBeReturnedBeforeTheGivenDate() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndDateAchieveBefore(childEntity.getId(), date)).thenReturn(milestoneEntities.subList(0, 2));
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenReturn(exampleResponse);

        List<GetMilestoneResponse> response = getMilestoneService.getByDateAchieveBefore(childEntity.getId(), date,authentication);

        assertEquals(2, response.size());
        for (int i = 0; i < response.size(); i++){
            MilestoneEntity entity = milestoneEntities.get(i);
            assertTrue(entity.getDateAchieve().isBefore(date));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(milestonesRepository, times(1)).findByChildIdAndDateAchieveBefore(childEntity.getId(), date);
        verify(getMilestoneConverter, times(2)).toDto(any(MilestoneEntity.class));



    }

    @Test
    void getByDateAchieveAfter() {
    }

    @Test
    void getMilestonesBetweenDays() {
    }

    @Test
    void getAllMilestone() {
    }

    @Test
    void getByTitle() {
    }
}