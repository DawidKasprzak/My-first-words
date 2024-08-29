package pl.kasprzak.dawid.myfirstwords.service.children;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildRequest;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.children.CreateChildConverter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateChildServiceTest {
    @Mock
    private ChildrenRepository childrenRepository;
    @Mock
    private ParentsRepository parentsRepository;
    @Mock
    private CreateChildConverter createChildConverter;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private CreateChildService createChildService;
    private CreateChildRequest createChildRequest;
    private CreateChildResponse createChildResponse;
    private ChildEntity childEntity;
    private ParentEntity parentEntity;

    @BeforeEach
    void setUp() {

        createChildRequest = CreateChildRequest.builder()
                .name("childName")
                .build();

        createChildResponse = CreateChildResponse.builder()
                .name(createChildRequest.getName())
                .build();

        childEntity = new ChildEntity();
        childEntity.setName("childName");

        parentEntity = new ParentEntity();
        parentEntity.setUsername("parentUsername");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("parentUsername");

    }

    /**
     * Unit test for addChild method in CreateChildService.
     * This test verifies that the parent is correctly authenticated and authorized
     * through the SecurityContextHolder. It ensures that a child is successfully
     * added to the parent's account and that all interactions with repositories
     * and converters are correct.
     */
    @Test
    void when_addChild_then_childShouldBeSavedToParentAccount() {

        when(parentsRepository.findByUsername("parentUsername")).thenReturn(Optional.of(parentEntity));
        when(createChildConverter.fromDto(createChildRequest)).thenReturn(childEntity);
        when(childrenRepository.save(childEntity)).thenReturn(childEntity);
        when(createChildConverter.toDto(childEntity)).thenReturn(createChildResponse);

        CreateChildResponse result = createChildService.addChild(createChildRequest);

        assertEquals("childName", result.getName());
        verify(parentsRepository, times(1)).findByUsername("parentUsername");
        verify(childrenRepository, times(1)).save(childEntity);

    }

    /**
     * Unit test for addChild method in CreateChildService.
     * Verifies that the parent is authenticated through SecurityContextHolder and checked for existence.
     * Ensures that a ParentNotFoundException is thrown and the appropriate error message is returned
     * when the parent is not found in the repository.
     */
    @Test
    void when_parentNotFound_then_throwParentNotFoundException() {

        when(parentsRepository.findByUsername("parentUsername")).thenReturn(Optional.empty());

        ParentNotFoundException parentNotFoundException = assertThrows(ParentNotFoundException.class,
                () -> createChildService.addChild(createChildRequest));

        assertEquals("Parent not found", parentNotFoundException.getMessage());
        verify(parentsRepository, times(1)).findByUsername("parentUsername");
        verify(createChildConverter, never()).fromDto(any());
        verify(childrenRepository, never()).save(any());
    }
}