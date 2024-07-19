package pl.kasprzak.dawid.myfirstwords.service.converters.children;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.cdi.Eager;
import pl.kasprzak.dawid.myfirstwords.model.children.GetChildResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class GetChildConverterTest {

    @InjectMocks
    private GetChildConverter getChildConverter;
    private ChildEntity childEntity;

    @BeforeEach
    void setUp(){

        childEntity = new ChildEntity();
        childEntity.setId(1L);
        childEntity.setName("childName");
        childEntity.setBirthDate(LocalDate.now().minusDays(2));

    }

    @Test
    void when_callFromDto_then_throwUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> getChildConverter.fromDto(null));
    }

    @Test
    void when_toDto_then_returnGetChildResponse() {
        GetChildResponse result = getChildConverter.toDto(childEntity);

        assertEquals(childEntity.getId(), result.getId());
        assertEquals(childEntity.getName(), result.getName());
        assertEquals(childEntity.getBirthDate(), result.getBirthDate());
    }
}