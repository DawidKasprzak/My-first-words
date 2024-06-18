package pl.kasprzak.dawid.myfirstwords.service.parents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.exception.EmailAlreadyExistsException;
import pl.kasprzak.dawid.myfirstwords.exception.UsernameAlreadyExistsException;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentRequest;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.parents.CreateParentConverter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateParentServiceTest {

    @Mock
    private ParentsRepository parentsRepository;
    @Mock
    private CreateParentConverter createParentConverter;
    @InjectMocks
    private CreateParentService createParentService;

    private CreateParentRequest createParentRequest;
    private ParentEntity parentEntity;
    private CreateParentResponse createParentResponse;

    @BeforeEach
    void setUp() {
        createParentRequest = new CreateParentRequest();
        createParentRequest.setUsername("usernameTest");
        createParentRequest.setMail("test@mail.com");

        parentEntity = new ParentEntity();
        parentEntity.setUsername("usernameTest");
        parentEntity.setMail("test@mail.com");

        createParentResponse = new CreateParentResponse();
        createParentResponse.setUsername("usernameTest");
        createParentResponse.setMail("test@mail.com");
    }

    @Test
    void when_createNewParent_then_parentShouldBeSaved() {

        when(parentsRepository.findByUsername("usernameTest")).thenReturn(Optional.empty());
        when(parentsRepository.findByMail("test@mail.com")).thenReturn(Optional.empty());
        when(createParentConverter.fromDto(createParentRequest)).thenReturn(parentEntity);
        when(parentsRepository.save(parentEntity)).thenReturn(parentEntity);
        when(createParentConverter.toDto(parentEntity)).thenReturn(createParentResponse);

        CreateParentResponse result = createParentService.saveParent(createParentRequest);

        assertEquals(result, createParentResponse);
        verify(parentsRepository).findByUsername("usernameTest");
        verify(parentsRepository).findByMail("test@mail.com");
        verify(createParentConverter).fromDto(createParentRequest);
        verify(parentsRepository).save(parentEntity);
        verify(createParentConverter).toDto(parentEntity);
    }

    @Test
    void when_usernameAlreadyExists_then_throwUsernameAlreadyExistsException() {
        when(parentsRepository.findByUsername("usernameTest")).thenReturn(Optional.of(parentEntity));

        UsernameAlreadyExistsException usernameAlreadyExistsException = assertThrows(UsernameAlreadyExistsException.class,
                () -> createParentService.saveParent(createParentRequest));

        assertEquals("Username already exists: usernameTest", usernameAlreadyExistsException.getMessage());
        verify(parentsRepository).findByUsername("usernameTest");
        verify(parentsRepository, never()).findByMail(anyString());
        verify(createParentConverter, never()).fromDto(any());
        verify(parentsRepository, never()).save(any());
        verify(createParentConverter, never()).toDto(any());
    }

    @Test
    void when_emailAlreadyExists_then_throwEmailAlreadyExistsException(){
        when(parentsRepository.findByUsername("usernameTest")).thenReturn(Optional.empty());
        when(parentsRepository.findByMail("test@mail.com")).thenReturn(Optional.of(parentEntity));

        EmailAlreadyExistsException emailAlreadyExistsException = assertThrows(EmailAlreadyExistsException.class,
                ()-> createParentService.saveParent(createParentRequest));

        assertEquals("Email already exists: test@mail.com", emailAlreadyExistsException.getMessage());
        verify(parentsRepository).findByUsername("usernameTest");
        verify(parentsRepository).findByMail("test@mail.com");
        verify(createParentConverter, never()).fromDto(any());
        verify(parentsRepository, never()).save(any());
        verify(createParentConverter, never()).toDto(any());
    }
}