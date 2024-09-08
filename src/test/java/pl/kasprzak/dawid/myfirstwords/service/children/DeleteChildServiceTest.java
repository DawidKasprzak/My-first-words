package pl.kasprzak.dawid.myfirstwords.service.children;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.exception.AdminMissingParentIDException;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteChildServiceTest {
    @Mock
    private ChildEntity childEntity;
    @Mock
    private ChildrenRepository childrenRepository;
    @Mock
    private AuthorizationHelper authorizationHelper;
    @InjectMocks
    private DeleteChildService deleteChildService;

    @BeforeEach
    void setUp() {

        childEntity = new ChildEntity();
        childEntity.setId(1L);
        childEntity.setName("child");
        childrenRepository.save(childEntity);
    }

    /**
     * Unit test for the deleteChild method in DeleteChildService.
     * This test verifies that the child belongs to the authenticated parent by using the AuthorizationHelper.
     * It ensures that the child is successfully deleted from the parent's account
     */
    @Test
    void when_deleteChild_then_childShouldBeDeleted() {

        Long childId = childEntity.getId();

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childId, null)).thenReturn(childEntity);

        deleteChildService.deleteChild(childId, null);

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childId, null);
        verify(childrenRepository, times(1)).deleteById(childId);
    }

    /**
     * Unit test for the deleteChildForParentByAdmin method in DeleteChildService when the authenticated user is an administrator.
     * This test verifies that the correct parentID is provided and that the child is successfully deleted from the parent's account.
     * Lenient stubbing is used to simulate an admin user.
     */
    @Test
    void when_adminDeletesChildForParent_then_childShouldBeDeleted() {
        long parentID = 1L;

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentID)).thenReturn(childEntity);

        deleteChildService.deleteChild(childEntity.getId(), parentID);

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parentID);
        verify(childrenRepository, times(1)).deleteById(childEntity.getId());

    }

    /**
     * Unit test for the deleteChildForParentByAdmin method in DeleteChildService when the authenticated user is an administrator
     * and no parentID is provided.
     * This test verifies that an AdminMissingParentIDException is thrown when the admin tries to delete a child without providing a parentID.
     * Lenient stubbing is used to simulate an admin user.
     */
    @Test
    void when_adminDeletesChildForParentWithoutParentID_then_shouldThrowAdminMissingParentIDException() {

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null))
                .thenThrow(new AdminMissingParentIDException("Admin must provide a parentID to perform this operation."));

        AdminMissingParentIDException adminMissingParentIDException = assertThrows(AdminMissingParentIDException.class,
                () -> deleteChildService.deleteChild(childEntity.getId(), null));

        assertEquals("Admin must provide a parentID to perform this operation.", adminMissingParentIDException.getMessage());
        verify(authorizationHelper, never()).validateAndAuthorizeChildForAdmin(anyLong(), anyLong());
        verify(childrenRepository, never()).deleteById(anyLong());

    }
}