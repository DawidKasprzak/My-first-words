package pl.kasprzak.dawid.myfirstwords.service.words;

import jakarta.persistence.GeneratedValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
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

        wordEntities = Arrays.asList(
                new WordEntity(1L, "word1", date.minusDays(1), childEntity),
                new WordEntity(2L, "word2", date.minusDays(2), childEntity),
                new WordEntity(3L, "word3", date.plusDays(1), childEntity),
                new WordEntity(4L, "word4", date.plusDays(2), childEntity)
        );

    }

    @Test
    void when_getByDateAchieveBefore_then_wordsShouldBeReturnedBeforeTheGivenDate() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndDateAchieveBefore(childEntity.getId(), date)).thenReturn(wordEntities.subList(0, 2));
        when(getWordsConverter.toDto(any(WordEntity.class))).thenReturn(new GetWordResponse());

        List<GetWordResponse> response = getWordService.getByDateAchieveBefore(childEntity.getId(), date, authentication);

        assertEquals(2, response.size());

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, times(1)).findByChildIdAndDateAchieveBefore(childEntity.getId(), date);
        verify(getWordsConverter, times(2)).toDto(any(WordEntity.class));
    }

    @Test
    void when_getByDateAchieveAfter_then_wordsShouldBeReturnedAfterTheGivenDate() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndDateAchieveAfter(childEntity.getId(), date)).thenReturn(wordEntities.subList(2, 4));
        when(getWordsConverter.toDto(any(WordEntity.class))).thenReturn(new GetWordResponse());

        List<GetWordResponse> response = getWordService.getByDateAchieveAfter(childEntity.getId(), date, authentication);

        assertEquals(2, response.size());

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, times(1)).findByChildIdAndDateAchieveAfter(childEntity.getId(), date);
        verify(getWordsConverter, times(2)).toDto(any(WordEntity.class));
    }

    @Test
    void getWordsBetweenDays() {
    }

    @Test
    void getByWord() {
    }

    @Test
    void getWordById() {
    }

    @Test
    void getAllWords() {
    }
}