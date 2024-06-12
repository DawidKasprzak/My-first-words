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

    public void deleteAccount(Long parentId){
        if (parentsRepository.existsById(parentId)){
            parentsRepository.deleteById(parentId);
        } else {
            throw new ParentNotFoundException("Parent not found");
        }
    }
}
