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

    public CreateParentResponse saveParent(CreateParentRequest parentRequest){
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
