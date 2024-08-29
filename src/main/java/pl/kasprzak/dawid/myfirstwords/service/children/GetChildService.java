package pl.kasprzak.dawid.myfirstwords.service.children;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.children.GetAllChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.GetChildResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
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
    private final ParentsRepository parentsRepository;
    private final GetChildConverter getChildConverter;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Service method for retrieving all children associated with a specified parent.
     * If a `parentID` is provided and the authenticated user is an admin,
     * the method will retrieve the children of the parent with the specified `parentID`.
     * If no `parentID` is provided, the method will retrieve the children of the authenticated parent.
     * If the `parentID` is null and the authenticated user is an admin,
     * an exception will be thrown, requiring the admin to provide a `parentID`.
     * This method:
     * 1. Retrieves the parent entity by `parentID` if provided.
     * 2. If `parentID` is not provided, retrieves the username of the authenticated parent
     * from the SecurityContextHolder and finds the parent entity by username.
     * 3. Fetches all children associated with the retrieved parent entity.
     * 4. Converts the list of child entities to DTOs and wraps them in a GetAllChildResponse.
     *
     * @param parentID The ID of the parent whose children are to be retrieved. Optional for parents,
     *                 but required for admins.
     * @return a GetAllChildResponse containing a list of all child DTOs for the specified or authenticated parent.
     * @throws ParentNotFoundException  if the parent with the specified `parentID` or the authenticated parent
     *                                  cannot be found.
     * @throws IllegalArgumentException if the authenticated user is an admin and does not provide a `parentID`.
     */
    @Transactional
    public GetAllChildResponse getAllChildrenOfParent(Long parentID) {
        if (parentID == null && isAdmin()) {
            throw new IllegalArgumentException("Admin must provide a parentID to retrieve children");
        }
        ParentEntity parent;
        if (parentID != null) {
            parent = parentsRepository.findById(parentID)
                    .orElseThrow(() -> new ParentNotFoundException("Parent not found"));
        } else {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            parent = parentsRepository.findByUsername(username)
                    .orElseThrow(() -> new ParentNotFoundException("Parent not found"));
        }
        return GetAllChildResponse.builder()
                .children(childrenRepository.findByParentId(parent.getId()).stream()
                        .map(getChildConverter::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Checks if the authenticated user has the role of an administrator.
     * This method retrieves the authentication details from the SecurityContextHolder
     * and checks if the user has the `ROLE_ADMIN` authority.
     *
     * @return `true` if the authenticated user has the `ROLE_ADMIN` authority, otherwise `false`.
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Service method for retrieving a child by the given ID and converting it to a DTO.
     * This method validates and authorizes the parent using the AuthorizationHelper, and if authorized,
     * converts the child entity to a DTO and returns it.
     * The parent's identity is retrieved from the SecurityContextHolder.
     *
     * @param childId the ID of the child to be retrieved.
     * @return a GetChildResponse containing the child DTO.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     */
    public GetChildResponse getChildById(Long childId) {
        ChildEntity childEntity = authorizationHelper.validateAndAuthorizeChild(childId);
        return getChildConverter.toDto(childEntity);
    }
}
