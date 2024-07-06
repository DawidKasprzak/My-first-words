package pl.kasprzak.dawid.myfirstwords.service.converters.children;

import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.model.children.GetChildResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.Convertable;

@Service
public class GetChildConverter implements Convertable<Void, ChildEntity, GetChildResponse> {
    @Override
    public ChildEntity fromDto(Void input) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetChildResponse toDto(ChildEntity childEntity) {
        return GetChildResponse.builder()
                .id(childEntity.getId())
                .name(childEntity.getName())
                .birthDate(childEntity.getBirthDate())
                .build();
    }
}
