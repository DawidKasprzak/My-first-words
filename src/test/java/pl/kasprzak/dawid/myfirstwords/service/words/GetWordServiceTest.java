package pl.kasprzak.dawid.myfirstwords.service.words;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
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
    private Authentication authentication;
    @Mock
    private WordsRepository wordsRepository;
    @Mock
    private GetWordsConverter getWordsConverter;
    @InjectMocks
    private GetWordService getWordService;

    private ParentEntity parentEntity;
    private ChildEntity childEntity;
    private List<WordEntity> wordEntities;
    private WordEntity wordEntity1, wordEntity2, wordEntity3, wordEntity4;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        parentEntity = new ParentEntity();
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

        wordEntity2 = new WordEntity();
        wordEntity2.setId(2L);
        wordEntity2.setWord("word2");
        wordEntity2.setDateAchieve(date.minusDays(2));
        wordEntity2.setChild(childEntity);

        wordEntity3 = new WordEntity();
        wordEntity3.setId(3L);
        wordEntity3.setWord("word3");
        wordEntity3.setDateAchieve(date.plusDays(1));
        wordEntity3.setChild(childEntity);

        wordEntity4 = new WordEntity();
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

    @Test
    void when_getByDateAchieveBefore_then_wordsShouldBeReturnedBeforeTheGivenDate() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndDateAchieveBefore(childEntity.getId(), date)).thenReturn(wordEntities.subList(0, 2));
        when(getWordsConverter.toDto(any(WordEntity.class))).thenAnswer(invocationOnMock -> {
            WordEntity entity = invocationOnMock.getArgument(0);
            return createGetWordResponse(entity);
        });

        List<GetWordResponse> response = getWordService.getByDateAchieveBefore(childEntity.getId(), date, authentication);

        assertEquals(2, response.size());
        for (GetWordResponse wordResponse : response) {
            assertTrue(wordResponse.getDateAchieve().isBefore(date));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, times(1)).findByChildIdAndDateAchieveBefore(childEntity.getId(), date);
        verify(getWordsConverter, times(2)).toDto(any(WordEntity.class));
    }

    @Test
    void when_getByDateAchieveAfter_then_wordsShouldBeReturnedAfterTheGivenDate() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndDateAchieveAfter(childEntity.getId(), date)).thenReturn(wordEntities.subList(2, 4));
        when(getWordsConverter.toDto(any(WordEntity.class))).thenAnswer(invocationOnMock -> {
            WordEntity entity = invocationOnMock.getArgument(0);
            return createGetWordResponse(entity);
        });

        List<GetWordResponse> response = getWordService.getByDateAchieveAfter(childEntity.getId(), date, authentication);

        assertEquals(2, response.size());
        for (GetWordResponse wordResponse : response) {
            assertTrue(wordResponse.getDateAchieve().isAfter(date));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, times(1)).findByChildIdAndDateAchieveAfter(childEntity.getId(), date);
        verify(getWordsConverter, times(2)).toDto(any(WordEntity.class));
    }

    @Test
    void when_getWordsBetweenDays_then_wordsShouldBeReturnedBetweenTheGivenDates() {
        LocalDate startDate = date.minusDays(2);
        LocalDate endDate = date.plusDays(2);

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndDateAchieveBetween(childEntity.getId(), startDate, endDate)).thenReturn(wordEntities);
        when(getWordsConverter.toDto(any(WordEntity.class))).thenAnswer(invocationOnMock -> {
            WordEntity entity = invocationOnMock.getArgument(0);
            return createGetWordResponse(entity);
        });

        List<GetWordResponse> response = getWordService.getWordsBetweenDays(childEntity.getId(), startDate, endDate, authentication);

        assertEquals(4, response.size());
        for (GetWordResponse wordResponse : response) {
            assertTrue(wordResponse.getDateAchieve().isAfter(startDate.minusDays(1))
                    && wordResponse.getDateAchieve().isBefore(endDate.plusDays(1)));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, times(1)).findByChildIdAndDateAchieveBetween(childEntity.getId(), startDate, endDate);
        verify(getWordsConverter, times(4)).toDto(any(WordEntity.class));
    }

    @Test
    void when_getWordsBetweenDays_and_startDateIsNull_then_throwDateValidationException() {
        LocalDate endDate = date.plusDays(2);

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);

        DateValidationException dateValidationException = assertThrows(DateValidationException.class,
                () -> getWordService.getWordsBetweenDays(childEntity.getId(), null, endDate, authentication));

        assertEquals("Start date and end date must not be null", dateValidationException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, never()).findByChildIdAndDateAchieveBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void when_getWordsBetweenDays_and_endDateIsNull_then_throwDateValidationException() {
        LocalDate startDate = date.minusDays(2);

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);

        DateValidationException dateValidationException = assertThrows(DateValidationException.class,
                () -> getWordService.getWordsBetweenDays(childEntity.getId(), startDate, null, authentication));

        assertEquals("Start date and end date must not be null", dateValidationException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, never()).findByChildIdAndDateAchieveBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void when_getWordsBetweenDays_and_startDateIsAfterEndDate_then_throwInvalidDateOrderException() {
        LocalDate startDate = date.plusDays(2);
        LocalDate endDate = date.minusDays(2);

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);

        InvalidDateOrderException invalidDateOrderException = assertThrows(InvalidDateOrderException.class,
                () -> getWordService.getWordsBetweenDays(childEntity.getId(), startDate, endDate, authentication));

        assertEquals("Start date must be before or equal to end date", invalidDateOrderException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, never()).findByChildIdAndDateAchieveBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void when_getAllWords_then_allWordsTheChildShouldBeReturned() {

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findAllByChildId(childEntity.getId())).thenReturn(wordEntities);
        when(getWordsConverter.toDto(any(WordEntity.class))).thenAnswer(invocationOnMock -> {
            WordEntity entity = invocationOnMock.getArgument(0);
            return createGetWordResponse(entity);
        });

        GetAllWordsResponse response = getWordService.getAllWords(childEntity.getId(), authentication);

        assertEquals(wordEntities.size(), response.getWords().size());

        for (GetWordResponse wordResponse : response.getWords()) {
            assertTrue(wordEntities.stream().anyMatch(entity ->
                    entity.getId().equals(wordResponse.getId()) &&
                            entity.getWord().equals(wordResponse.getWord()) &&
                            entity.getDateAchieve().equals(wordResponse.getDateAchieve())));
        }

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, times(1)).findAllByChildId(childEntity.getId());
        verify(getWordsConverter, times(wordEntities.size())).toDto(any(WordEntity.class));
    }


    /**
     * Unit test for getByWord method.
     * Verifies that the correct word is returned for a given child ID and word.
     * Also verifies that the child belongs to the authenticated parent.
     */
    @Test
    void when_getByWord_then_theChildWordShouldBeReturned() {
        // Define the word to search for
        String word = "word1";

        // Mock the behavior of the authorization, repository and conversion methods
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId())).thenReturn(Optional.of(wordEntity1));
        when(getWordsConverter.toDto(wordEntity1)).thenReturn(
                GetWordResponse.builder()
                        .id(wordEntity1.getId())
                        .word(wordEntity1.getWord())
                        .dateAchieve(wordEntity1.getDateAchieve())
                        .build());

        // Call the service method
        GetWordResponse response = getWordService.getByWord(childEntity.getId(), word, authentication);

        // Assert the response and verify the interactions
        assertNotNull(response);
        assertEquals(word, response.getWord());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, times(1)).findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId());
        verify(getWordsConverter, times(1)).toDto(wordEntity1);

    }

    /**
     * Unit test for getByWord method when the word does not exist.
     * Verifies that a WordNotFoundException is thrown and the appropriate error message is returned.
     * Also verifies that the child belongs to the authenticated parent.
     */
    @Test
    void when_getByWord_and_wordNotExist_then_throwWordNotFoundException() {
        // Define the word to search for
        String word = "nonExistentWord";

        // Mock the behavior of the authorization and repository methods
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId())).thenReturn(Optional.empty());

        // Assert that the WordNotFoundException is thrown
        WordNotFoundException wordNotFoundException = assertThrows(WordNotFoundException.class,
                () -> getWordService.getByWord(childEntity.getId(), word, authentication));

        // Assert the exception message and verify the interaction
        assertEquals("Word not found", wordNotFoundException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, times(1)).findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId());
        verify(getWordsConverter, never()).toDto(any(WordEntity.class));
    }
}