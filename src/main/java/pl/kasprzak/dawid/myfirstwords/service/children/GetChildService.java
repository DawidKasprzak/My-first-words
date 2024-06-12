package pl.kasprzak.dawid.myfirstwords.service.children;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.GetAllChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.GetChildResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.children.CreateChildConverter;
import pl.kasprzak.dawid.myfirstwords.service.converters.children.GetChildConverter;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetChildService {

    private final ChildrenRepository childrenRepository;
    private final ParentsRepository parentsRepository;
    private final GetChildConverter getChildConverter;
    private final AuthorizationHelper authorizationHelper;

    @Transactional
    public GetAllChildResponse getAllChildrenForParent(Authentication authentication) {
        ParentEntity parent = parentsRepository.findByUsername(authentication.getName())
                .orElseThrow(()-> new ParentNotFoundException("Parent not found"));
        return GetAllChildResponse.builder()
                .children(childrenRepository.findByParentId(parent.getId()).stream()
                        .map(getChildConverter::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public GetChildResponse getChildById(Long childId, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        return childrenRepository.findById(childId)
                .map(getChildConverter::toDto)
                .orElseThrow(() -> new ChildNotFoundException("Child not found with id: " + childId));
    }


}
