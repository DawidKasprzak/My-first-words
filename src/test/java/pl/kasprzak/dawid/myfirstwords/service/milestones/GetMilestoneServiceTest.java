package pl.kasprzak.dawid.myfirstwords.service.milestones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.exception.AdminMissingParentIDException;
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
    private ParentEntity parentEntity;
    private MilestoneEntity milestoneEntity1;
    private List<MilestoneEntity> milestoneEntities;
    private LocalDate date;
    private GetMilestoneResponse milestoneResponse;

    @BeforeEach
    void setUp() {

        parentEntity = new ParentEntity();
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
     * Unit test for the getByDateAchieveBefore method in GetMilestoneService.
     * This test verifies that the service correctly retrieves milestone achieved before a specified date
     * for a given child and converts them to DTOs.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. The MilestonesRepository is queried to find milestones associated with the child that were achieved before the specified date.
     * 3. Each retrieved MilestoneEntity is converted to a GetMilestoneResponse DTO using the GetMilestonesConverter.
     */
    @Test
    void when_getByDateAchieveBefore_then_milestonesShouldBeReturnedBeforeTheGivenDate() {
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndDateAchieveBefore(childEntity.getId(), date)).thenReturn(milestoneEntities.subList(0, 2));
        // Mock the behavior of getMilestoneConverter.toDto method to ensure that any MilestoneEntity passed to it
        // is converted to a GetMilestoneResponse using a predefined conversion method, createGetMilestoneResponse
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a MilestoneEntity object
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetMilestoneResponse to convert the MilestoneEntity object to a GetMilestoneResponse
            return createGetMilestoneResponse(entity);
        });

        List<GetMilestoneResponse> response = getMilestoneService.getByDateAchieveBefore(childEntity.getId(), date, null);

        assertEquals(2, response.size());
        for (GetMilestoneResponse milestoneResponse : response) {
            assertTrue(milestoneResponse.getDateAchieve().isBefore(date));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, times(1)).findByChildIdAndDateAchieveBefore(childEntity.getId(), date);
        verify(getMilestoneConverter, times(2)).toDto(any(MilestoneEntity.class));
    }

    /**
     * Unit test for the getByDateAchieveBefore method in GetMilestoneService when accessed by an administrator.
     * This test verifies that the service correctly retrieves milestone achieved before a specified date
     * for a given child when the request is made by an administrator.
     * The test ensures that:
     * 1. The child is validated and authorized for the administrator using the AuthorizationHelper
     * with a provided parent ID.
     * 2. The MilestonesRepository is queried to find milestones associated with the child that were achieved
     * before the specified date.
     * 3. Each retrieved MilestoneEntity is converted to a GetMilestoneResponse DTO using the GetMilestonesConverter.
     */
    @Test
    void when_adminGetsMilestonesByDateAchieveBefore_then_milestonesShouldBeReturnedBeforeTheGivenDate() {
        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndDateAchieveBefore(childEntity.getId(), date)).thenReturn(milestoneEntities.subList(0, 2));

        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            return createGetMilestoneResponse(entity);
        });

        List<GetMilestoneResponse> responses = getMilestoneService.getByDateAchieveBefore(childEntity.getId(), date, parentEntity.getId());

        assertEquals(2, responses.size());
        for (GetMilestoneResponse milestoneResponse : responses) {
            assertTrue(milestoneResponse.getDateAchieve().isBefore(date));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId());
        verify(milestonesRepository, times(1)).findByChildIdAndDateAchieveBefore(childEntity.getId(), date);
        verify(getMilestoneConverter, times(2)).toDto(any(MilestoneEntity.class));
    }

    /**
     * Unit test for the getByDateAchieveAfter method in GetMilestoneService.
     * This test verifies that the service correctly retrieves milestones achieved after a specified date
     * for a given child and converts them to DTOs.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. The MilestonesRepository is queried to find milestones associated with the child that were achieved after the specified date.
     * 3. Each retrieved MilestoneEntity is converted to a GeMilestoneResponse DTO using the GetMilestonesConverter.
     */
    @Test
    void when_getByDateAchieveAfter_then_milestonesShouldBeReturnedAfterTheGivenDate() {
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndDateAchieveAfter(childEntity.getId(), date)).thenReturn(milestoneEntities.subList(2, 4));
        // Mock the behavior of getMilestoneConverter.toDto method to ensure that any MilestoneEntity passed to it
        // is converted to a GetMilestoneResponse using a predefined conversion method, createGetMilestoneResponse
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a MilestoneEntity object
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetMilestoneResponse to convert the MilestoneEntity object to a GetMilestoneResponse
            return createGetMilestoneResponse(entity);
        });

        List<GetMilestoneResponse> response = getMilestoneService.getByDateAchieveAfter(childEntity.getId(), date, null);

        assertEquals(2, response.size());
        for (GetMilestoneResponse milestoneResponse : response) {
            assertTrue(milestoneResponse.getDateAchieve().isAfter(date));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, times(1)).findByChildIdAndDateAchieveAfter(childEntity.getId(), date);
        verify(getMilestoneConverter, times(2)).toDto(any(MilestoneEntity.class));

    }

    /**
     * Unit test for the getByDateAchieveAfter method in GetMilestoneService when accessed by an administrator.
     * This test verifies that the service correctly retrieves milestones achieved after a specified date
     * for a given child when the request is made by an administrator.
     * The test ensures that:
     * 1. The child is validated and authorized for the administrator using the AuthorizationHelper
     * with a provided parent ID.
     * 2. The MilestonesRepository is queried to find milestones associated with the child that were achieved
     * after the specified date.
     * 3. Each retrieved MilestoneEntity is converted to a GetMilestoneResponse DTO using the GetMilestonesConverter.
     */
    @Test
    void when_adminGetsMilestonesByDateAchieveAfter_then_milestonesShouldBeReturnedAfterTheGivenDate() {
        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndDateAchieveAfter(childEntity.getId(), date)).thenReturn(milestoneEntities.subList(2, 4));

        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            return createGetMilestoneResponse(entity);
        });

        List<GetMilestoneResponse> response = getMilestoneService.getByDateAchieveAfter(childEntity.getId(), date, parentEntity.getId());

        assertEquals(2, response.size());
        for (GetMilestoneResponse milestoneResponse : response) {
            assertTrue(milestoneResponse.getDateAchieve().isAfter(date));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId());
        verify(milestonesRepository, times(1)).findByChildIdAndDateAchieveAfter(childEntity.getId(), date);
        verify(getMilestoneConverter, times(2)).toDto(any(MilestoneEntity.class));

    }

    /**
     * Unit test for the getMilestonesBetweenDays method in GetMilestoneService.
     * This test verifies that the service correctly retrieves milestones achieved between a specified dates
     * for a given child and converts them to DTOs.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. The MilestonesRepository is queried to find milestones associated with the child that were achieved between the specified date.
     * 3. Each retrieved MilestoneEntity is converted to a GetMilestoneResponse DTO using the GetMilestonesConverter.
     */
    @Test
    void when_getMilestonesBetweenDays_then_milestonesShouldBeReturnedBetweenTheGivenDates() {
        LocalDate startDate = date.minusDays(2);
        LocalDate endDate = date.plusDays(2);

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndDateAchieveBetween(childEntity.getId(), startDate, endDate)).thenReturn(milestoneEntities);
        // Mock the behavior of getMilestoneConverter.toDto method to ensure that any MilestoneEntity passed to it
        // is converted to a GetMilestoneResponse using a predefined conversion method, createGetMilestoneResponse
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a MilestoneEntity object
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetMilestoneResponse to convert the MilestoneEntity object to a GetMilestoneResponse
            return createGetMilestoneResponse(entity);
        });

        List<GetMilestoneResponse> response = getMilestoneService.getMilestonesBetweenDays(childEntity.getId(), startDate, endDate, null);

        assertEquals(4, response.size());
        for (GetMilestoneResponse milestoneResponse : response) {
            assertTrue(milestoneResponse.getDateAchieve().isAfter(startDate.minusDays(1))
                    && milestoneResponse.getDateAchieve().isBefore(endDate.plusDays(1)));
        }
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, times(1)).findByChildIdAndDateAchieveBetween(childEntity.getId(), startDate, endDate);
        verify(getMilestoneConverter, times(4)).toDto(any(MilestoneEntity.class));
    }

    /**
     * Unit test for the getMilestonesBetweenDays method in GetMilestoneService when accessed by an administrator.
     * This test verifies that the service correctly retrieves milestones achieved between specified dates
     * for a given child when the request is made by an administrator, providing a parent ID.
     * The test ensures that:
     * 1. The child is validated and authorized for the administrator using the AuthorizationHelper with a provided parent ID.
     * 2. The MilestonesRepository is queried to find milestones associated with the child that were achieved between the specified dates.
     * 3. Each retrieved MilestoneEntity is converted to a GetMilestoneResponse DTO using the GetMilestonesConverter.
     */
    @Test
    void when_adminGetsMilestonesBetweenDays_then_milestonesShouldBeReturnedBetweenTheGivenDates() {
        LocalDate startDate = date.minusDays(2);
        LocalDate endDate = date.plusDays(2);

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndDateAchieveBetween(childEntity.getId(), startDate, endDate)).thenReturn(milestoneEntities);
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            return createGetMilestoneResponse(entity);
        });

        List<GetMilestoneResponse> response = getMilestoneService.getMilestonesBetweenDays(childEntity.getId(), startDate, endDate, parentEntity.getId());

        assertEquals(4, response.size());
        for (GetMilestoneResponse milestoneResponse : response) {
            assertTrue(milestoneResponse.getDateAchieve().isAfter(startDate.minusDays(1))
                    && milestoneResponse.getDateAchieve().isBefore(endDate.plusDays(1)));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId());
        verify(milestonesRepository, times(1)).findByChildIdAndDateAchieveBetween(childEntity.getId(), startDate, endDate);
        verify(getMilestoneConverter, times(4)).toDto(any(MilestoneEntity.class));
    }

    /**
     * Unit test for the getMilestonesBetweenDays method in GetMilestoneService when the start date is null.
     * This test verifies that the service throws a DateValidationException if the start date is not provided.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. A DateValidationException is thrown if the start date is null.
     * 3. The MilestonesRepository is never queried if the validation fails due to a missing start date.
     * 4. The appropriate error message ("Start date and end date must not be null") is returned when the exception is thrown.
     */
    @Test
    void when_getMilestonesBetweenDays_and_startDateIsNull_then_throwDateValidationException() {
        LocalDate endDate = date.plusDays(2);

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);

        DateValidationException dateValidationException = assertThrows(DateValidationException.class,
                () -> getMilestoneService.getMilestonesBetweenDays(childEntity.getId(), null, endDate, null));

        assertEquals("Start date and end date must not be null", dateValidationException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, never()).findByChildIdAndDateAchieveBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }

    /**
     * Unit test for the getMilestonesBetweenDays method in GetMilestoneService when the end date is null.
     * This test verifies that the service throws a DateValidationException if the end date is not provided.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. A DateValidationException is thrown if the end date is null.
     * 3. The WordsRepository is never queried if the validation fails due to a missing end date.
     * 4. The appropriate error message ("Start date and end date must not be null") is returned when the exception is thrown.
     */
    @Test
    void when_getMilestonesBetweenDays_and_endDateIsNull_then_throwDateValidationException() {
        LocalDate startDate = date.minusDays(2);

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);

        DateValidationException dateValidationException = assertThrows(DateValidationException.class,
                () -> getMilestoneService.getMilestonesBetweenDays(childEntity.getId(), startDate, null, null));

        assertEquals("Start date and end date must not be null", dateValidationException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, never()).findByChildIdAndDateAchieveBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));

    }

    /**
     * Unit test for the getMilestonesBetweenDays method in GetMilestoneService when the start date is after end date.
     * This test verifies that the service throws a InvalidDateOrderException if the start date is after end date.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. A InvalidDateOrderException is thrown if the start date is after end date.
     * 3. The MilestonesRepository is never queried if the validation fails due to start date is after end date.
     * 4. The appropriate error message ("Start date must be before or equal to end date") is returned when the exception is thrown.
     */
    @Test
    void when_getMilestonesBetweenDays_and_startDateIsAfterEndDate_then_throwInvalidDateOrderException() {
        LocalDate startDate = date.plusDays(2);
        LocalDate endDate = date.minusDays(2);

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);

        InvalidDateOrderException invalidDateOrderException = assertThrows(InvalidDateOrderException.class,
                () -> getMilestoneService.getMilestonesBetweenDays(childEntity.getId(), startDate, endDate, null));

        assertEquals("Start date must be before or equal to end date", invalidDateOrderException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, never()).findByChildIdAndDateAchieveBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }

    /**
     * Unit test for the getAllMilestones method in GetMilestoneService.
     * This test verifies that the service correctly retrieves all milestones for a given child
     * and converts them to DTOs.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. The MilestonesRepository is queried to find all milestones associated with the child.
     * 3. Each retrieved MilestoneEntity is converted to a GetMilestoneResponse DTO using the GetMilestonesConverter.
     */
    @Test
    void when_getAllMilestones_then_allMilestonesTheChildShouldBeReturned() {

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(milestonesRepository.findAllByChildId(childEntity.getId())).thenReturn(milestoneEntities);
        // Mock the behavior of getMilestoneConverter.toDto method to ensure that any MilestoneEntity passed to it
        // is converted to a GetMilestoneResponse using a predefined conversion method, createGetMilestoneResponse
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a MilestoneEntity object
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetMilestoneResponse to convert the MilestoneEntity object to a GetMilestoneResponse
            return createGetMilestoneResponse(entity);
        });

        GetAllMilestoneResponse response = getMilestoneService.getAllMilestone(childEntity.getId(), null);

        assertEquals(milestoneEntities.size(), response.getMilestones().size());

        for (GetMilestoneResponse milestoneResponse : response.getMilestones()) {
            assertTrue(milestoneEntities.stream().anyMatch(entity ->
                    entity.getId().equals(milestoneResponse.getId()) &&
                            entity.getTitle().equals(milestoneResponse.getTitle()) &&
                            entity.getDateAchieve().equals(milestoneResponse.getDateAchieve())));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, times(1)).findAllByChildId(childEntity.getId());
        verify(getMilestoneConverter, times(milestoneEntities.size())).toDto(any(MilestoneEntity.class));
    }

    /**
     * Unit test for the getAllMilestones method in GetMilestoneService when accessed by an administrator.
     * This test verifies that the service correctly retrieves all milestone for a given child
     * when the request is made by an administrator, providing a parent ID.
     * The test ensures that:
     * 1. The child is validated and authorized for the administrator using the AuthorizationHelper with a provided parent ID.
     * 2. The MilestonesRepository is queried to find all milestones associated with the child.
     * 3. Each retrieved MilestoneEntity is converted to a GetMilestoneResponse DTO using the GetMilestonesConverter.
     */
    @Test
    void when_adminGetsAllMilestones_then_allMilestonesForTheChildShouldBeReturned() {

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findAllByChildId(childEntity.getId())).thenReturn(milestoneEntities);
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            return createGetMilestoneResponse(entity);
        });

        GetAllMilestoneResponse response = getMilestoneService.getAllMilestone(childEntity.getId(), parentEntity.getId());

        assertEquals(milestoneEntities.size(), response.getMilestones().size());
        //sprawdzić
        for (GetMilestoneResponse milestoneResponse : response.getMilestones()) {
            assertTrue(milestoneEntities.stream().anyMatch(entity ->
                    entity.getId().equals(milestoneResponse.getId()) &&
                            entity.getTitle().equals(milestoneResponse.getTitle()) &&
                            entity.getDateAchieve().equals(milestoneResponse.getDateAchieve())));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId());
        verify(milestonesRepository, times(1)).findAllByChildId(childEntity.getId());
        verify(getMilestoneConverter, times(milestoneEntities.size())).toDto(any(MilestoneEntity.class));
    }

    /**
     * Unit test for the getByTitle method in GetMilestoneService.
     * This test verifies that the service correctly retrieves a single milestone
     * for a given child based on the title provided, ignoring case sensitivity.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. The MilestonesRepository is queried to find milestones associated with the child, ignoring case sensitivity.
     * 3. The retrieved MilestoneEntity is converted to a GetMilestoneResponse DTO using the GetMilestoneConverter.
     */
    @Test
    void when_getByTitle_then_milestoneByTitleShouldBeReturned() {
        String title = "tiTLe1";

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(milestonesRepository.findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId())).thenReturn(milestoneEntities.subList(0, 1));
        when(getMilestoneConverter.toDto(milestoneEntity1)).thenReturn(milestoneResponse);

        List<GetMilestoneResponse> expectedMilestones = Collections.singletonList(milestoneResponse);
        GetAllMilestoneResponse expectedResponse = GetAllMilestoneResponse.builder().milestones(expectedMilestones).build();

        GetAllMilestoneResponse response = getMilestoneService.getByTitle(childEntity.getId(), title.toLowerCase(), null);

        assertEquals(expectedResponse, response);
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, times(1)).findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId());
        verify(getMilestoneConverter, times(1)).toDto(milestoneEntity1);
    }

    /**
     * Unit test for the getByTitle method in GetMilestoneService when accessed by an administrator.
     * This test verifies that the service correctly retrieves a single milestone
     * for a given child when the request is made by an administrator, providing a parent ID.
     * The test ensures that:
     * 1. The child is validated and authorized for the administrator using the AuthorizationHelper with a provided parent ID.
     * 2. The MilestonesRepository is queried to find milestones associated with the child, ignoring case sensitivity.
     * 3. The retrieved MilestoneEntity is converted to a GetMilestoneResponse DTO using the GetMilestoneConverter.
     */
    @Test
    void when_adminGetsMilestoneByTitle_then_childMilestoneByTitleShouldBeReturned() {
        String title = "tiTLe1";

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId())).thenReturn(milestoneEntities.subList(0, 1));
        when(getMilestoneConverter.toDto(milestoneEntity1)).thenReturn(milestoneResponse);

        List<GetMilestoneResponse> expectedMilestones = Collections.singletonList(milestoneResponse);
        GetAllMilestoneResponse expectedResponse = GetAllMilestoneResponse.builder().milestones(expectedMilestones).build();

        GetAllMilestoneResponse response = getMilestoneService.getByTitle(childEntity.getId(), title.toLowerCase(), parentEntity.getId());

        assertEquals(expectedResponse, response);
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId());
        verify(milestonesRepository, times(1)).findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId());
        verify(getMilestoneConverter, times(1)).toDto(milestoneEntity1);
    }

    /**
     * Unit test for the getByTitle method in GetMilestoneService.
     * This test verifies that the service correctly retrieves multiple milestones
     * for a given child based on the title provided, ignoring case sensitivity.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. The MilestonesRepository is queried to find milestones associated with the child, ignoring case sensitivity.
     * 3. Each retrieved MilestoneEntity is converted to a GetMilestoneResponse DTO using the GetMilestoneConverter.
     */
    @Test
    void when_getByTitle_then_allMilestonesByTitleShouldBeReturned() {
        String title = "miLeStoNe";

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(milestonesRepository.findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId())).thenReturn(milestoneEntities);
        // Mock the behavior of getMilestoneConverter.toDto method to ensure that any MilestoneEntity passed to it
        // is converted to a GetMilestoneResponse using a predefined conversion method, createGetMilestoneResponse
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a MilestoneEntity object
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetMilestoneResponse to convert the MilestoneEntity object to a GetMilestoneResponse
            return createGetMilestoneResponse(entity);
        });

        GetAllMilestoneResponse response = getMilestoneService.getByTitle(childEntity.getId(), title.toLowerCase(), null);

        assertEquals(4, response.getMilestones().size());
        for (GetMilestoneResponse milestoneResponse : response.getMilestones()) {
            assertTrue(milestoneEntities.stream()
                    .anyMatch(milestoneEntity ->
                            milestoneEntity.getTitle().contains(milestoneResponse.getTitle())));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, times(1)).findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId());
        verify(getMilestoneConverter, times(response.getMilestones().size())).toDto(any(MilestoneEntity.class));
    }

    /**
     * Unit test for the getByTitle method in GetMilestoneService when accessed by an administrator.
     * This test verifies that the service correctly retrieves multiple milestones
     * for a given child when the request is made by an administrator, providing a parent ID.
     * The test ensures that:
     * 1. The child is validated and authorized for the administrator using the AuthorizationHelper with a provided parent ID.
     * 2. The MilestonesRepository is queried to find milestones associated with the child, ignoring case sensitivity.
     * 3. Each retrieved MilestoneEntity is converted to a GetMilestoneResponse DTO using the GetMilestoneConverter.
     */
    @Test
    void when_adminGetsMilestoneByTitle_then_allMilestonesByTitleShouldBeReturned() {
        String title = "miLeStoNe";

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId())).thenReturn(milestoneEntities);
        // Mock the behavior of getMilestoneConverter.toDto method to ensure that any MilestoneEntity passed to it
        // is converted to a GetMilestoneResponse using a predefined conversion method, createGetMilestoneResponse
        when(getMilestoneConverter.toDto(any(MilestoneEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a MilestoneEntity object
            MilestoneEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetMilestoneResponse to convert the MilestoneEntity object to a GetMilestoneResponse
            return createGetMilestoneResponse(entity);
        });

        GetAllMilestoneResponse response = getMilestoneService.getByTitle(childEntity.getId(), title.toLowerCase(), parentEntity.getId());

        assertEquals(4, response.getMilestones().size());
        for (GetMilestoneResponse milestoneResponse : response.getMilestones()) {
            assertTrue(milestoneEntities.stream()
                    .anyMatch(milestoneEntity ->
                            milestoneEntity.getTitle().contains(milestoneResponse.getTitle())));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId());
        verify(milestonesRepository, times(1)).findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId());
        verify(getMilestoneConverter, times(response.getMilestones().size())).toDto(any(MilestoneEntity.class));
    }

    /**
     * Unit test for the getByTitle method in GetMilestoneService when accessed by an administrator
     * without providing a parent ID.
     * This test verifies that an AdminMissingParentIDException is thrown when the administrator
     * tries to retrieve a milestone for a child without providing a parent ID.
     * The test ensures that:
     * 1. The child is not authorized because the parent ID is missing.
     * 2. The AdminMissingParentIDException is thrown with the appropriate message.
     * 3. The MilestonesRepository is never queried if the authorization fails due to a missing parent ID.
     */
    @Test
    void when_adminGetsMilestoneByTitleWithoutParentID_then_adminMissingParentIDExceptionShouldBeThrown() {
        String title = "miLeStoNe";

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null))
                .thenThrow(new AdminMissingParentIDException("Admin must provide a parentID to perform this operation."));

        AdminMissingParentIDException adminMissingParentIDException = assertThrows(AdminMissingParentIDException.class,
                () -> getMilestoneService.getByTitle(childEntity.getId(), title, null));

        assertEquals("Admin must provide a parentID to perform this operation.", adminMissingParentIDException.getMessage());


        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, never()).findByTitleContainingIgnoreCaseAndChildId(anyString(), anyLong());
        verify(getMilestoneConverter, never()).toDto(any(MilestoneEntity.class));
    }

    /**
     * Unit test for the getByTitle method in GetMilestoneService when the requested milestone does not exist for the given child.
     * This test verifies that the service correctly handles the case where no milestones match the given title for a specific child.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. The MilestonesRepository is queried to find milestones associated with the child that contain the given title, ignoring case sensitivity.
     * 3. If no matching milestones are found, a MilestoneNotFoundException is thrown with the appropriate error message.
     */
    @Test
    void when_getByTitle_and_titleNonExistent_then_throwMilestoneNotFoundException() {
        String title = "titleNonExistent";

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(milestonesRepository.findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId())).thenReturn(Collections.emptyList());

        MilestoneNotFoundException milestoneNotFoundException = assertThrows(MilestoneNotFoundException.class,
                () -> getMilestoneService.getByTitle(childEntity.getId(), title.toLowerCase(), null));

        assertEquals("Milestone not found", milestoneNotFoundException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, times(1)).findByTitleContainingIgnoreCaseAndChildId(title.toLowerCase(), childEntity.getId());
        verify(getMilestoneConverter, never()).toDto(any(MilestoneEntity.class));
    }
}