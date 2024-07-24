package pl.kasprzak.dawid.myfirstwords.service.words;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import pl.kasprzak.dawid.myfirstwords.exception.WordNotFoundException;
import pl.kasprzak.dawid.myfirstwords.repository.WordsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteWordServiceTest {
    @Mock
    private AuthorizationHelper authorizationHelper;
    @Mock
    private Authentication authentication;
    @Mock
    private WordsRepository wordsRepository;
    @InjectMocks
    private DeleteWordService deleteWordService;
    private WordEntity wordEntity;
    private ChildEntity childEntity;

    @BeforeEach
    void setUp() {

        wordEntity = new WordEntity();
        wordEntity.setId(1L);

        childEntity = new ChildEntity();
        childEntity.setId(1L);
        wordEntity.setChild(childEntity);

    }

    /**
     * Unit test for deleteWord method in DeleteWordService.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that a word is successfully deleted from the child's account.
     */
    @Test
    void when_deleteWord_then_wordShouldBeDeletedFromChildAccount() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndId(childEntity.getId(), wordEntity.getId())).thenReturn(Optional.of(wordEntity));

        deleteWordService.deleteWord(childEntity.getId(), wordEntity.getId(), authentication);

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, times(1)).delete(wordEntity);
    }

    /**
     * Unit test for deleteWord method in DeleteWordService.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that a WordNotFoundException is thrown when the word is not found.
     */
    @Test
    void when_deleteWordAndWordNotFound_then_throwWordNotFoundException() {
        when(authorizationHelper.validateAndAuthorizeChild(childEntity.getId(), authentication)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndId(childEntity.getId(), wordEntity.getId())).thenReturn(Optional.empty());

        WordNotFoundException wordNotFoundException = assertThrows(WordNotFoundException.class,
                () -> deleteWordService.deleteWord(childEntity.getId(), wordEntity.getId(), authentication));

        assertEquals("Word not found", wordNotFoundException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childEntity.getId(), authentication);
        verify(wordsRepository, never()).delete(any());
    }
}