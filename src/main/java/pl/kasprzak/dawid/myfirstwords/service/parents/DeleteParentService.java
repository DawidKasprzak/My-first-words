package pl.kasprzak.dawid.myfirstwords.service.parents;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
            throw new RuntimeException("Parent not found");
        }
    }
}
