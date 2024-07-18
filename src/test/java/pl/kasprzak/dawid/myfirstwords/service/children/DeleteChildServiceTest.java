package pl.kasprzak.dawid.myfirstwords.service.children;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
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
    @Mock
    private Authentication authentication;
    @InjectMocks
    private DeleteChildService deleteChildService;


    @Test
    void when_deleteChild_then_childShouldBeDeleted() {
        childEntity = new ChildEntity();
        childEntity.setId(1L);
        childEntity.setName("child");
        childrenRepository.save(childEntity);

        Long childId = childEntity.getId();

        when(authorizationHelper.validateAndAuthorizeChild(childId, authentication)).thenReturn(childEntity);

        deleteChildService.deleteChild(childId, authentication);

        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childId, authentication);
        verify(childrenRepository, times(1)).deleteById(childId);
    }
}