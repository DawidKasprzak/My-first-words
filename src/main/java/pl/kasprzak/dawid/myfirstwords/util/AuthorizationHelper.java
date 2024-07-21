package pl.kasprzak.dawid.myfirstwords.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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
     * Validates and authorizes a child based on the given child ID and authentication.
     * This method checks if the authenticated parent has access to the specified child.
     * If the parent or child does not exist, or if the parent does not have access, appropriate exception are thronw.
     *
     * @param childId The ID of the child to be validated and authorized.
     * @param authentication The authentication object containing the parent's credentials.
     * @return The childEntity if validation and authorization are successful.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException if the child with the given ID is not found.
     * @throws AccessDeniedException if the authenticated parent does not have access to the child.
     */
    public ChildEntity validateAndAuthorizeChild(Long childId, Authentication authentication) {
        ParentEntity parent = parentsRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ParentNotFoundException("Parent not found"));
        ChildEntity child = childrenRepository.findById(childId)
                .orElseThrow(() -> new ChildNotFoundException("Child not found"));
        if (!child.getParent().getId().equals(parent.getId())) {
            throw new AccessDeniedException("The parent does not have access to this child");
        }
        return child;
    }
}
