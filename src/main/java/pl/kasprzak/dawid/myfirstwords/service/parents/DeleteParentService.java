package pl.kasprzak.dawid.myfirstwords.service.parents;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

@Service
@RequiredArgsConstructor
public class DeleteParentService {

    private final ParentsRepository parentsRepository;

    /**
     * Service method for deleting a parent account by the given parent ID.
     * This method checks if a parent with the specified ID exists, and if so, deletes the parent.
     * If the parent does not exist, a ParentNotFoundException is thrown.
     *
     * @param parentId the ID of the parent to be deleted.
     * @throws ParentNotFoundException if a parent with specified ID is not found.
     */
    public void deleteAccount(Long parentId) {
        if (parentsRepository.existsById(parentId)) {
            parentsRepository.deleteById(parentId);
        } else {
            throw new ParentNotFoundException("Parent not found");
        }
    }
}
