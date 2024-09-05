package pl.kasprzak.dawid.myfirstwords.service.words;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.exception.AdminMissingParentIDException;
import pl.kasprzak.dawid.myfirstwords.exception.DateValidationException;
import pl.kasprzak.dawid.myfirstwords.exception.InvalidDateOrderException;
import pl.kasprzak.dawid.myfirstwords.exception.WordNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.words.GetAllWordsResponse;
import pl.kasprzak.dawid.myfirstwords.model.words.GetWordResponse;
import pl.kasprzak.dawid.myfirstwords.repository.WordsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.words.GetWordsConverter;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetWordServiceTest {

    @Mock
    private AuthorizationHelper authorizationHelper;
    @Mock
    private WordsRepository wordsRepository;
    @Mock
    private GetWordsConverter getWordsConverter;
    @InjectMocks
    private GetWordService getWordService;

    private ParentEntity parentEntity;
    private ChildEntity childEntity;
    private List<WordEntity> wordEntities;
    private WordEntity wordEntity1;
    private LocalDate date;

    @BeforeEach
    void setUp() {

        parentEntity = new ParentEntity();
        parentEntity.setId(1L);
        parentEntity.setUsername("parentName");
        parentEntity.setPassword("password");

        childEntity = new ChildEntity();
        childEntity.setName("childName");
        childEntity.setParent(parentEntity);

        date = LocalDate.of(2024, 5, 5);

        wordEntity1 = new WordEntity();
        wordEntity1.setId(1L);
        wordEntity1.setWord("word1");
        wordEntity1.setDateAchieve(date.minusDays(1));
        wordEntity1.setChild(childEntity);

        WordEntity wordEntity2 = new WordEntity();
        wordEntity2.setId(2L);
        wordEntity2.setWord("word2");
        wordEntity2.setDateAchieve(date.minusDays(2));
        wordEntity2.setChild(childEntity);

        WordEntity wordEntity3 = new WordEntity();
        wordEntity3.setId(3L);
        wordEntity3.setWord("word3");
        wordEntity3.setDateAchieve(date.plusDays(1));
        wordEntity3.setChild(childEntity);

        WordEntity wordEntity4 = new WordEntity();
        wordEntity4.setId(4L);
        wordEntity4.setWord("word4");
        wordEntity4.setDateAchieve(date.plusDays(2));
        wordEntity4.setChild(childEntity);

        wordEntities = Arrays.asList(wordEntity1, wordEntity2, wordEntity3, wordEntity4);
    }

    private GetWordResponse createGetWordResponse(WordEntity entity) {
        return GetWordResponse.builder()
                .id(entity.getId())
                .word(entity.getWord())
                .dateAchieve(entity.getDateAchieve())
                .build();
    }

    /**
     * Unit test for the getByDateAchieveBefore method in GetWordService.
     * This test verifies that the service correctly retrieves words achieved before a specified date
     * for a given child and converts them to DTOs.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. The WordsRepository is queried to find words associated with the child that were achieved before the specified date.
     * 3. Each retrieved WordEntity is converted to a GetWordResponse DTO using the GetWordsConverter.
     */
    @Test
    void when_getByDateAchieveBefore_then_wordsShouldBeReturnedBeforeTheGivenDate() {
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndDateAchieveBefore(childEntity.getId(), date)).thenReturn(wordEntities.subList(0, 2));
        // Mock the behavior of getWordsConverter.toDto method to ensure that any WordEntity passed to it
        // is converted to a GetWordResponse using a predefined conversion method, createGetWordResponse.
        when(getWordsConverter.toDto(any(WordEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a WordEntity object
            WordEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetWordResponse to convert the WordEntity object to a GetWordResponse
            return createGetWordResponse(entity);
        });

        List<GetWordResponse> response = getWordService.getByDateAchieveBefore(childEntity.getId(), date, null);

        assertEquals(2, response.size());
        for (GetWordResponse wordResponse : response) {
            assertTrue(wordResponse.getDateAchieve().isBefore(date));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(wordsRepository, times(1)).findByChildIdAndDateAchieveBefore(childEntity.getId(), date);
        verify(getWordsConverter, times(2)).toDto(any(WordEntity.class));
    }

    /**
     * Unit test for the getByDateAchieveBefore method in GetWordService when accessed by an administrator.
     * This test verifies that the service correctly retrieves words achieved before a specified date
     * for a given child when the request is made by an administrator.
     * The test ensures that:
     * 1. The child is validated and authorized for the administrator using the AuthorizationHelper
     * with a provided parent ID.
     * 2. The WordsRepository is queried to find words associated with the child that were achieved
     * before the specified date.
     * 3. Each retrieved WordEntity is converted to a GetWordResponse DTO using the GetWordsConverter.
     */
    @Test
    void when_adminGetsWordsByDateAchieveBefore_then_wordsShouldBeReturnedBeforeTheGivenDate() {
        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId())).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndDateAchieveBefore(childEntity.getId(), date)).thenReturn(wordEntities.subList(0, 2));

        when(getWordsConverter.toDto(any(WordEntity.class))).thenAnswer(invocationOnMock -> {
            WordEntity entity = invocationOnMock.getArgument(0);
            return createGetWordResponse(entity);
        });

        List<GetWordResponse> responses = getWordService.getByDateAchieveBefore(childEntity.getId(), date, parentEntity.getId());

        assertEquals(2, responses.size());
        for (GetWordResponse wordResponse : responses) {
            assertTrue(wordResponse.getDateAchieve().isBefore(date));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId());
        verify(wordsRepository, times(1)).findByChildIdAndDateAchieveBefore(childEntity.getId(), date);
        verify(getWordsConverter, times(2)).toDto(any(WordEntity.class));
    }

    /**
     * Unit test for the getByDateAchieveBefore method in GetWordService when accessed by an administrator without a parent ID.
     * This test verifies that the service correctly throws an AdminMissingParentIDException when the admin
     * attempts to retrieve words achieved before a specified date without providing a parent ID.
     * The test ensures that:
     * 1. The AuthorizationHelper correctly identifies the current user as an admin.
     * 2. The validateAndAuthorizeForAdminOrParent method throws an AdminMissingParentIDException when no parent ID is provided.
     * 3. The WordsRepository is never queried if an exception is thrown.
     */
    @Test
    void when_adminGetsWordsByDateAchieveBeforeWithoutParentID_then_adminMissingParentIDExceptionShouldBeThrown() {
        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null))
                .thenThrow(new AdminMissingParentIDException("Admin must provide a parentID to retrieve children"));

        AdminMissingParentIDException adminMissingParentIDException = assertThrows(AdminMissingParentIDException.class,
                () -> getWordService.getByDateAchieveBefore(childEntity.getId(), date, null));

        assertEquals("Admin must provide a parentID to retrieve children", adminMissingParentIDException.getMessage());

        verify(authorizationHelper, never()).validateAndAuthorizeForAdminOrParent(anyLong(), anyLong());
        verify(wordsRepository, never()).findByChildIdAndDateAchieveBefore(childEntity.getId(), date);
    }

    /**
     * Unit test for the getByDateAchieveAfter method in GetWordService.
     * This test verifies that the service correctly retrieves words achieved after a specified date
     * for a given child and converts them to DTOs.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. The WordsRepository is queried to find words associated with the child that were achieved after the specified date.
     * 3. Each retrieved WordEntity is converted to a GetWordResponse DTO using the GetWordsConverter.
     */
    @Test
    void when_getByDateAchieveAfter_then_wordsShouldBeReturnedAfterTheGivenDate() {
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndDateAchieveAfter(childEntity.getId(), date)).thenReturn(wordEntities.subList(2, 4));
        // Mock the behavior of getWordsConverter.toDto method to ensure that any WordEntity passed to it
        // is converted to a GetWordResponse using a predefined conversion method, createGetWordResponse.
        when(getWordsConverter.toDto(any(WordEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a WordEntity object
            WordEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetWordResponse to convert the WordEntity object to a GetWordResponse
            return createGetWordResponse(entity);
        });

        List<GetWordResponse> response = getWordService.getByDateAchieveAfter(childEntity.getId(), date, null);

        assertEquals(2, response.size());
        for (GetWordResponse wordResponse : response) {
            assertTrue(wordResponse.getDateAchieve().isAfter(date));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(wordsRepository, times(1)).findByChildIdAndDateAchieveAfter(childEntity.getId(), date);
        verify(getWordsConverter, times(2)).toDto(any(WordEntity.class));
    }

    /**
     * Unit test for the getByDateAchieveAfter method in GetWordService when accessed by an administrator.
     * This test verifies that the service correctly retrieves words achieved after a specified date
     * for a given child when the request is made by an administrator.
     * The test ensures that:
     * 1. The child is validated and authorized for the administrator using the AuthorizationHelper
     * with a provided parent ID.
     * 2. The WordsRepository is queried to find words associated with the child that were achieved
     * after the specified date.
     * 3. Each retrieved WordEntity is converted to a GetWordResponse DTO using the GetWordsConverter.
     */
    @Test
    void when_adminGetsWordsByDateAchieveAfter_then_wordsShouldBeReturnedAfterTheGivenDate() {
        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId())).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndDateAchieveAfter(childEntity.getId(), date)).thenReturn(wordEntities.subList(2, 4));

        when(getWordsConverter.toDto(any(WordEntity.class))).thenAnswer(invocationOnMock -> {
            WordEntity entity = invocationOnMock.getArgument(0);
            return createGetWordResponse(entity);
        });

        List<GetWordResponse> responses = getWordService.getByDateAchieveAfter(childEntity.getId(), date, parentEntity.getId());

        assertEquals(2, responses.size());
        for (GetWordResponse wordResponse : responses) {
            assertTrue(wordResponse.getDateAchieve().isAfter(date));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId());
        verify(wordsRepository, times(1)).findByChildIdAndDateAchieveAfter(childEntity.getId(), date);
        verify(getWordsConverter, times(2)).toDto(any(WordEntity.class));
    }

    /**
     * Unit test for the getByDateAchieveAfter method in GetWordService when accessed by an administrator without a parent ID.
     * This test verifies that the service correctly throws an AdminMissingParentIDException when the admin
     * attempts to retrieve words achieved after a specified date without providing a parent ID.
     * The test ensures that:
     * 1. The AuthorizationHelper correctly identifies the current user as an admin.
     * 2. The validateAndAuthorizeForAdminOrParent method throws an AdminMissingParentIDException when no parent ID is provided.
     * 3. The WordsRepository is never queried if an exception is thrown.
     */
    @Test
    void when_adminGetsWordsByDateAchieveAfterWithoutParentID_then_adminMissingParentIDExceptionShouldBeThrown() {
        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null))
                .thenThrow(new AdminMissingParentIDException("Admin must provide a parentID to retrieve children"));

        AdminMissingParentIDException adminMissingParentIDException = assertThrows(AdminMissingParentIDException.class,
                () -> getWordService.getByDateAchieveAfter(childEntity.getId(), date, null));

        assertEquals("Admin must provide a parentID to retrieve children", adminMissingParentIDException.getMessage());

        verify(authorizationHelper, never()).validateAndAuthorizeForAdminOrParent(anyLong(), anyLong());
        verify(wordsRepository, never()).findByChildIdAndDateAchieveAfter(childEntity.getId(), date);
    }

    /**
     * Unit test for the getWordsBetweenDays method in GetWordService.
     * This test verifies that the service correctly retrieves words achieved between a specified dates
     * for a given child and converts them to DTOs.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. The WordsRepository is queried to find words associated with the child that were achieved between the specified date.
     * 3. Each retrieved WordEntity is converted to a GetWordResponse DTO using the GetWordsConverter.
     */
    @Test
    void when_getWordsBetweenDays_then_wordsShouldBeReturnedBetweenTheGivenDates() {
        LocalDate startDate = date.minusDays(2);
        LocalDate endDate = date.plusDays(2);

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndDateAchieveBetween(childEntity.getId(), startDate, endDate)).thenReturn(wordEntities);
        // Mock the behavior of getWordsConverter.toDto method to ensure that any WordEntity passed to it
        // is converted to a GetWordResponse using a predefined conversion method, createGetWordResponse.
        when(getWordsConverter.toDto(any(WordEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a WordEntity object
            WordEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetWordResponse to convert the WordEntity object to a GetWordResponse
            return createGetWordResponse(entity);
        });

        List<GetWordResponse> response = getWordService.getWordsBetweenDays(childEntity.getId(), startDate, endDate, null);

        assertEquals(4, response.size());
        for (GetWordResponse wordResponse : response) {
            assertTrue(wordResponse.getDateAchieve().isAfter(startDate.minusDays(1))
                    && wordResponse.getDateAchieve().isBefore(endDate.plusDays(1)));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(wordsRepository, times(1)).findByChildIdAndDateAchieveBetween(childEntity.getId(), startDate, endDate);
        verify(getWordsConverter, times(4)).toDto(any(WordEntity.class));
    }

    /**
     * Unit test for the getWordsBetweenDays method in GetWordService when accessed by an administrator.
     * This test verifies that the service correctly retrieves words achieved between specified dates
     * for a given child when the request is made by an administrator, providing a parent ID.
     * The test ensures that:
     * 1. The child is validated and authorized for the administrator using the AuthorizationHelper with a provided parent ID.
     * 2. The WordsRepository is queried to find words associated with the child that were achieved between the specified dates.
     * 3. Each retrieved WordEntity is converted to a GetWordResponse DTO using the GetWordsConverter.
     */
    @Test
    void when_adminGetsWordsBetweenDays_then_wordsShouldBeReturnedBetweenTheGivenDates() {
        LocalDate startDate = date.minusDays(2);
        LocalDate endDate = date.plusDays(2);

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId())).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndDateAchieveBetween(childEntity.getId(), startDate, endDate)).thenReturn(wordEntities);
        when(getWordsConverter.toDto(any(WordEntity.class))).thenAnswer(invocationOnMock -> {
            WordEntity entity = invocationOnMock.getArgument(0);
            return createGetWordResponse(entity);
        });

        List<GetWordResponse> response = getWordService.getWordsBetweenDays(childEntity.getId(), startDate, endDate, parentEntity.getId());

        assertEquals(4, response.size());
        for (GetWordResponse wordResponse : response) {
            assertTrue(wordResponse.getDateAchieve().isAfter(startDate.minusDays(1))
                    && wordResponse.getDateAchieve().isBefore(endDate.plusDays(1)));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId());
        verify(wordsRepository, times(1)).findByChildIdAndDateAchieveBetween(childEntity.getId(), startDate, endDate);
        verify(getWordsConverter, times(4)).toDto(any(WordEntity.class));
    }

    /**
     * Unit test for the getWordsBetweenDays method in GetWordService when accessed by an administrator
     * but without providing a parent ID.
     * This test verifies that an AdminMissingParentIDException is thrown when the administrator
     * tries to retrieve words for a child without providing a parent ID.
     * The test ensures that:
     * 1. The child is not authorized because the parent ID is missing.
     * 2. The AdminMissingParentIDException is thrown with the appropriate message.
     */
    @Test
    void when_adminDoesNotProvideParentID_then_adminMissingParentIDExceptionShouldBeThrown() {
        LocalDate startDate = date.minusDays(2);
        LocalDate endDate = date.plusDays(2);

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null))
                .thenThrow(new AdminMissingParentIDException("Admin must provide a parentID to retrieve children"));

        AdminMissingParentIDException adminMissingParentIDException = assertThrows(AdminMissingParentIDException.class,
                () -> getWordService.getWordsBetweenDays(childEntity.getId(), startDate, endDate, null));

        assertEquals("Admin must provide a parentID to retrieve children", adminMissingParentIDException.getMessage());

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(wordsRepository, never()).findByChildIdAndDateAchieveBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
        verify(getWordsConverter, never()).toDto(any(WordEntity.class));
    }

    /**
     * Unit test for the getWordsBetweenDays method in GetWordService when the start date is null.
     * This test verifies that the service throws a DateValidationException if the start date is not provided.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. A DateValidationException is thrown if the start date is null.
     * 3. The WordsRepository is never queried if the validation fails due to a missing start date.
     * 4. The appropriate error message ("Start date and end date must not be null") is returned when the exception is thrown.
     */
    @Test
    void when_getWordsBetweenDays_and_startDateIsNull_then_throwDateValidationException() {
        LocalDate endDate = date.plusDays(2);

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);

        DateValidationException dateValidationException = assertThrows(DateValidationException.class,
                () -> getWordService.getWordsBetweenDays(childEntity.getId(), null, endDate, null));

        assertEquals("Start date and end date must not be null", dateValidationException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(wordsRepository, never()).findByChildIdAndDateAchieveBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }

    /**
     * Unit test for the getWordsBetweenDays method in GetWordService when the end date is null.
     * This test verifies that the service throws a DateValidationException if the end date is not provided.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. A DateValidationException is thrown if the end date is null.
     * 3. The WordsRepository is never queried if the validation fails due to a missing end date.
     * 4. The appropriate error message ("Start date and end date must not be null") is returned when the exception is thrown.
     */
    @Test
    void when_getWordsBetweenDays_and_endDateIsNull_then_throwDateValidationException() {
        LocalDate startDate = date.minusDays(2);

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);

        DateValidationException dateValidationException = assertThrows(DateValidationException.class,
                () -> getWordService.getWordsBetweenDays(childEntity.getId(), startDate, null, null));

        assertEquals("Start date and end date must not be null", dateValidationException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(wordsRepository, never()).findByChildIdAndDateAchieveBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }

    /**
     * Unit test for the getWordsBetweenDays method in GetWordService when the start date is after end date.
     * This test verifies that the service throws a InvalidDateOrderException if the start date is after end date.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. A InvalidDateOrderException is thrown if the start date is after end date.
     * 3. The WordsRepository is never queried if the validation fails due to start date is after end date.
     * 4. The appropriate error message ("Start date must be before or equal to end date") is returned when the exception is thrown.
     */
    @Test
    void when_getWordsBetweenDays_and_startDateIsAfterEndDate_then_throwInvalidDateOrderException() {
        LocalDate startDate = date.plusDays(2);
        LocalDate endDate = date.minusDays(2);

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);

        InvalidDateOrderException invalidDateOrderException = assertThrows(InvalidDateOrderException.class,
                () -> getWordService.getWordsBetweenDays(childEntity.getId(), startDate, endDate, null));

        assertEquals("Start date must be before or equal to end date", invalidDateOrderException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(wordsRepository, never()).findByChildIdAndDateAchieveBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }

    /**
     * Unit test for getAllWords method in GetWordService.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that all words for the child are retrieved and converted to DTOs.
     */
    @Test
    void when_getAllWords_then_allWordsTheChildShouldBeReturned() {

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId())).thenReturn(childEntity);
        when(wordsRepository.findAllByChildId(childEntity.getId())).thenReturn(wordEntities);
        // Mock the behavior of getWordsConverter.toDto method to ensure that any WordEntity passed to it
        // is converted to a GetWordResponse using a predefined conversion method, createGetWordResponse.
        when(getWordsConverter.toDto(any(WordEntity.class))).thenAnswer(invocationOnMock -> {
            // Extract the argument passed to the toDto method, which is a WordEntity object
            WordEntity entity = invocationOnMock.getArgument(0);
            // Use the helper method createGetWordResponse to convert the WordEntity object to a GetWordResponse
            return createGetWordResponse(entity);
        });

        GetAllWordsResponse response = getWordService.getAllWords(childEntity.getId());

        assertEquals(wordEntities.size(), response.getWords().size());

        for (GetWordResponse wordResponse : response.getWords()) {
            assertTrue(wordEntities.stream().anyMatch(entity ->
                    entity.getId().equals(wordResponse.getId()) &&
                            entity.getWord().equals(wordResponse.getWord()) &&
                            entity.getDateAchieve().equals(wordResponse.getDateAchieve())));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId());
        verify(wordsRepository, times(1)).findAllByChildId(childEntity.getId());
        verify(getWordsConverter, times(wordEntities.size())).toDto(any(WordEntity.class));
    }

    /**
     * Unit test for the getByWord method in GetWordService.
     * This test verifies that the service correctly retrieves a word for a given child based on the exact word provided.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. The WordsRepository is queried to find the word associated with the child, ignoring case sensitivity.
     * 3. The retrieved WordEntity is converted to a GetWordResponse DTO using the GetWordsConverter.
     */
    @Test
    void when_getByWord_then_theChildWordShouldBeReturned() {
        String word = "word1";

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(wordsRepository.findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId())).thenReturn(Optional.of(wordEntity1));
        when(getWordsConverter.toDto(wordEntity1)).thenReturn(
                GetWordResponse.builder()
                        .id(wordEntity1.getId())
                        .word(wordEntity1.getWord())
                        .dateAchieve(wordEntity1.getDateAchieve())
                        .build());

        GetWordResponse response = getWordService.getByWord(childEntity.getId(), word, null);

        assertNotNull(response);
        assertEquals(word, response.getWord());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(wordsRepository, times(1)).findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId());
        verify(getWordsConverter, times(1)).toDto(wordEntity1);

    }

    /**
     * Unit test for the getByWord method in GetWordService when accessed by an administrator.
     * This test verifies that the service correctly retrieves a specific word for a given child
     * when the request is made by an administrator, providing a parent ID.
     * The test ensures that:
     * 1. The child is validated and authorized for the administrator using the AuthorizationHelper with a provided parent ID.
     * 2. The WordsRepository is queried to find the word associated with the child, ignoring case sensitivity.
     * 3. The retrieved WordEntity is converted to a GetWordResponse DTO using the GetWordsConverter.
     */
    @Test
    void when_adminGetsWordByExactMatch_then_theChildWordShouldBeReturned() {
        String word = "word1";

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId())).thenReturn(childEntity);
        when(wordsRepository.findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId())).thenReturn(Optional.of(wordEntity1));
        when(getWordsConverter.toDto(wordEntity1)).thenReturn(
                GetWordResponse.builder()
                        .id(wordEntity1.getId())
                        .word(wordEntity1.getWord())
                        .dateAchieve(wordEntity1.getDateAchieve())
                        .build());

        GetWordResponse response = getWordService.getByWord(childEntity.getId(), word, parentEntity.getId());

        assertEquals(word, response.getWord());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentEntity.getId());
        verify(wordsRepository, times(1)).findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId());
        verify(getWordsConverter, times(1)).toDto(wordEntity1);

    }

    /**
     * Unit test for the getByWord method in GetWordService when accessed by an administrator
     * without providing a parent ID.
     * This test verifies that an AdminMissingParentIDException is thrown when the administrator
     * tries to retrieve a word for a child without providing a parent ID.
     * The test ensures that:
     * 1. The child is not authorized because the parent ID is missing.
     * 2. The AdminMissingParentIDException is thrown with the appropriate message.
     * 3. The WordsRepository is never queried if the authorization fails due to a missing parent ID.
     */
    @Test
    void when_adminGetsWordByExactMatch_then_adminMissingParentIDExceptionShouldBeThrown() {
        String word = "word1";

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null))
                .thenThrow(new AdminMissingParentIDException("Admin must provide a parentID to retrieve children"));

        AdminMissingParentIDException adminMissingParentIDException = assertThrows(AdminMissingParentIDException.class,
                () -> getWordService.getByWord(childEntity.getId(), word, null));

        assertEquals("Admin must provide a parentID to retrieve children", adminMissingParentIDException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(wordsRepository, never()).findByWordIgnoreCaseAndChildId(anyString(), anyLong());
        verify(getWordsConverter, never()).toDto(any(WordEntity.class));

    }

    /**
     * Unit test for the getByWord method in GetWordService when the requested word does not exist for the given child.
     * This test verifies that the service correctly handles the case where the word is not found for a given child ID.
     * The test ensures that:
     * 1. The child is validated and authorized using the AuthorizationHelper for either the authenticated parent or administrator.
     * 2. The WordsRepository is queried to find the word associated with the child, ignoring case sensitivity.
     * 3. If the word is not found, a WordNotFoundException is thrown with the appropriate error message.
     */
    @Test
    void when_getByWord_and_wordNotExist_then_throwWordNotFoundException() {
        String word = "nonExistentWord";

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(wordsRepository.findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId())).thenReturn(Optional.empty());

        WordNotFoundException wordNotFoundException = assertThrows(WordNotFoundException.class,
                () -> getWordService.getByWord(childEntity.getId(), word, null));

        assertEquals("Word not found", wordNotFoundException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(wordsRepository, times(1)).findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId());
        verify(getWordsConverter, never()).toDto(any(WordEntity.class));
    }
}