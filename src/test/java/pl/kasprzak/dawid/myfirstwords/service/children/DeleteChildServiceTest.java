package pl.kasprzak.dawid.myfirstwords.service.children;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

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

    /**
     * Unit test for deleteChild method in DeleteChildService.
     * This test first verifies that the child belongs to the authenticated parent by using the AuthorizationHelper.
     * It then checks that the child is successfully deleted from the parent's account.
     * The test also ensures that the correct methods in AuthorizationHelper and ChildrenRepository are called.
     */
    @Test
    void when_deleteChild_then_childShouldBeDeleted() {
        childEntity = new ChildEntity();
        childEntity.setId(1L);
        childEntity.setName("child");
        childrenRepository.save(childEntity);

        Long childId = childEntity.getId();

        when(authorizationHelper.validateAndAuthorizeChild(childId)).thenReturn(childEntity);

        deleteChildService.deleteChild(childId);

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childId);
        verify(childrenRepository, times(1)).deleteById(childId);
    }
}