package pl.kasprzak.dawid.myfirstwords.service.children;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.exception.AdminMissingParentIDException;
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
     * Verifies that the parent is authenticated and authorized using AuthorizationHelper.
     * Then verifies that all children of the authenticated parent are retrieved and correctly converted to DTOs.
     */
    @Test
    void when_getAllChildrenOfParent_then_returnAllChildren() {
        List<ChildEntity> children = Arrays.asList(child1, child2);
        List<GetChildResponse> expectResponse = Arrays.asList(getChildResponse1, getChildResponse2);

        when(authorizationHelper.validateParentOrAdmin(null)).thenReturn(parentEntity);
        when(childrenRepository.findByParentId(1L)).thenReturn(children);
        when(getChildConverter.toDto(child1)).thenReturn(getChildResponse1);
        when(getChildConverter.toDto(child2)).thenReturn(getChildResponse2);

        GetAllChildResponse response = getChildService.getAllChildrenOfParent(null);

        assertEquals(expectResponse, response.getChildren());
        verify(childrenRepository, times(1)).findByParentId(1L);
        verify(getChildConverter, times(1)).toDto(child1);
        verify(getChildConverter, times(1)).toDto(child2);
    }

    /**
     * Unit test for the getAllChildrenOfParent method in GetChildService.
     * Verifies that when an administrator provides a valid parent ID, the method correctly retrieves
     * all children associated with that parent and converts them to DTOs.
     * Simulates the admin scenario using lenient stubbing.
     */
    @Test
    void when_adminGetsAllChildrenByParentID_then_returnAllChildrenForSpecifiedParent() {
        List<ChildEntity> children = Arrays.asList(child1, child2);
        List<GetChildResponse> expectedResponse = Arrays.asList(getChildResponse1, getChildResponse2);

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateParentOrAdmin(parentEntity.getId())).thenReturn(parentEntity);
        when(childrenRepository.findByParentId(1L)).thenReturn(children);
        when(getChildConverter.toDto(child1)).thenReturn(getChildResponse1);
        when(getChildConverter.toDto(child2)).thenReturn(getChildResponse2);

        GetAllChildResponse response = getChildService.getAllChildrenOfParent(1L);

        assertEquals(expectedResponse, response.getChildren());
        verify(childrenRepository, times(1)).findByParentId(1L);
        verify(getChildConverter, times(1)).toDto(child1);
        verify(getChildConverter, times(1)).toDto(child2);
    }

    /**
     * Unit test for getAllChildrenOfParent method in GetChildService.
     * Verifies that a ParentNotFoundException is thrown and the appropriate error message is returned
     * when the parent is not found during the validation process.
     */
    @Test
    void when_getAllChildrenOfParent_then_throwParentNotFoundException() {

        when(authorizationHelper.validateParentOrAdmin(null))
                .thenThrow(new ParentNotFoundException("Parent not found"));

        ParentNotFoundException parentNotFoundException = assertThrows(ParentNotFoundException.class,
                () -> getChildService.getAllChildrenOfParent(null));

        assertEquals("Parent not found", parentNotFoundException.getMessage());
        verify(authorizationHelper, times(1)).validateParentOrAdmin(null);
        verify(childrenRepository, never()).findByParentId(anyLong());
        verify(getChildConverter, never()).toDto(any());

    }

    /**
     * Unit test for the getAllChildrenOfParent method in GetChildService.
     * Verifies that when an administrator does not provide a parent ID, the method throws
     * an AdminMissingParentIDException with the appropriate error message.
     * This test uses lenient stubbing to simulate an admin user who fails to provide a parent ID.
     */
    @Test
    void when_adminDoesNotProvideParentID_then_throwIllegalArgumentException() {

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateParentOrAdmin(null))
                .thenThrow(new AdminMissingParentIDException("Admin must provide a parentID to retrieve children"));

        AdminMissingParentIDException adminMissingParentIDException = assertThrows(AdminMissingParentIDException.class,
                () -> getChildService.getAllChildrenOfParent(null));

        assertEquals("Admin must provide a parentID to retrieve children", adminMissingParentIDException.getMessage());
        verify(authorizationHelper, times(1)).validateParentOrAdmin(null);
        verify(parentsRepository, never()).findById(anyLong());
        verify(childrenRepository, never()).findByParentId(anyLong());
    }

    /**
     * Unit test for getChildById method in GetChildService.
     * Verifies that the child belongs to the authenticated parent.
     * Then verifies that a child with a specific ID is retrieved and correctly converted to a DTO.
     */
    @Test
    void when_getChildById_then_returnChildWithSpecificId() {
        Long childId = child1.getId();

        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childId, null)).thenReturn(child1);
        when(getChildConverter.toDto(child1)).thenReturn(getChildResponse1);

        GetChildResponse response = getChildService.getChildById(childId, null);

        assertEquals(getChildResponse1, response);
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childId, null);
        verify(getChildConverter, times(1)).toDto(child1);
    }


    /**
     * Unit test for getChildById method in GetChildService when accessed by an administrator.
     * This test verifies that when an administrator requests a child's details by its ID and the parent's ID,
     * the child entity is retrieved and correctly converted to a DTO.
     * Lenient stubbing is used to simulate an admin user.
     */
    @Test
    void when_adminGetsChildById_then_childShouldBeReturned() {
        Long parentID = parentEntity.getId();
        Long childID = child1.getId();

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childID, parentID)).thenReturn(child1);
        when(getChildConverter.toDto(child1)).thenReturn(getChildResponse1);

        GetChildResponse response = getChildService.getChildById(childID, parentID);

        assertEquals(childID, response.getId());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childID, parentID);
        verify(getChildConverter, times(1)).toDto(child1);
    }

    /**
     * Unit test for the getChildById method in GetChildService.
     * Verifies that when an administrator does not provide a parent ID, the method throws
     * an AdminMissingParentIDException with the appropriate error message.
     * Lenient stubbing is used to simulate an admin user who fails to provide a parent ID.
     */
    @Test
    void when_adminDoesNotProvideParentIDForSingleChild_then_throwIllegalArgumentException() {
        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(child1.getId(), null))
                .thenThrow(new AdminMissingParentIDException("Admin must provide a parentID to retrieve children"));

        AdminMissingParentIDException adminMissingParentIDException = assertThrows(AdminMissingParentIDException.class,
                () -> getChildService.getChildById(1L, null));

        assertEquals("Admin must provide a parentID to retrieve children", adminMissingParentIDException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(child1.getId(), null);
        verify(getChildConverter, never()).toDto(any());
    }
}