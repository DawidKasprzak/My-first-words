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
        createChildRequest = CreateChildRequest.builder()
                .name("childName")
                .birthDate(LocalDate.of(2020,1,1))
                .gender(Gender.GIRL)
                .build();

        childEntity = new ChildEntity();
        childEntity.setName(createChildRequest.getName());
        childEntity.setBirthDate(createChildRequest.getBirthDate());
        childEntity.setGender(createChildRequest.getGender());
    }

    @Test
    void when_fromDto_then_returnChildEntity() {
        ChildEntity entity = createChildConverter.fromDto(createChildRequest);

        assertEquals(createChildRequest.getName(), entity.getName());
        assertEquals(createChildRequest.getBirthDate(), entity.getBirthDate());
        assertEquals(createChildRequest.getGender(), entity.getGender());
    }

    @Test
    void when_toDto_then_returnCreateChildResponse() {
        CreateChildResponse response = createChildConverter.toDto(childEntity);

        assertEquals(childEntity.getName(), response.getName());
        assertEquals(childEntity.getBirthDate(), response.getBirthDate());
        assertEquals(childEntity.getGender(), response.getGender());
    }
}