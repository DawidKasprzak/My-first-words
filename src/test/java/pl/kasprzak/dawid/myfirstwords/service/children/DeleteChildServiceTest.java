package pl.kasprzak.dawid.myfirstwords.service.children;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
     * Unit test for deleteChild method in DeleteChildService.
     * This test first verifies that the child belongs to the authenticated parent by using the AuthorizationHelper.
     * It then checks that the child is successfully deleted from the parent's account.
     * The test also ensures that the correct methods in AuthorizationHelper and ChildrenRepository are called.
     */
    @Test
    void when_deleteChild_then_childShouldBeDeleted() {

        Long childId = childEntity.getId();

        when(authorizationHelper.validateAndAuthorizeChild(childId)).thenReturn(childEntity);

        deleteChildService.deleteChild(childId, null);

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childId);
        verify(childrenRepository, times(1)).deleteById(childId);
    }

    /**
     * Unit test for deleteChildForParentByAdmin method in DeleteChildService when the authenticated user is an administrator.
     * This test verifies that the correct parentID is provided and that the child is successfully deleted from the parent's account.
     * The test ensures that the correct methods in AuthorizationHelper and ChildrenRepository are called.
     */
    @Test
    void when_adminDeletesChildForParent_then_childShouldBeDeleted() {
        long parentID = 1L;

        when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeChildForAdmin(childEntity.getId(), parentID)).thenReturn(childEntity);

        deleteChildService.deleteChild(childEntity.getId(), parentID);

        verify(authorizationHelper, times(1)).isAdmin();
        verify(authorizationHelper, times(1)).validateAndAuthorizeChildForAdmin(childEntity.getId(), parentID);
        verify(childrenRepository, times(1)).deleteById(childEntity.getId());

    }

    /**
     * Unit test for deleteChildForParentByAdmin method in DeleteChildService when the authenticated user is an administrator and no parentID is provided.
     * This test verifies that an IllegalArgumentException is thrown when the admin tries to delete a child without providing a parentID.
     */
    @Test
    void when_adminDeletesChildForParentWithoutParentID_then_shouldThrowIllegalArgumentException() {
        long parentID = 1L;

        when(authorizationHelper.isAdmin()).thenReturn(true);

        IllegalArgumentException illegalArgumentException = assertThrows(java.lang.IllegalArgumentException.class,
                () -> {
                    deleteChildService.deleteChild(childEntity.getId(), null);
                });

        assertEquals("Admin must provide a parentID to delete a child", illegalArgumentException.getMessage());
        verify(authorizationHelper, times(1)).isAdmin();
        verify(authorizationHelper, never()).validateAndAuthorizeChildForAdmin(anyLong(), anyLong());
        verify(childrenRepository, never()).deleteById(anyLong());

    }
}