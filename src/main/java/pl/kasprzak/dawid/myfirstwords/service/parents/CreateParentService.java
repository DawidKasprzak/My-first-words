package pl.kasprzak.dawid.myfirstwords.service.parents;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.EmailAlreadyExistsException;
import pl.kasprzak.dawid.myfirstwords.exception.UsernameAlreadyExistsException;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentRequest;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.parents.CreateParentConverter;

@Service
@RequiredArgsConstructor
public class CreateParentService {

    private final ParentsRepository parentsRepository;
    private final CreateParentConverter createParentConverter;

    /**
     * Service method for creating and saving a new parent.
     * This method converts a CreateParentRequest DTO to a parentEntity, checks for existing usernames and emails,
     * and if non exist, saves the new parent to the repository.
     *
     * @param parentRequest the CreateParentRequest DTO containing the details of the new parent to be created.
     * @return CreateParentResponse DTO containing the details of the newly created parent.
     * @throws UsernameAlreadyExistsException if a parent with the specified username already exists.
     * @throws EmailAlreadyExistsException    if a parent with the specified email already exists.
     */
    public CreateParentResponse saveParent(CreateParentRequest parentRequest) {
        if (parentsRepository.findByUsername(parentRequest.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists: " + parentRequest.getUsername());
        }
        if (parentsRepository.findByMail(parentRequest.getMail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists: " + parentRequest.getMail());
        }
        ParentEntity savedEntity = parentsRepository.save(createParentConverter.fromDto(parentRequest));
        return createParentConverter.toDto(savedEntity);
    }
}
