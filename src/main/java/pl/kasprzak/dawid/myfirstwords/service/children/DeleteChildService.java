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
     * Service method for deleting a child identified by the given ID after validating and authorizing the parent.
     * This method uses the AuthorizationHelper to validate and authorize the authenticated parent. If the parent is authorized,
     * the method deletes the child entity from the repository.
     *
     * @param childId        the ID of the child to be deleted.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     */
    public void deleteChild(Long childId) {
        ChildEntity child = authorizationHelper.validateAndAuthorizeChild(childId);
        childrenRepository.deleteById(child.getId());
    }
}
