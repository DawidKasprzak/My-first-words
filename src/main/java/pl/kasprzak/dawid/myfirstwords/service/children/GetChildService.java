package pl.kasprzak.dawid.myfirstwords.service.children;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.GetAllChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.GetChildResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.service.converters.children.CreateChildConverter;
import pl.kasprzak.dawid.myfirstwords.service.converters.children.GetChildConverter;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetChildService {

    private final ChildrenRepository childrenRepository;
    private final GetChildConverter getChildConverter;

    public GetAllChildResponse getAll() {
        return GetAllChildResponse.builder()
                .children(childrenRepository.findAll().stream()
                        .map(getChildConverter::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public GetChildResponse getChildByName(String name) {
        return childrenRepository.findByName(name)
                .map(getChildConverter::toDto)
                .orElseThrow(() -> new NoSuchElementException("Child not found with name: " + name));
    }


}
