package pl.kasprzak.dawid.myfirstwords.service.children;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

@Service
@RequiredArgsConstructor
public class DeleteChildService {


    private final AuthorizationHelper authorizationHelper;
    private final ChildrenRepository childrenRepository;


    public void deleteChild(Long childId, Authentication authentication) {
        ChildEntity child = authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        childrenRepository.deleteById(child.getId());
    }
}
