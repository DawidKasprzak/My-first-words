package pl.kasprzak.dawid.myfirstwords.service.children;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
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
    private Authentication authentication;
    @InjectMocks
    private CreateChildService createChildService;
    private CreateChildRequest createChildRequest;
    private CreateChildResponse createChildResponse;
    private ChildEntity childEntity;
    private ParentEntity parentEntity;

    @BeforeEach
    void setUp() {

        createChildRequest = new CreateChildRequest();
        createChildRequest.setName("childName");

        createChildResponse = new CreateChildResponse();
        createChildResponse.setName("childName");

        childEntity = new ChildEntity();
        childEntity.setName("childName");

        parentEntity = new ParentEntity();
        parentEntity.setUsername("parentUsername");

    }

    @Test
    void when_addChild_then_childShouldBeSavedToParentAccount() {
        when(authentication.getName()).thenReturn("parentUsername");
        when(parentsRepository.findByUsername("parentUsername")).thenReturn(Optional.of(parentEntity));
        when(createChildConverter.fromDto(createChildRequest)).thenReturn(childEntity);
        when(childrenRepository.save(childEntity)).thenReturn(childEntity);
        when(createChildConverter.toDto(childEntity)).thenReturn(createChildResponse);

        CreateChildResponse result = createChildService.addChild(createChildRequest, authentication);

        assertEquals("childName", result.getName());
        verify(parentsRepository, times(1)).findByUsername("parentUsername");
        verify(childrenRepository, times(1)).save(childEntity);

    }

    @Test
    void when_parentNotFound_then_throwParentNotFoundException() {
        when(authentication.getName()).thenReturn("parentUsername");
        when(parentsRepository.findByUsername("parentUsername")).thenReturn(Optional.empty());

        ParentNotFoundException parentNotFoundException = assertThrows(ParentNotFoundException.class,
                () -> createChildService.addChild(createChildRequest, authentication));

        assertEquals("Parent not found", parentNotFoundException.getMessage());
        verify(parentsRepository, times(1)).findByUsername("parentUsername");
        verify(createChildConverter, never()).fromDto(any());
        verify(childrenRepository, never()).save(any());
    }
}