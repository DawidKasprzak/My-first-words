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
