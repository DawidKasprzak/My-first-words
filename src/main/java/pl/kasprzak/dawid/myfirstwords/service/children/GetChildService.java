package pl.kasprzak.dawid.myfirstwords.service.children;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
     * Service method for retrieving all children of the authenticated parent.
     * This method finds the parent by username from the authentication object, fetches all children associate
     * with the parent, converts them to the DTOs, and returns them wrapped in a GetAllChildResponse.
     *
     * @param authentication the authentication object containing the parent's credentials.
     * @return a GetAllChildResponse containing a list of all child DTOs for the authenticated parent.
     * @throws ParentNotFoundException if the parent with the specified username is not found.
     */
    @Transactional
    public GetAllChildResponse getAllChildrenOfParent(Authentication authentication) {
        ParentEntity parent = parentsRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ParentNotFoundException("Parent not found"));
        return GetAllChildResponse.builder()
                .children(childrenRepository.findByParentId(parent.getId()).stream()
                        .map(getChildConverter::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Service method for retrieving a child by the given ID and converts it to a DTO.
     * This method validates and authorizes the parent using the AuthorizationHelper, and if authorized,
     * converts the child entity to a DTO and returns it.
     *
     * @param childId        the ID of the child to be retrieved.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a GetChildResponse containing the child DTO.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     */
    public GetChildResponse getChildById(Long childId, Authentication authentication) {
        ChildEntity childEntity = authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        return getChildConverter.toDto(childEntity);
    }
}
