package pl.kasprzak.dawid.myfirstwords.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationHelperTest {

    @Mock
    private ParentsRepository parentsRepository;
    @Mock
    private ChildrenRepository childrenRepository;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private AuthorizationHelper authorizationHelper;

    private ParentEntity parentEntity;
    private ChildEntity childEntity;

    @BeforeEach
    void setUp() {
        parentEntity = new ParentEntity();
        parentEntity.setId(1L);
        parentEntity.setUsername("parent");

        childEntity = new ChildEntity();
        childEntity.setId(1L);
        childEntity.setName("child");
        childEntity.setParent(parentEntity);
    }

    @Test
    void when_validateAndAuthorizeChild_then_returnChild() {
        Long childId = childEntity.getId();

        when(authentication.getName()).thenReturn("parent");
        when(parentsRepository.findByUsername("parent")).thenReturn(Optional.of(parentEntity));
        when(childrenRepository.findById(childId)).thenReturn(Optional.of(childEntity));

        ChildEntity result = authorizationHelper.validateAndAuthorizeChild(childId, authentication);

        assertEquals(childEntity, result);
        verify(parentsRepository, times(1)).findByUsername("parent");
        verify(childrenRepository, times(1)).findById(childId);
    }

    @Test
    void when_parentNotFound_then_throwParentNotFoundException() {
        Long childId = childEntity.getId();

        when(authentication.getName()).thenReturn("parent");
        when(parentsRepository.findByUsername("parent")).thenReturn(Optional.empty());

        ParentNotFoundException parentNotFoundException = assertThrows(ParentNotFoundException.class,
                () -> authorizationHelper.validateAndAuthorizeChild(childId, authentication));

        assertEquals("Parent not found", parentNotFoundException.getMessage());
        verify(parentsRepository, times(1)).findByUsername("parent");
        verify(childrenRepository, never()).findById(anyLong());
    }

    @Test
    void when_childNotFound_then_throwChildNotFoundException() {
        Long childId = childEntity.getId();

        when(authentication.getName()).thenReturn("parent");
        when(parentsRepository.findByUsername("parent")).thenReturn(Optional.of(parentEntity));
        when(childrenRepository.findById(childId)).thenReturn(Optional.empty());

        ChildNotFoundException childNotFoundException = assertThrows(ChildNotFoundException.class,
                () -> authorizationHelper.validateAndAuthorizeChild(childId, authentication));

        assertEquals("Child not found", childNotFoundException.getMessage());
        verify(parentsRepository, times(1)).findByUsername("parent");
        verify(childrenRepository, times(1)).findById(childId);
    }

    @Test
    void when_childDoesNotBelongToParent_then_throwAccessDeniedException() {
        Long childId = childEntity.getId();
        ParentEntity otherParent = new ParentEntity();
        otherParent.setId(2L);
        childEntity.setParent(otherParent);

        when(authentication.getName()).thenReturn("parent");
        when(parentsRepository.findByUsername("parent")).thenReturn(Optional.of(parentEntity));
        when(childrenRepository.findById(childId)).thenReturn(Optional.of(childEntity));

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class,
                () -> authorizationHelper.validateAndAuthorizeChild(childId, authentication));

        assertEquals("The parent does not have access to this child", accessDeniedException.getMessage());
        verify(parentsRepository, times(1)).findByUsername("parent");
        verify(childrenRepository, times(1)).findById(childId);
    }
}