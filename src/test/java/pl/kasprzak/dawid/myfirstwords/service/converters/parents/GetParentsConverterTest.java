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
        childEntity1 = new ChildEntity();
        childEntity1.setId(1L);
        childEntity1.setName("child1");

        childEntity2 = new ChildEntity();
        childEntity2.setId(2L);
        childEntity2.setName("child2");


        parentEntity = new ParentEntity();
        parentEntity.setId(1L);
        parentEntity.setUsername("parentUsername");
        parentEntity.setMail("parent@mail.com");
        parentEntity.setChildren(Arrays.asList(childEntity1, childEntity2));

        getChildResponse1 = GetChildResponse.builder()
                .id(childEntity1.getId())
                .name(childEntity1.getName())
                .build();

        getChildResponse2 = GetChildResponse.builder()
                .id(childEntity2.getId())
                .name(childEntity2.getName())
                .build();
    }

    @Test
    void when_toDto_then_returnParentInfoResponse() {

        when(getChildConverter.toDto(parentEntity.getChildren().get(0))).thenReturn(getChildResponse1);
        when(getChildConverter.toDto(parentEntity.getChildren().get(1))).thenReturn(getChildResponse2);

        ParentInfoResponse result = getParentsConverter.toDto(parentEntity);

        assertEquals(parentEntity.getId(), result.getId());
        assertEquals(parentEntity.getUsername(), result.getUsername());
        assertEquals(parentEntity.getMail(), result.getMail());

        List<GetChildResponse> expectedChildren = Arrays.asList(getChildResponse1, getChildResponse2);
        assertEquals(expectedChildren, result.getChildren());
    }

    @Test
    void when_callFromDto_then_throwUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> getParentsConverter.fromDto(null));
    }
}