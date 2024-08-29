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
import pl.kasprzak.dawid.myfirstwords.repository.AuthoritiesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.AuthorityEntity;
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
    private AuthoritiesRepository authoritiesRepository;
    @Mock
    private CreateParentConverter createParentConverter;
    @InjectMocks
    private CreateParentService createParentService;

    private CreateParentRequest createParentRequest;
    private ParentEntity parentEntity;
    private AuthorityEntity userAuthority;
    private CreateParentResponse createParentResponse;

    @BeforeEach
    void setUp() {

        createParentRequest = CreateParentRequest.builder()
                .username("usernameTest")
                .mail("test@mail.com")
                .build();

        parentEntity = new ParentEntity();
        parentEntity.setUsername("usernameTest");
        parentEntity.setMail("test@mail.com");

        userAuthority = new AuthorityEntity();
        userAuthority.setAuthority("ROLE_USER");


        createParentResponse = CreateParentResponse.builder()
                .username(createParentRequest.getUsername())
                .mail(createParentRequest.getMail())
                .build();
    }

    /**
     * Unit test for saveParent method in CreateParentService.
     * Verifies that a new parent is saved successfully when the username and email do not already exist,
     * and that the associated authority (role) is also saved.
     */
    @Test
    void when_createNewParent_then_parentShouldBeSaved() {

        when(parentsRepository.findByUsername("usernameTest")).thenReturn(Optional.empty());
        when(parentsRepository.findByMail("test@mail.com")).thenReturn(Optional.empty());
        when(createParentConverter.fromDto(createParentRequest)).thenReturn(parentEntity);
        when(authoritiesRepository.save(userAuthority)).thenReturn(userAuthority);
        when(parentsRepository.save(parentEntity)).thenReturn(parentEntity);
        when(createParentConverter.toDto(parentEntity)).thenReturn(createParentResponse);

        CreateParentResponse result = createParentService.saveParent(createParentRequest);

        assertEquals(result, createParentResponse);
        verify(parentsRepository, times(1)).findByUsername("usernameTest");
        verify(parentsRepository, times(1)).findByMail("test@mail.com");
        verify(createParentConverter, times(1)).fromDto(createParentRequest);
        verify(authoritiesRepository, times(1)).save(userAuthority);
        verify(parentsRepository, times(1)).save(parentEntity);
        verify(createParentConverter, times(1)).toDto(parentEntity);
    }

    /**
     * Unit test for saveParent method in CreateParentService.
     * Verifies that a UsernameAlreadyExistsException is thrown and the appropriate error message is returned,
     * when the username already exists.
     */
    @Test
    void when_usernameAlreadyExists_then_throwUsernameAlreadyExistsException() {
        when(parentsRepository.findByUsername("usernameTest")).thenReturn(Optional.of(parentEntity));

        UsernameAlreadyExistsException usernameAlreadyExistsException = assertThrows(UsernameAlreadyExistsException.class,
                () -> createParentService.saveParent(createParentRequest));

        assertEquals("Username already exists: usernameTest", usernameAlreadyExistsException.getMessage());
        verify(parentsRepository, times(1)).findByUsername("usernameTest");
        verify(parentsRepository, never()).findByMail(anyString());
        verify(createParentConverter, never()).fromDto(any());
        verify(authoritiesRepository,never()).save(any());
        verify(parentsRepository, never()).save(any());
        verify(createParentConverter, never()).toDto(any());
    }

    /**
     * Unit test for the saveParent method in CreateParentService.
     * Verifies that a EmailAlreadyExistsException is thrown and the appropriate error message is returned,
     * when the email already exists.
     */
    @Test
    void when_emailAlreadyExists_then_throwEmailAlreadyExistsException() {
        when(parentsRepository.findByUsername("usernameTest")).thenReturn(Optional.empty());
        when(parentsRepository.findByMail("test@mail.com")).thenReturn(Optional.of(parentEntity));

        EmailAlreadyExistsException emailAlreadyExistsException = assertThrows(EmailAlreadyExistsException.class,
                () -> createParentService.saveParent(createParentRequest));

        assertEquals("Email already exists: test@mail.com", emailAlreadyExistsException.getMessage());
        verify(parentsRepository, times(1)).findByUsername("usernameTest");
        verify(parentsRepository, times(1)).findByMail("test@mail.com");
        verify(createParentConverter, never()).fromDto(any());
        verify(authoritiesRepository, never()).save(any());
        verify(parentsRepository, never()).save(any());
        verify(createParentConverter, never()).toDto(any());
    }
}