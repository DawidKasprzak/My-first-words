package pl.kasprzak.dawid.myfirstwords.service.words;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.exception.WordNotFoundException;
import pl.kasprzak.dawid.myfirstwords.repository.WordsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
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
     * Unit test for the deleteWord method in DeleteWordService.
     * Verifies that the child is validated and authorized for the authenticated parent or admin using AuthorizationHelper.
     * Ensures that the word is successfully deleted from the child's account.
     */
    @Test
    void when_deleteWord_then_wordShouldBeDeletedFromChildAccount() {

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndId(childEntity.getId(), wordEntity.getId())).thenReturn(Optional.of(wordEntity));

        deleteWordService.deleteWord(childEntity.getId(), wordEntity.getId(), null);

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(wordsRepository, times(1)).delete(wordEntity);
    }

    /**
     * Unit test for the deleteWord method in DeleteWordService.
     * Verifies that the child is validated and authorized for the authenticated parent or admin using AuthorizationHelper.
     * This test ensures that a WordNotFoundException is thrown with the appropriate error message when the word is not found.
     */
    @Test
    void when_deleteWordAndWordNotFound_then_throwWordNotFoundException() {

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndId(childEntity.getId(), wordEntity.getId())).thenReturn(Optional.empty());

        WordNotFoundException wordNotFoundException = assertThrows(WordNotFoundException.class,
                () -> deleteWordService.deleteWord(childEntity.getId(), wordEntity.getId(), null));

        assertEquals("Word not found", wordNotFoundException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(wordsRepository, never()).delete(any());
    }

    /**
     * Unit test for the deleteWord method in DeleteWordService when accessed by an administrator.
     * This test verifies that when an administrator provides a valid parent ID, the word is successfully deleted
     * from the specified child's account. Lenient stubbing is used to simulate the admin role.
     */
    @Test
    void when_adminDeletesWordWithParentID_then_wordShouldBeDeleted(){
        ParentEntity parent = new ParentEntity();
        parent.setId(1L);

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parent.getId())).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndId(childEntity.getId(), wordEntity.getId())).thenReturn(Optional.of(wordEntity));

        deleteWordService.deleteWord(childEntity.getId(), wordEntity.getId(), parent.getId());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parent.getId());
        verify(wordsRepository, times(1)).delete(wordEntity);
    }
}