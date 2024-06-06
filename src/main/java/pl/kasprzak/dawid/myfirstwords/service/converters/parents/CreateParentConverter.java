package pl.kasprzak.dawid.myfirstwords.service.converters.parents;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentRequest;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.Convertable;

@Service
@RequiredArgsConstructor
public class CreateParentConverter implements Convertable<CreateParentRequest, ParentEntity, CreateParentResponse> {

    private final PasswordEncoder passwordEncoder;


    @Override
    public ParentEntity fromDto(CreateParentRequest input) {
        ParentEntity parentEntity = new ParentEntity();
        parentEntity.setUsername(input.getUsername());
        parentEntity.setMail(input.getMail());
        parentEntity.setPassword(passwordEncoder.encode(input.getPassword()));
        return parentEntity;
    }

    @Override
    public CreateParentResponse toDto(ParentEntity parentEntity) {
        return CreateParentResponse.builder()
                .username(parentEntity.getUsername())
                .mail(parentEntity.getMail())
                .build();
    }
}
