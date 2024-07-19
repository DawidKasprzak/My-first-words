package pl.kasprzak.dawid.myfirstwords.service.converters.parents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.model.children.GetChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.parents.ParentInfoResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.children.GetChildConverter;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetParentsConverterTest {

    @Mock
    private GetChildConverter getChildConverter;
    @InjectMocks
    private GetParentsConverter getParentsConverter;
    private ParentEntity parentEntity;
    private ChildEntity childEntity1, childEntity2;
    private GetChildResponse getChildResponse1, getChildResponse2;

    @BeforeEach
    void setUp() {
        // Initialize ChildEntity instances with test data
        childEntity1 = new ChildEntity();
        childEntity1.setId(1L);
        childEntity1.setName("child1");

        childEntity2 = new ChildEntity();
        childEntity2.setId(2L);
        childEntity2.setName("child2");

        // Initialize ParentEntity with test data and associate it with child entities
        parentEntity = new ParentEntity();
        parentEntity.setId(1L);
        parentEntity.setUsername("parentUsername");
        parentEntity.setMail("parent@mail.com");
        parentEntity.setChildren(Arrays.asList(childEntity1, childEntity2));

        // Initialize GetChildResponse instances corresponding to the child entities
        getChildResponse1 = GetChildResponse.builder()
                .id(childEntity1.getId())
                .name(childEntity1.getName())
                .build();

        getChildResponse2 = GetChildResponse.builder()
                .id(childEntity2.getId())
                .name(childEntity2.getName())
                .build();
    }

    /**
     * Unit test for converting ParentEntity to ParentInfoResponse.
     * Verifies that the ParentInfoResponse is correctly created from the ParentEntity.
     * and that the children are correctly converted using GetChildConverter.
     */
    @Test
    void when_toDto_then_returnParentInfoResponse() {
        // Mock the conversion of child entities to child responses
        when(getChildConverter.toDto(parentEntity.getChildren().get(0))).thenReturn(getChildResponse1);
        when(getChildConverter.toDto(parentEntity.getChildren().get(1))).thenReturn(getChildResponse2);

        // Convert the ParentEntity to response
        ParentInfoResponse result = getParentsConverter.toDto(parentEntity);

        // Verify the conversion of parent fields
        assertEquals(parentEntity.getId(), result.getId());
        assertEquals(parentEntity.getUsername(), result.getUsername());
        assertEquals(parentEntity.getMail(), result.getMail());

        // Verify the conversion of child entities
        List<GetChildResponse> expectedChildren = Arrays.asList(getChildResponse1, getChildResponse2);
        assertEquals(expectedChildren, result.getChildren());
    }

    /**
     * Unit test for calling fromDto method on GetParentsConverter.
     * Verifies that an UnsupportedOperationException is thrown when fromDto is called.
     */
    @Test
    void when_callFromDto_then_throwUnsupportedOperationException() {
        // Assert that UnsupportedOperationException is thrown when calling fromDto with null input
        assertThrows(UnsupportedOperationException.class, () -> getParentsConverter.fromDto(null));
    }
}