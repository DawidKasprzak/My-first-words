package pl.kasprzak.dawid.myfirstwords.service.children;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.AdminMissingParentIDException;
import pl.kasprzak.dawid.myfirstwords.model.children.GetAllChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.GetChildResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.children.GetChildConverter;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import org.springframework.security.access.AccessDeniedException;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetChildService {

    private final ChildrenRepository childrenRepository;
    private final GetChildConverter getChildConverter;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Service method for retrieving all children associated with a specified parent.
     * This method handles the retrieval of children based on the role of the authenticated user:
     * If the authenticated user is an administrator, they must provide a valid `parentID` to retrieve
     * the children associated with that parent. The method will throw an `IllegalArgumentException`
     * if `parentID` is null.
     * If the authenticated user is a parent, the method will automatically retrieve the children
     * associated with the authenticated parent, using their username from the SecurityContextHolder.
     * This method:
     * 1. Validates and authorizes the parent or administrator using the `validateParentOrAdmin` method.
     * 2. Fetches all children associated with the retrieved parent entity.
     * 3. Converts the list of child entities to DTOs and wraps them in a `GetAllChildResponse`.
     *
     * @param parentID The ID of the parent whose children are to be retrieved. Optional for parents,
     *                 but required for admins.
     * @return a GetAllChildResponse containing a list of all child DTOs for the specified or authenticated parent.
     * @throws ParentNotFoundException       if the parent with the specified `parentID` or the authenticated parent
     *                                       cannot be found.
     * @throws AdminMissingParentIDException if the authenticated user is an admin and does not provide a `parentID`.
     */
    @Transactional
    public GetAllChildResponse getAllChildrenOfParent(Long parentID) {
        ParentEntity parent = authorizationHelper.validateParentOrAdmin(parentID);
        return GetAllChildResponse.builder()
                .children(childrenRepository.findByParentId(parent.getId()).stream()
                        .map(getChildConverter::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Service method for retrieving a child by the given ID and converting it to a DTO.
     * This method validates and authorizes the request based on the role of the authenticated user.
     * - If the authenticated user is a parent, their identity is retrieved from the SecurityContextHolder,
     * and the method validates and authorizes access to the child based on the parent's access rights.
     * - If the authenticated user is an administrator, the method requires a parentID to validate and
     * authorize access to the child associated with that parent.
     * The child entity is then converted to a DTO and returned.
     *
     * @param childId  the ID of the child to be retrieved.
     * @param parentID the ID of the parent, required if the authenticated user is an administrator.
     * @return a GetChildResponse containing the child DTO.
     * @throws AdminMissingParentIDException if the authenticated user is an administrator and the parentID is null.
     * @throws ParentNotFoundException       if the parent with the specified ID is not found.
     * @throws ChildNotFoundException        if the child with the given ID is not found.
     * @throws AccessDeniedException         if the authenticated parent does not have access to the child,
     *                                       or if the administrator is not authorized for the specified parent.
     */
    public GetChildResponse getChildById(Long childId, Long parentID) {
        ChildEntity child = authorizationHelper.validateAndAuthorizeForAdminOrParent(childId, parentID);
        return getChildConverter.toDto(child);
    }
}
