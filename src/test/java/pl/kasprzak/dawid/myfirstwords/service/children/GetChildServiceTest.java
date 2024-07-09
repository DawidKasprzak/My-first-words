package pl.kasprzak.dawid.myfirstwords.service.children;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.Gender;
import pl.kasprzak.dawid.myfirstwords.model.children.GetAllChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.GetChildResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.children.GetChildConverter;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetChildServiceTest {


    @Mock
    private ChildrenRepository childrenRepository;
    @Mock
    private ParentsRepository parentsRepository;
    @Mock
    private GetChildConverter getChildConverter;
    @Mock
    private Authentication authentication;
    @Mock
    private AuthorizationHelper authorizationHelper;
    @InjectMocks
    private GetChildService getChildService;
    private ParentEntity parentEntity;
    private ParentEntity otherParent;
    private GetChildResponse getChildResponse1, getChildResponse2;
    private ChildEntity child1, child2, child3;

    @BeforeEach
    void setUp() {

        parentEntity = new ParentEntity();
        parentEntity.setId(1L);
        parentEntity.setUsername("parent");

        otherParent = new ParentEntity();
        otherParent.setId(2L);
        otherParent.setUsername("otherParent");

        child1 = new ChildEntity();
        child1.setId(1L);
        child1.setName("child1");
        child1.setBirthDate(LocalDate.of(2020, 02, 20));
        child1.setGender(Gender.GIRL);
        child1.setParent(parentEntity);

        child2 = new ChildEntity();
        child2.setId(2L);
        child2.setName("child2");
        child2.setBirthDate(LocalDate.of(2010, 01, 01));
        child2.setGender(Gender.BOY);
        child2.setParent(parentEntity);

        child3 = new ChildEntity();
        child3.setId(3L);
        child3.setName("child3");
        child3.setParent(otherParent);

        getChildResponse1 = GetChildResponse.builder()
                .id(child1.getId())
                .name(child1.getName())
                .birthDate(child1.getBirthDate())
                .build();

        getChildResponse2 = GetChildResponse.builder()
                .id(child2.getId())
                .name(child2.getName())
                .birthDate(child2.getBirthDate())
                .build();
    }


    @Test
    void when_getAllChildrenOfParent_then_returnAllChildren() {
        List<ChildEntity> children = Arrays.asList(child1, child2);
        List<GetChildResponse> expectResponse = Arrays.asList(getChildResponse1, getChildResponse2);

        when(authentication.getName()).thenReturn("parent");
        when(parentsRepository.findByUsername("parent")).thenReturn(Optional.of(parentEntity));
        when(childrenRepository.findByParentId(1L)).thenReturn(children);
        when(getChildConverter.toDto(child1)).thenReturn(getChildResponse1);
        when(getChildConverter.toDto(child2)).thenReturn(getChildResponse2);

        GetAllChildResponse response = getChildService.getAllChildrenOfParent(authentication);

        assertEquals(expectResponse, response.getChildren());
        verify(parentsRepository, times(1)).findByUsername("parent");
        verify(childrenRepository, times(1)).findByParentId(1L);
        verify(getChildConverter, times(1)).toDto(child1);
        verify(getChildConverter, times(1)).toDto(child2);
    }

    @Test
    void when_getAllChildrenOfParent_then_throwParentNotFoundException() {
        when(parentsRepository.findByUsername(authentication.getName())).thenReturn(Optional.empty());

        ParentNotFoundException parentNotFoundException = assertThrows(ParentNotFoundException.class,
                () -> getChildService.getAllChildrenOfParent(authentication));
        assertEquals("Parent not found", parentNotFoundException.getMessage());
        verify(parentsRepository, times(1)).findByUsername(authentication.getName());
        verify(childrenRepository, never()).findByParentId(anyLong());
        verify(getChildConverter, never()).toDto(any());

    }

    @Test
    void when_getChildById_then_returnChildWithSpecificId() {
        Long childId = child1.getId();

        when(authorizationHelper.validateAndAuthorizeChild(childId, authentication)).thenReturn(child1);
        when(getChildConverter.toDto(child1)).thenReturn(getChildResponse1);

        GetChildResponse response = getChildService.getChildById(childId, authentication);

        assertEquals(getChildResponse1, response);
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childId, authentication);
        verify(getChildConverter, times(1)).toDto(child1);
    }
}