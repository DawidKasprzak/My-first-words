package pl.kasprzak.dawid.myfirstwords.service.words;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.exception.AdminMissingParentIDException;
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
     * This test verifies that the service correctly deletes a words for a specific child
     * when the request is made by the authenticated parent.
     * The test ensures that:
     * 1. The child is validated and authorized for the parent using the AuthorizationHelper.
     * 2. The WordsRepository is queried to find the word associated with the child.
     * 3. If the word exists, it is successfully deleted from the child's account.
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
     * Unit test for the deleteWord method in DeleteWordService when the word is not found.
     * This test verifies that the service correctly throws a WordNotFoundException
     * if the specified word for a given child does not exist.
     * The test ensures that:
     * 1. The child is validated and authorized for the parent using the AuthorizationHelper.
     * 2. The WordsRepository is queried to find the word associated with the child.
     * 3. If the word does not exist, a WordNotFoundException is thrown with the appropriate error message.
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
     * Unit test for the deleteWord method in DeleteWordService when accessed by an administrator
     * with a provided parent ID.
     * This test verifies that the service correctly deletes a word for a given child when the request
     * is made by an administrator and the parent ID is provided.
     * The test ensures that:
     * 1. The child is validated and authorized for the administrator using the AuthorizationHelper
     * with the provided parent ID.
     * 2. The WordsRepository is queried to find the word associated with the child.
     * 3. The word is successfully deleted from the child's account if it exists.
     */
    @Test
    void when_adminDeletesWordWithParentID_then_wordShouldBeDeleted() {
        ParentEntity parent = new ParentEntity();
        parent.setId(1L);

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parent.getId())).thenReturn(childEntity);
        when(wordsRepository.findByChildIdAndId(childEntity.getId(), wordEntity.getId())).thenReturn(Optional.of(wordEntity));

        deleteWordService.deleteWord(childEntity.getId(), wordEntity.getId(), parent.getId());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parent.getId());
        verify(wordsRepository, times(1)).delete(wordEntity);
    }

    /**
     * Unit test for the deleteWord method in DeleteWordService when accessed by an administrator
     * without providing a parent ID.
     * This test verifies that an AdminMissingParentIDException is thrown when the administrator
     * tries to delete a word for a child without providing a parent ID.
     * The test ensures that:
     * 1. The child is not authorized because the parent ID is missing.
     * 2. The AdminMissingParentIDException is thrown with the appropriate message.
     * 3. The WordsRepository is never queried if the authorization fails due to a missing parent ID.
     */
    @Test
    void when_adminDeletesWordWithoutParentID_then_adminMissingParentIDExceptionShouldBeThrown(){
        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null))
                .thenThrow(new AdminMissingParentIDException("Admin must provide a parentID to perform this operation."));

        AdminMissingParentIDException adminMissingParentIDException = assertThrows(AdminMissingParentIDException.class,
                () -> deleteWordService.deleteWord(childEntity.getId(), wordEntity.getId(), null));

        assertEquals("Admin must provide a parentID to perform this operation.", adminMissingParentIDException.getMessage());

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(wordsRepository, never()).delete(any(WordEntity.class));
    }
}