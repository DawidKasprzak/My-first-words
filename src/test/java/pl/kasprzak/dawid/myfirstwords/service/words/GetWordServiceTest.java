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
import java.util.stream.Collectors;

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
    private WordEntity wordEntity1;
    private WordEntity wordEntity2;
    private WordEntity wordEntity3;
    private WordEntity wordEntity4;
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


        wordEntity1 = new WordEntity(1L, "word1", date.minusDays(1), childEntity);
        wordEntity2 = new WordEntity(2L, "word2", date.minusDays(2), childEntity);
        wordEntity3 = new WordEntity(3L, "word3", date.plusDays(1), childEntity);
        wordEntity4 = new WordEntity(4L, "word4", date.plusDays(2), childEntity);

        wordEntities = Arrays.asList(wordEntity1, wordEntity2, wordEntity3, wordEntity4);

    }

    @Test
    void when_getByDateAchieveBefore_then_wordsShouldBeReturnedBeforeTheGivenDate() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndDateAchieveBefore(childEntity.getId(), date)).thenReturn(wordEntities.subList(0, 2));
        when(getWordsConverter.toDto(any(WordEntity.class))).thenReturn(new GetWordResponse(0L, "testWord", LocalDate.now()));

        List<GetWordResponse> response = getWordService.getByDateAchieveBefore(childEntity.getId(), date, authentication);

        assertEquals(2, response.size());
        assertEquals("testWord", response.get(0).getWord());
        assertEquals(LocalDate.now(), response.get(0).getDateAchieve());

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, times(1)).findByChildIdAndDateAchieveBefore(childEntity.getId(), date);
        verify(getWordsConverter, times(2)).toDto(any(WordEntity.class));
    }

    @Test
    void when_getByDateAchieveAfter_then_wordsShouldBeReturnedAfterTheGivenDate() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndDateAchieveAfter(childEntity.getId(), date)).thenReturn(wordEntities.subList(2, 4));
        when(getWordsConverter.toDto(any(WordEntity.class))).thenReturn(new GetWordResponse(0L, "testWord", LocalDate.now()));

        List<GetWordResponse> response = getWordService.getByDateAchieveAfter(childEntity.getId(), date, authentication);

        assertEquals(2, response.size());
        assertEquals("testWord", response.get(0).getWord());
        assertEquals(LocalDate.now(), response.get(0).getDateAchieve());

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
        when(getWordsConverter.toDto(any(WordEntity.class))).thenReturn(new GetWordResponse(0L, "testWord", LocalDate.now()));

        List<GetWordResponse> response = getWordService.getWordsBetweenDays(childEntity.getId(), startDate, endDate, authentication);

        assertEquals(4, response.size());
        assertEquals("testWord", response.get(0).getWord());
        assertEquals(LocalDate.now(), response.get(0).getDateAchieve());

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
    void getWordById() {
    }

    @Test
    void when_getAllWords_then_allWordsTheChildShouldBeReturned() {
        List<GetWordResponse> expectedResponse = wordEntities.stream()
                .map(wordEntity -> new GetWordResponse(wordEntity.getId(), wordEntity.getWord(), wordEntity.getDateAchieve()))
                .collect(Collectors.toList());

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findAllByChildId(childEntity.getId())).thenReturn(wordEntities);
        for (int i = 0; i < wordEntities.size(); i++) {
            when(getWordsConverter.toDto(wordEntities.get(i))).thenReturn(expectedResponse.get(i));
        }

        GetAllWordsResponse response = getWordService.getAllWords(childEntity.getId(), authentication);

        assertEquals(expectedResponse, response.getWords());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, times(1)).findAllByChildId(childEntity.getId());
        for (int i = 0; i < wordEntities.size(); i++) {
            verify(getWordsConverter, times(1)).toDto(wordEntities.get(i));
        }
    }


    @Test
    void when_getByWord_then_theChildWordShouldBeReturned() {
        String word = "word1";

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId())).thenReturn(Optional.of(wordEntity1));
        when(getWordsConverter.toDto(wordEntity1)).thenReturn(new GetWordResponse(wordEntity1.getId(), wordEntity1.getWord(), wordEntity1.getDateAchieve()));

        GetWordResponse response = getWordService.getByWord(childEntity.getId(), word, authentication);

        assertNotNull(response);
        assertEquals(word, response.getWord());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, times(1)).findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId());
        verify(getWordsConverter, times(1)).toDto(wordEntity1);

    }

    @Test
    void when_getByWord_and_wordNotExist_then_throwWordNotFoundException() {
        String word = "word1";

        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId())).thenReturn(Optional.empty());

        WordNotFoundException wordNotFoundException = assertThrows(WordNotFoundException.class,
                () -> getWordService.getByWord(childEntity.getId(), word, authentication));

        assertEquals("Word not found", wordNotFoundException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, times(1)).findByWordIgnoreCaseAndChildId(word.toLowerCase(), childEntity.getId());
        verify(getWordsConverter, never()).toDto(any(WordEntity.class));

    }
}