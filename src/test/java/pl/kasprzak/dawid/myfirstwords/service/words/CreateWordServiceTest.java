package pl.kasprzak.dawid.myfirstwords.service.words;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordRequest;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordResponse;
import pl.kasprzak.dawid.myfirstwords.repository.WordsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.words.CreateWordConverter;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateWordServiceTest {


    @Mock
    private AuthorizationHelper authorizationHelper;
    @Mock
    private Authentication authentication;
    @Mock
    private WordsRepository wordsRepository;
    @Mock
    private CreateWordConverter createWordConverter;
    @InjectMocks
    private CreateWordService createWordService;

    private ChildEntity childEntity;
    private CreateWordRequest createWordRequest;
    private WordEntity wordEntity;
    private CreateWordResponse createWordResponse;

    @BeforeEach
    void setUp() {

        childEntity = new ChildEntity();
        childEntity.setId(1L);
        childEntity.setName("childName");

        createWordRequest = CreateWordRequest.builder()
                .word("wordTest")
                .build();

        createWordResponse = CreateWordResponse.builder()
                .id(1L)
                .word(createWordRequest.getWord())
                .build();

        wordEntity = new WordEntity();
        wordEntity.setId(createWordResponse.getId());
        wordEntity.setWord(createWordResponse.getWord());
        wordEntity.setChild(childEntity);

    }

    @Test
    void when_addWord_then_wordShouldBeAddedToTheChild() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(createWordConverter.fromDto(createWordRequest)).thenReturn(wordEntity);
        when(wordsRepository.save(wordEntity)).thenReturn(wordEntity);
        when(createWordConverter.toDto(wordEntity)).thenReturn(createWordResponse);

        CreateWordResponse response = createWordService.addWord(childEntity.getId(), createWordRequest, authentication);

        assertEquals(createWordResponse, response);
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(createWordConverter, times(1)).fromDto(createWordRequest);
        verify(wordsRepository, times(1)).save(wordEntity);
        verify(createWordConverter, times(1)).toDto(wordEntity);
    }
}