package pl.kasprzak.dawid.myfirstwords.service.parents;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.parents.ChangePasswordRequest;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

@Service
@RequiredArgsConstructor
public class ChangePasswordService {

    private final ParentsRepository parentsRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Service method for changing the password for a parent identified by the given ID.
     * This method retrieves the parent by ID, encodes the new password, and updates the parent's password in the repository.
     *
     * @param id the ID of the parent whose password is to be changed.
     * @param request the ChangePasswordRequest containing the new password.
     * @throws ParentNotFoundException if the parent with the specified ID is not found.
     */
    public void changePasswordForParent(Long id, ChangePasswordRequest request) {
        ParentEntity parentEntity = parentsRepository.findById(id).orElseThrow(() -> new ParentNotFoundException("Parent not found"));
        parentEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        parentsRepository.save(parentEntity);
    }
}
