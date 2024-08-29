package pl.kasprzak.dawid.myfirstwords.service.children;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
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
import java.util.*;

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
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private AuthorizationHelper authorizationHelper;
    @InjectMocks
    private GetChildService getChildService;
    private ParentEntity parentEntity;
    private GetChildResponse getChildResponse1, getChildResponse2;
    private ChildEntity child1, child2;

    @BeforeEach
    void setUp() {

        parentEntity = new ParentEntity();
        parentEntity.setId(1L);
        parentEntity.setUsername("parent");

        ParentEntity otherParent = new ParentEntity();
        otherParent.setId(2L);
        otherParent.setUsername("otherParent");

        child1 = new ChildEntity();
        child1.setId(1L);
        child1.setName("child1");
        child1.setBirthDate(LocalDate.of(2020, 2, 20));
        child1.setGender(Gender.GIRL);
        child1.setParent(parentEntity);

        child2 = new ChildEntity();
        child2.setId(2L);
        child2.setName("child2");
        child2.setBirthDate(LocalDate.of(2010, 1, 1));
        child2.setGender(Gender.BOY);
        child2.setParent(parentEntity);

        ChildEntity child3 = new ChildEntity();
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


    /**
     * Unit test for getAllChildrenOfParent method in GetChildService.
     * Verifies that the parent is authenticated and authorized using SecurityContextHolder.
     * Then verifies that all children of the authenticated parent are retrieved and correctly converted to DTOs.
     */
    @Test
    void when_getAllChildrenOfParent_then_returnAllChildren() {
        List<ChildEntity> children = Arrays.asList(child1, child2);
        List<GetChildResponse> expectResponse = Arrays.asList(getChildResponse1, getChildResponse2);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("parent");
        when(parentsRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(parentEntity));
        when(childrenRepository.findByParentId(1L)).thenReturn(children);
        when(getChildConverter.toDto(child1)).thenReturn(getChildResponse1);
        when(getChildConverter.toDto(child2)).thenReturn(getChildResponse2);

        GetAllChildResponse response = getChildService.getAllChildrenOfParent(null);

        assertEquals(expectResponse, response.getChildren());
        verify(parentsRepository, times(1)).findByUsername("parent");
        verify(childrenRepository, times(1)).findByParentId(1L);
        verify(getChildConverter, times(1)).toDto(child1);
        verify(getChildConverter, times(1)).toDto(child2);
    }

    /**
     * Unit test for the getAllChildrenOfParent method in GetChildService.
     * Verifies that when an administrator provides a valid parent ID, the method correctly retrieves
     * all children associated with that parent and converts them to DTOs.
     * The test mocks the SecurityContext to simulate an admin user and ensures that the appropriate
     * repositories and converters are called, resulting in a correct response.
     */
    @Test
    void when_adminGetsAllChildrenByParentID_then_returnAllChildrenForSpecifiedParent() {
        List<ChildEntity> children = Arrays.asList(child1, child2);
        List<GetChildResponse> expectResponse = Arrays.asList(getChildResponse1, getChildResponse2);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        lenient().when(authentication.getAuthorities()).thenAnswer(invocationOnMock -> Collections.<GrantedAuthority>singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(parentsRepository.findById(1L)).thenReturn(Optional.of(parentEntity));
        when(childrenRepository.findByParentId(1L)).thenReturn(children);
        when(getChildConverter.toDto(child1)).thenReturn(getChildResponse1);
        when(getChildConverter.toDto(child2)).thenReturn(getChildResponse2);

        GetAllChildResponse response = getChildService.getAllChildrenOfParent(1L);

        assertEquals(expectResponse, response.getChildren());
        verify(parentsRepository, times(1)).findById(1L);
        verify(childrenRepository, times(1)).findByParentId(1L);
        verify(getChildConverter, times(1)).toDto(child1);
        verify(getChildConverter, times(1)).toDto(child2);
    }

    /**
     * Unit test for getAllChildrenOfParent method in GetChildService.
     * Verifies that a ParentNotFoundException is thrown and the appropriate error message is returned,
     * when the parent is not found.
     */
    @Test
    void when_getAllChildrenOfParent_then_throwParentNotFoundException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("parent");
        when(parentsRepository.findByUsername(authentication.getName())).thenReturn(Optional.empty());

        ParentNotFoundException parentNotFoundException = assertThrows(ParentNotFoundException.class,
                () -> getChildService.getAllChildrenOfParent(null));

        assertEquals("Parent not found", parentNotFoundException.getMessage());
        verify(parentsRepository, times(1)).findByUsername(authentication.getName());
        verify(childrenRepository, never()).findByParentId(anyLong());
        verify(getChildConverter, never()).toDto(any());

    }

    /**
     * Unit test for the getAllChildrenOfParent method in GetChildService.
     * Verifies that when an administrator does not provide a parent ID, the method throws
     * an IllegalArgumentException with the appropriate error message.
     * The test mocks the SecurityContext to simulate an admin user and ensures that no
     * repository or converter methods are called when the exception is thrown.
     */
    @Test
    void when_adminDoesNotProvideParentID_then_throwIllegalArgumentException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getAuthorities()).thenAnswer(invocationOnMock -> Collections.<GrantedAuthority>singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> getChildService.getAllChildrenOfParent(null));

        assertEquals("Admin must provide a parentID to retrieve children", illegalArgumentException.getMessage());

        verify(parentsRepository, never()).findById(anyLong());
        verify(childrenRepository, never()).findByParentId(anyLong());
    }

    /**
     * Unit test for getChildById method in GetChildService.
     * First verifies that the child belongs to the authenticated parent.
     * Then verifies that a child with a specific ID is retrieved and correctly converted to a DTO.
     */
    @Test
    void when_getChildById_then_returnChildWithSpecificId() {
        Long childId = child1.getId();

        when(authorizationHelper.validateAndAuthorizeChild(childId)).thenReturn(child1);
        when(getChildConverter.toDto(child1)).thenReturn(getChildResponse1);

        GetChildResponse response = getChildService.getChildById(childId);

        assertEquals(getChildResponse1, response);
        verify(authorizationHelper, times(1)).validateAndAuthorizeChild(childId);
        verify(getChildConverter, times(1)).toDto(child1);
    }
}