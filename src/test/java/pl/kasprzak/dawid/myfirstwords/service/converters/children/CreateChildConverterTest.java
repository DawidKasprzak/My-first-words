package pl.kasprzak.dawid.myfirstwords.service.converters.children;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildRequest;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.Gender;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CreateChildConverterTest {

    @InjectMocks
    private CreateChildConverter createChildConverter;
    private CreateChildRequest createChildRequest;
    private ChildEntity childEntity;

    @BeforeEach
    void setUp(){

        // Initialize a CreateChildRequest with test data
        createChildRequest = CreateChildRequest.builder()
                .name("childName")
                .birthDate(LocalDate.of(2020,1,1))
                .gender(Gender.GIRL)
                .build();

        // Initialize ChildEntity with test data
        childEntity = new ChildEntity();
        childEntity.setName(createChildRequest.getName());
        childEntity.setBirthDate(createChildRequest.getBirthDate());
        childEntity.setGender(createChildRequest.getGender());
    }

    /**
     * Unit test for converting CreateChildRequest to ChildEntity.
     * Verifies that the ChildEntity is correctly created from the CreateChildRequest.
     */
    @Test
    void when_fromDto_then_returnChildEntity() {
        // Convert the request to entity
        ChildEntity entity = createChildConverter.fromDto(createChildRequest);

        // Verify the conversion
        assertEquals(createChildRequest.getName(), entity.getName());
        assertEquals(createChildRequest.getBirthDate(), entity.getBirthDate());
        assertEquals(createChildRequest.getGender(), entity.getGender());
    }

    /**
     * Unit test for converting ChildEntity to CreateChildResponse.
     * Verifies that the CreateChildResponse is correctly created from the ChildEntity.
     */
    @Test
    void when_toDto_then_returnCreateChildResponse() {
        // Convert the entity to response
        CreateChildResponse response = createChildConverter.toDto(childEntity);

        // Verify the conversion
        assertEquals(childEntity.getName(), response.getName());
        assertEquals(childEntity.getBirthDate(), response.getBirthDate());
        assertEquals(childEntity.getGender(), response.getGender());
    }
}