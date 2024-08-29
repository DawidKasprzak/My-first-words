package pl.kasprzak.dawid.myfirstwords.service.converters.parents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentRequest;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateParentConverterTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private CreateParentConverter createParentConverter;
    private CreateParentRequest createParentRequest;
    private ParentEntity parentEntity;

    @BeforeEach
    void setUp() {
        createParentRequest = CreateParentRequest.builder()
                .username("parentUsername")
                .mail("parent@mail.com")
                .password("testPassword")
                .build();

        parentEntity = new ParentEntity();
        parentEntity.setId(1L);
        parentEntity.setUsername(createParentRequest.getUsername());
        parentEntity.setMail(createParentRequest.getMail());
        parentEntity.setPassword("encodedPassword");
    }

    /**
     * Unit test for the fromDto method of CreateParentConverter.
     * Verifies that the ParentEntity is correctly created from the CreateParentRequest.
     * Ensures that the password is encoded using the PasswordEncoder.
     */
    @Test
    void when_fromDto_then_returnParentEntity() {
        when(passwordEncoder.encode(createParentRequest.getPassword())).thenReturn("encodedPassword");

        ParentEntity entity = createParentConverter.fromDto(createParentRequest);

        assertEquals(createParentRequest.getUsername(), entity.getUsername());
        assertEquals(createParentRequest.getMail(), entity.getMail());
        assertEquals("encodedPassword", entity.getPassword());

        verify(passwordEncoder, times(1)).encode(createParentRequest.getPassword());
    }

    /**
     * Unit test for the toDto method of CreateParentConverter.
     * Verifies that the CreateParentResponse is correctly created from ParentEntity.
     */
    @Test
    void when_toDto_then_returnCreateParentResponse() {
        CreateParentResponse response = createParentConverter.toDto(parentEntity);

        assertEquals("parentUsername", response.getUsername());
        assertEquals("parent@mail.com", response.getMail());
        assertEquals(parentEntity.getId(), response.getId());
    }
}