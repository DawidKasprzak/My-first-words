package pl.kasprzak.dawid.myfirstwords.service.children;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildRequest;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.children.CreateChildConverter;

@Service
@RequiredArgsConstructor
public class CreateChildService {

    private final ChildrenRepository childrenRepository;
    private final ParentsRepository parentsRepository;
    private final CreateChildConverter createChildConverter;

    /**
     * Service method for adding a new child for the authenticated parent.
     * This method retrieves the currently authenticated parent's username from the SecurityContextHolder,
     * converts the CreateChildRequest DTO to a ChildEntity, sets the parent of the child,
     * saves the child entity to the repository, and converts the saved entity to a CreateChildResponse DTO.
     *
     * @param request        the CreateChildRequest containing the child's details.
     * @return a CreateChildResponse DTO containing the details of the newly created child.
     * @throws ParentNotFoundException if the authenticated parent is not found in the repository.
     */
    public CreateChildResponse addChild(CreateChildRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ParentEntity parent = parentsRepository.findByUsername(username)
                .orElseThrow(() -> new ParentNotFoundException("Parent not found"));
        ChildEntity childEntity = createChildConverter.fromDto(request);
        childEntity.setParent(parent);
        ChildEntity savedEntity = childrenRepository.save(childEntity);
        return createChildConverter.toDto(savedEntity);
    }
}
