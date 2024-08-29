package pl.kasprzak.dawid.myfirstwords.service.milestones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.exception.DateValidationException;
import pl.kasprzak.dawid.myfirstwords.exception.InvalidDateOrderException;
import pl.kasprzak.dawid.myfirstwords.exception.MilestoneNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.milestones.GetAllMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.model.milestones.GetMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.milestones.GetMilestoneConverter;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMilestoneServiceTest {
    @Mock
    private AuthorizationHelper authorizationHelper;
    @Mock
    private MilestonesRepository milestonesRepository;
    @Mock
    private GetMilestoneConverter getMilestoneConverter;
    @InjectMocks
    private GetMilestoneService getMilestoneService;
    private ChildEntity childEntity;
    private MilestoneEntity milestoneEntity1;
    private List<MilestoneEntity> milestoneEntities;
    private LocalDate date;
    private GetMilestoneResponse milestoneResponse;

    @BeforeEach
    void setUp() {

        ParentEntity parentEntity = new ParentEntity();
            parentEntity.setUsername("parentName");
            parentEntity.setPassword("password");

            childEntity = new ChildEntity();
            childEntity.setName("childName");
            childEntity.setParent(parentEntity);

            date = LocalDate.of(2024, 6, 6);

            milestoneEntity1 = new MilestoneEntity();
            milestoneEntity1.setId(1L);
            milestoneEntity1.setTitle("milestoneTitle1");
            milestoneEntity1.setDateAchieve(date.minusDays(1));
            milestoneEntity1.setChild(childEntity);

        MilestoneEntity milestoneEntity2 = new MilestoneEntity();
            milestoneEntity2.setId(2L);
            milestoneEntity2.setTitle("milestoneTitle2");
            milestoneEntity2.setDateAchieve(date.minusDays(2));
            milestoneEntity2.setChild(childEntity);

        MilestoneEntity milestoneEntity3 = new MilestoneEntity();
            milestoneEntity3.setId(3L);
            milestoneEntity3.setTitle("milestoneTitle3");
            milestoneEntity3.setDateAchieve(date.plusDays(1));
            milestoneEntity3.setChild(childEntity);

        MilestoneEntity milestoneEntity4 = new MilestoneEntity();
            milestoneEntity4.setId(4L);
            milestoneEntity4.setTitle("milestoneTitle4");
            milestoneEntity4.setDateAchieve(date.plusDays(2));
            milestoneEntity4.setChild(childEntity);

            milestoneEntities = Arrays.asList(milestoneEntity1, milestoneEntity2, milestoneEntity3, milestoneEntity4);

            milestoneResponse = GetMilestoneResponse.builder()
                    .id(milestoneEntity1.getId())
                    .title(milestoneEntity1.getTitle())
                    .dateAchieve(milestoneEntity1.getDateAchieve())
                    .build();
        }


    private GetMilestoneResponse createGetMilestoneResponse(MilestoneEntity entity) {
        return GetMilestoneResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .dateAchieve(entity.getDateAchieve())
                .build();
    }

    /**
     * Unit test for getByDateAchieveBefore method in GetMilestoneService.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that milestones achieved before the given date are retrieved and converted to DTOs.
     */
    @Test
    void when_getByDateAchieveBefore_then_milestonesShouldBeReturnedBeforeTheGivenDate() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndDateAchieveBefore(childEntity.getId(), date)).thenReturn(milestoneEntities.subList(0, 2));
        // Mock the behavior of getMilestoneConverter.toDto method to ensure that any MilestoneEntity passed to it
        // is converted to a GetMilestoneResponse using a predefined conversion method, createGetMilestoneResponse
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a MilestoneEntity object
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetMilestoneResponse to convert the MilestoneEntity object to a GetMilestoneResponse
            return createGetMilestoneResponse(entity);
        });

        List<GetMilestoneResponse> response = getMilestoneService.getByDateAchieveBefore(childEntity.getId(), date);

        assertEquals(2, response.size());
        for (GetMilestoneResponse milestoneResponse : response) {
            assertTrue(milestoneResponse.getDateAchieve().isBefore(date));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(milestonesRepository, times(1)).findByChildIdAndDateAchieveBefore(childEntity.getId(), date);
        verify(getMilestoneConverter, times(2)).toDto(any(MilestoneEntity.class));
    }

    /**
     * Unit test for getByDateAchieveAfter method in GetMilestoneService.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that milestones achieved after the given date are retrieved and converted to DTOs.
     */
    @Test
    void when_getByDateAchieveAfter_then_milestonesShouldBeReturnedAfterTheGivenDate() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndDateAchieveAfter(childEntity.getId(), date)).thenReturn(milestoneEntities.subList(2, 4));
        // Mock the behavior of getMilestoneConverter.toDto method to ensure that any MilestoneEntity passed to it
        // is converted to a GetMilestoneResponse using a predefined conversion method, createGetMilestoneResponse
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a MilestoneEntity object
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetMilestoneResponse to convert the MilestoneEntity object to a GetMilestoneResponse
            return createGetMilestoneResponse(entity);
        });

        List<GetMilestoneResponse> response = getMilestoneService.getByDateAchieveAfter(childEntity.getId(), date);

        assertEquals(2, response.size());
        for (GetMilestoneResponse milestoneResponse : response) {
            assertTrue(milestoneResponse.getDateAchieve().isAfter(date));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(milestonesRepository, times(1)).findByChildIdAndDateAchieveAfter(childEntity.getId(), date);
        verify(getMilestoneConverter, times(2)).toDto(any(MilestoneEntity.class));

    }

    /**
     * Unit test for getMilestonesBetweenDays method in GetMilestoneService.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that milestones achieved between the given dates are retrieved and converted to DTOs.
     */
    @Test
    void when_getMilestonesBetweenDays_then_milestonesShouldBeReturnedBetweenTheGivenDates() {
        LocalDate startDate = date.minusDays(2);
        LocalDate endDate = date.plusDays(2);

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndDateAchieveBetween(childEntity.getId(), startDate, endDate)).thenReturn(milestoneEntities);
        // Mock the behavior of getMilestoneConverter.toDto method to ensure that any MilestoneEntity passed to it
        // is converted to a GetMilestoneResponse using a predefined conversion method, createGetMilestoneResponse
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a MilestoneEntity object
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetMilestoneResponse to convert the MilestoneEntity object to a GetMilestoneResponse
            return createGetMilestoneResponse(entity);
        });

        List<GetMilestoneResponse> response = getMilestoneService.getMilestonesBetweenDays(childEntity.getId(), startDate, endDate);

        assertEquals(4, response.size());
        for (GetMilestoneResponse milestoneResponse : response) {
            assertTrue(milestoneResponse.getDateAchieve().isAfter(startDate.minusDays(1))
                    && milestoneResponse.getDateAchieve().isBefore(endDate.plusDays(1)));
        }
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(milestonesRepository, times(1)).findByChildIdAndDateAchieveBetween(childEntity.getId(), startDate, endDate);
        verify(getMilestoneConverter, times(4)).toDto(any(MilestoneEntity.class));
    }

    /**
     * Unit test for getMilestonesBetweenDays method in GetMilestoneService when start date is null.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that a DateValidationException is thrown and the appropriate error message is returned.
     */
    @Test
    void when_getMilestonesBetweenDays_and_startDateIsNull_then_throwDateValidationException() {
        LocalDate endDate = date.plusDays(2);

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);

        DateValidationException dateValidationException = assertThrows(DateValidationException.class,
                () -> getMilestoneService.getMilestonesBetweenDays(childEntity.getId(), null, endDate));

        assertEquals("Start date and end date must not be null", dateValidationException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(milestonesRepository, never()).findByChildIdAndDateAchieveBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }

    /**
     * Unit test for getMilestonesBetweenDays method in GetMilestoneService when end date is null.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that a DateValidationException is thrown and the appropriate error message is returned.
     */
    @Test
    void when_getMilestonesBetweenDays_and_endDateIsNull_then_throwDateValidationException() {
        LocalDate startDate = date.minusDays(2);

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);

        DateValidationException dateValidationException = assertThrows(DateValidationException.class,
                () -> getMilestoneService.getMilestonesBetweenDays(childEntity.getId(), startDate, null));

        assertEquals("Start date and end date must not be null", dateValidationException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(milestonesRepository, never()).findByChildIdAndDateAchieveBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));

    }

    /**
     * Unit test for getMilestonesBetweenDays method in GetMilestoneService when start date is after end date.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that a InvalidDateOrderException is thrown and the appropriate error message is returned.
     */
    @Test
    void when_getMilestonesBetweenDays_and_startDateIsAfterEndDate_then_throwInvalidDateOrderException() {
        LocalDate startDate = date.plusDays(2);
        LocalDate endDate = date.minusDays(2);

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);

        InvalidDateOrderException invalidDateOrderException = assertThrows(InvalidDateOrderException.class,
                () -> getMilestoneService.getMilestonesBetweenDays(childEntity.getId(), startDate, endDate));

        assertEquals("Start date must be before or equal to end date", invalidDateOrderException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(milestonesRepository, never()).findByChildIdAndDateAchieveBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }

    /**
     * Unit test for getAllMilestone method in GetMilestoneService.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that all milestones for the child are retrieved and converted to DTOs.
     */
    @Test
    void when_getAllMilestones_then_allMilestonesTheChildShouldBeReturned() {

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findAllByChildId(childEntity.getId())).thenReturn(milestoneEntities);
        // Mock the behavior of getMilestoneConverter.toDto method to ensure that any MilestoneEntity passed to it
        // is converted to a GetMilestoneResponse using a predefined conversion method, createGetMilestoneResponse
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a MilestoneEntity object
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetMilestoneResponse to convert the MilestoneEntity object to a GetMilestoneResponse
            return createGetMilestoneResponse(entity);
        });

        GetAllMilestoneResponse response = getMilestoneService.getAllMilestone(childEntity.getId());

        assertEquals(milestoneEntities.size(), response.getMilestones().size());

        for (GetMilestoneResponse milestoneResponse : response.getMilestones()) {
            assertTrue(milestoneEntities.stream().anyMatch(entity ->
                    entity.getId().equals(milestoneResponse.getId()) &&
                            entity.getTitle().equals(milestoneResponse.getTitle()) &&
                            entity.getDateAchieve().equals(milestoneResponse.getDateAchieve())));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(milestonesRepository, times(1)).findAllByChildId(childEntity.getId());
        verify(getMilestoneConverter, times(milestoneEntities.size())).toDto(any(MilestoneEntity.class));
    }

    /**
     * Unit test for getByTitle method in GetMilestoneService.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that the correct milestone is returned for a given child ID and title.
     */
    @Test
    void when_getByTitle_then_milestoneByTitleShouldBeReturned() {
        String title = "tiTLe1";

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId())).thenReturn(milestoneEntities.subList(0, 1));
        when(getMilestoneConverter.toDto(milestoneEntity1)).thenReturn(milestoneResponse);

        List<GetMilestoneResponse> expectedMilestones = Collections.singletonList(milestoneResponse);
        GetAllMilestoneResponse expectedResponse = GetAllMilestoneResponse.builder().milestones(expectedMilestones).build();

        GetAllMilestoneResponse response = getMilestoneService.getByTitle(childEntity.getId(), title.toLowerCase());

        assertEquals(expectedResponse, response);
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(milestonesRepository, times(1)).findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId());
        verify(getMilestoneConverter, times(1)).toDto(milestoneEntity1);
    }

    /**
     * Unit test for getByTitle method in GetMilestoneService.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that all milestones matching the given title are returned for the child.
     */
    @Test
    void when_getByTitle_then_allMilestonesByTitleShouldBeReturned() {
        String title = "miLeStoNe";

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId())).thenReturn(milestoneEntities);
        // Mock the behavior of getMilestoneConverter.toDto method to ensure that any MilestoneEntity passed to it
        // is converted to a GetMilestoneResponse using a predefined conversion method, createGetMilestoneResponse
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a MilestoneEntity object
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetMilestoneResponse to convert the MilestoneEntity object to a GetMilestoneResponse
            return createGetMilestoneResponse(entity);
        });

        GetAllMilestoneResponse response = getMilestoneService.getByTitle(childEntity.getId(), title.toLowerCase());

        assertEquals(4, response.getMilestones().size());
        for (GetMilestoneResponse milestoneResponse : response.getMilestones()) {
            assertTrue(milestoneEntities.stream()
                    .anyMatch(milestoneEntity ->
                            milestoneEntity.getTitle().contains(milestoneResponse.getTitle())));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(milestonesRepository, times(1)).findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId());
        verify(getMilestoneConverter, times(response.getMilestones().size())).toDto(any(MilestoneEntity.class));
    }

    /**
     * Unit test for getByTitle method in GetMilestoneService when the milestone does not exist.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that a MilestoneNotFoundException is thrown and the appropriate error message is returned.
     */
    @Test
    void when_getByTitle_and_titleNonExistent_then_throwMilestoneNotFoundException() {
        String title = "titleNonExistent";

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId())).thenReturn(Collections.emptyList());

        MilestoneNotFoundException milestoneNotFoundException = assertThrows(MilestoneNotFoundException.class,
                () -> getMilestoneService.getByTitle(childEntity.getId(), title.toLowerCase()));

        assertEquals("Milestone not found", milestoneNotFoundException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(milestonesRepository, times(1)).findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId());
        verify(getMilestoneConverter, never()).toDto(any(MilestoneEntity.class));
    }
}