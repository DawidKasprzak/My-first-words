package pl.kasprzak.dawid.myfirstwords.service.converters.parents;

import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.model.children.GetChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.parents.ParentInfoResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.Convertable;
import pl.kasprzak.dawid.myfirstwords.service.converters.children.GetChildConverter;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetParentsConverter implements Convertable<Void, ParentEntity, ParentInfoResponse> {

    private final GetChildConverter getChildConverter;

    public GetParentsConverter(GetChildConverter getChildConverter) {
        this.getChildConverter = getChildConverter;
    }

    @Override
    public ParentEntity fromDto(Void input) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParentInfoResponse toDto(ParentEntity parentEntity) {
        List<GetChildResponse> children = parentEntity.getChildren()
                .stream()
                .map(getChildConverter::toDto)
                .collect(Collectors.toList());

        return ParentInfoResponse.builder()
                .id(parentEntity.getId())
                .username(parentEntity.getUsername())
                .mail(parentEntity.getMail())
                .children(children)
                .build();
    }

}
