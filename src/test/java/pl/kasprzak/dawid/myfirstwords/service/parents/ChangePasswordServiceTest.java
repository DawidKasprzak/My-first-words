package pl.kasprzak.dawid.myfirstwords.service.parents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.parents.ChangePasswordRequest;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
class ChangePasswordServiceTest {
    @Mock
    private ParentsRepository parentsRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private ChangePasswordService changePasswordService;

    @Test
    void when_changePassword_then_passwordShouldBeChanged() {
        Long parentId = 1L;
        String newPassword = "newPassword";
        String encodedPassword = "encodedPassword";

        ParentEntity parent = new ParentEntity();
        parent.setId(parentId);
        parent.setPassword("oldPassword");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setPassword(newPassword);

        when(parentsRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        changePasswordService.changePasswordForParent(parentId, request);

        verify(parentsRepository).findById(parentId);
        verify(passwordEncoder).encode(newPassword);
        verify(parentsRepository).save(parent);

        assertEquals(encodedPassword, parent.getPassword());
    }

    @Test
    void when_changePassword_parentNotFound_then_throwParentNotFoundException(){
        Long parentId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setPassword("newPassword");

        when(parentsRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThrows(ParentNotFoundException.class, ()-> changePasswordService.changePasswordForParent(parentId, request));
    }
}