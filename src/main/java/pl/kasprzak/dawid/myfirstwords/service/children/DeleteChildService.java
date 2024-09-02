package pl.kasprzak.dawid.myfirstwords.service.children;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class DeleteChildService {


    private final AuthorizationHelper authorizationHelper;
    private final ChildrenRepository childrenRepository;

    /**
     * Service method for deleting a child identified by the given ID after validating and authorizing
     * either the authenticated parent or an administrator.
     * If the authenticated user is an administrator, the method requires a parentID to validate and
     * authorize the operation for the specified parent. If the user is not an administrator, the method
     * validates and authorizes the operation based on the authenticated parent's access to the child.
     * The method uses the AuthorizationHelper to perform the necessary validation and authorization.
     * Once validated, the child entity is deleted from the repository.
     *
     * @param childId  the ID of the child to be deleted.
     * @param parentID the ID of the parent, required if the authenticated user is an administrator.
     * @throws IllegalArgumentException if the authenticated user is an administrator and the parentID is null.
     * @throws ParentNotFoundException  if the parent with the specified ID is not found.
     * @throws ChildNotFoundException   if the child with the specified ID is not found.
     * @throws AccessDeniedException    if the authenticated parent does not have access to the child,
     *                                  or if the administrator is not authorized for the specified parent.
     */
    public void deleteChild(Long childId, Long parentID) {
        ChildEntity child = authorizationHelper.validateAndAuthorizeForAdminOrParent(childId, parentID);
        childrenRepository.deleteById(child.getId());
    }
}
