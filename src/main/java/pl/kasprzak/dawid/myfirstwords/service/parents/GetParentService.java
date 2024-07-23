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

    /**
     * Service method for retrieving all parents from the repository and converts them to DTOs.
     * This method fetches all parent entities, converts them to DTOs, and returns them wrapped in a GetAllParentsResponse.
     *
     * @return a GetAllParentsResponse containing a list of all parent DTOs.
     */
    public GetAllParentsResponse getAll() {
        return GetAllParentsResponse.builder()
                .parents(parentsRepository.findAll().stream()
                        .map(getParentsConverter::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Service method for retrieving a parent by the given ID and converts it to a DTO.
     * This method fetches a parent entity by its ID, converts it to a DTO, and returns it.
     * If the parent is not found, a ParentNotFoundException is thrown.
     *
     * @param parentId the ID of the parent to be retrieved.
     * @return a ParentInfoResponse containing the parent DTO.
     * @throws ParentNotFoundException if a parent with the specified ID is not found.
     */
    public ParentInfoResponse getById(Long parentId) {
        return parentsRepository.findById(parentId)
                .map(getParentsConverter::toDto)
                .orElseThrow(() -> new ParentNotFoundException("Parent not found with id: " + parentId));
    }
}
