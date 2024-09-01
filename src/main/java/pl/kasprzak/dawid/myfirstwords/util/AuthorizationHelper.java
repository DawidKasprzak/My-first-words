package pl.kasprzak.dawid.myfirstwords.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

@Component
@RequiredArgsConstructor
public class AuthorizationHelper {

    public final ChildrenRepository childrenRepository;
    public final ParentsRepository parentsRepository;

    /**
     * Checks if the authenticated user has the role of an administrator.
     * This method retrieves the authentication details from the SecurityContextHolder
     * and checks if the user has the `ROLE_ADMIN` authority.
     *
     * @return `true` if the authenticated user has the `ROLE_ADMIN` authority, otherwise `false`.
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }


    /**
     * Validates and authorizes a child based on the given child ID.
     * This method retrieves the username of the authenticated parent from the SecurityContextHolder,
     * checks if the parent exists, and verifies if the parent has access to the specified child.
     * If the parent or child does not exist, or if the parent does not have access, appropriate exceptions are thrown.
     *
     * @param childId The ID of the child to be validated and authorized.
     * @return The ChildEntity if validation and authorization are successful.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     */
    public ChildEntity validateAndAuthorizeChild(Long childId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ParentEntity parent = parentsRepository.findByUsername(username)
                .orElseThrow(() -> new ParentNotFoundException("Parent not found"));
        ChildEntity child = childrenRepository.findById(childId)
                .orElseThrow(() -> new ChildNotFoundException("Child not found"));
        if (!child.getParent().getId().equals(parent.getId())) {
            throw new AccessDeniedException("The parent does not have access to this child");
        }
        return child;
    }

    /**
     * Validates and authorizes access to a child based on the provided child ID and parent ID.
     * This method is intended for use by administrators to validate and authorize access to a child
     * entity belonging to a specified parent. It ensures that the child belongs to the parent with the
     * given ID and that the parent exists.
     *
     * @param childID  The ID of the child to be validated and authorized.
     * @param parentID The ID of the parent to be validated and authorized.
     * @return The ChildEntity if the validation and authorization are successful.
     * @throws ParentNotFoundException if the parent with the specified ID is not found.
     * @throws ChildNotFoundException  if the child with the specified ID is not found.
     * @throws AccessDeniedException   if the parent does not have access to the specified child.
     */
    public ChildEntity validateAndAuthorizeChildForAdmin(Long childID, Long parentID) {
        ParentEntity parent = parentsRepository.findById(parentID)
                .orElseThrow(() -> new ParentNotFoundException("Parent not found"));
        ChildEntity child = childrenRepository.findById(childID)
                .orElseThrow(() -> new ChildNotFoundException("Child not found"));
        if (!child.getParent().getId().equals(parent.getId())) {
            throw new AccessDeniedException("The parent does not have access to this child");
        }
        return child;
    }
}
