package pl.kasprzak.dawid.myfirstwords.service.parents;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.EmailAlreadyExistsException;
import pl.kasprzak.dawid.myfirstwords.exception.UsernameAlreadyExistsException;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentRequest;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentResponse;
import pl.kasprzak.dawid.myfirstwords.repository.AuthoritiesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.AuthorityEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.parents.CreateParentConverter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateParentService {

    private final ParentsRepository parentsRepository;
    private final AuthoritiesRepository authoritiesRepository;
    private final CreateParentConverter createParentConverter;

    /**
     * Service method for creating and saving a new parent.
     * This method converts a CreateParentRequest DTO to a ParentEntity, validates the uniqueness
     * of the username and email, assigns a default role, and then saves the new parent and their
     * associated authority to the repository. The default role assigned to every new parent is "ROLE_USER".
     *
     * @param parentRequest the CreateParentRequest DTO containing the details of the new parent to be created.
     * @return CreateParentResponse DTO containing the details of the newly created parent.
     * @throws UsernameAlreadyExistsException if a parent with the specified username already exists.
     * @throws EmailAlreadyExistsException    if a parent with the specified email already exists.
     */
    public CreateParentResponse saveParent(CreateParentRequest parentRequest) {
        validateUniqueUsernameAndEmail(parentRequest);

        ParentEntity parentEntity = createParentConverter.fromDto(parentRequest);

        AuthorityEntity userAuthority = new AuthorityEntity();
        userAuthority.setAuthority("ROLE_USER");

        authoritiesRepository.save(userAuthority);

        parentEntity.setAuthorities(List.of(userAuthority));

        ParentEntity savedEntity = parentsRepository.save(parentEntity);
        return createParentConverter.toDto(savedEntity);
    }

    /**
     * Validates the uniqueness of the username and email in the given CreateParentRequest.
     * This method checks if a parent with the specified username or email already exists in the repository.
     * If a duplicate is found, an appropriate exception is thrown.
     *
     * @param parentRequest the CreateParentRequest DTO containing the username and email to be validated.
     * @throws UsernameAlreadyExistsException if a parent with the specified username already exists.
     * @throws EmailAlreadyExistsException    if a parent with the specified email already exists.
     */
    private void validateUniqueUsernameAndEmail(CreateParentRequest parentRequest) {
        parentsRepository.findByUsername(parentRequest.getUsername()).ifPresent(parentEntity -> {
            throw new UsernameAlreadyExistsException("Username already exists: " + parentRequest.getUsername());
        });
        parentsRepository.findByMail(parentRequest.getMail()).ifPresent(parentEntity -> {
            throw new EmailAlreadyExistsException("Email already exists: " + parentRequest.getMail());
        });
    }
}
