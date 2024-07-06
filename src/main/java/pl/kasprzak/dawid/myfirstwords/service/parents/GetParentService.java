package pl.kasprzak.dawid.myfirstwords.service.parents;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.parents.GetAllParentsResponse;
import pl.kasprzak.dawid.myfirstwords.model.parents.ParentInfoResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.service.converters.parents.GetParentsConverter;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetParentService {

    private final ParentsRepository parentsRepository;
    private final GetParentsConverter getParentsConverter;

    public GetAllParentsResponse getAll() {
        return GetAllParentsResponse.builder()
                .parents(parentsRepository.findAll().stream()
                        .map(getParentsConverter::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public ParentInfoResponse getById(Long parentId) {
        return parentsRepository.findById(parentId)
                .map(getParentsConverter::toDto)
                .orElseThrow(() -> new ParentNotFoundException("Parent not found with id: " + parentId));
    }
}
