package pl.kasprzak.dawid.myfirstwords.service.converters.children;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void setUp() {

        // Initialize ChildEntity with test data
        childEntity = new ChildEntity();
        childEntity.setId(1L);
        childEntity.setName("childName");
        childEntity.setBirthDate(LocalDate.now().minusDays(2));

    }

    /**
     * Unit test for calling fromDto method on GetChildConverter.
     * Verifies that an UnsupportedOperationException is thrown when fromDto is called.
     */
    @Test
    void when_callFromDto_then_throwUnsupportedOperationException() {
        // Assert that UnsupportedOperationException is thrown when calling fromDto with null input
        assertThrows(UnsupportedOperationException.class, () -> getChildConverter.fromDto(null));
    }

    /**
     * Unit test for toDto method of GetChildConverter.
     * Verifies that the GetChildResponse is correctly created from ChildEntity.
     */
    @Test
    void when_toDto_then_returnGetChildResponse() {
        // Convert the entity to response
        GetChildResponse response = getChildConverter.toDto(childEntity);

        // Verify the conversion
        assertEquals(childEntity.getId(), response.getId());
        assertEquals(childEntity.getName(), response.getName());
        assertEquals(childEntity.getBirthDate(), response.getBirthDate());
    }
}