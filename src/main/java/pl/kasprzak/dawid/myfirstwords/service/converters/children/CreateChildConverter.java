package pl.kasprzak.dawid.myfirstwords.service.converters.children;

import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildRequest;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.Convertable;

@Service
public class CreateChildConverter implements Convertable<CreateChildRequest, ChildEntity, CreateChildResponse> {

    @Override
    public ChildEntity fromDto(CreateChildRequest input) {
        ChildEntity childEntity = new ChildEntity();
        childEntity.setName(input.getName());
        childEntity.setBirthDate(input.getBirthDate());
        childEntity.setGender(input.getGender());
        return childEntity;
    }

    @Override
    public CreateChildResponse toDto(ChildEntity childEntity) {
        return CreateChildResponse.builder()
                .name(childEntity.getName())
                .birthDate(childEntity.getBirthDate())
                .build();
    }
}
