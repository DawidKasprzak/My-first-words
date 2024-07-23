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
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ChangePasswordServiceTest {
    @Mock
    private ParentsRepository parentsRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private ChangePasswordService changePasswordService;
    private ChangePasswordRequest request;

    /**
     * Unit test for changePasswordForParent method in ChangePasswordService.
     * Verifies that the parent's password is successfully changed when the parent ID exists.
     */
    @Test
    void when_changePassword_then_passwordShouldBeChanged() {
        Long parentId = 1L;
        String newPassword = "newPassword";
        String encodedPassword = "encodedPassword";

        ParentEntity parent = new ParentEntity();
        parent.setId(parentId);
        parent.setPassword("oldPassword");

        request = new ChangePasswordRequest();
        request.setPassword(newPassword);

        when(parentsRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        changePasswordService.changePasswordForParent(parentId, request);

        assertEquals(encodedPassword, parent.getPassword());
        verify(parentsRepository, times(1)).findById(parentId);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(parentsRepository, times(1)).save(parent);
    }

    /**
     * Unit test for changePasswordForParent method in ChangePasswordService.
     * Verifies that a ParentNotFoundException is thrown when the parent ID does not exist.
     */
    @Test
    void when_changePassword_parentNotFound_then_throwParentNotFoundException() {
        Long parentId = 1L;
        request = new ChangePasswordRequest();
        request.setPassword("newPassword");

        when(parentsRepository.findById(parentId)).thenReturn(Optional.empty());

        ParentNotFoundException parentNotFoundException = assertThrows(ParentNotFoundException.class,
                () -> changePasswordService.changePasswordForParent(parentId, request));

        assertEquals("Parent not found", parentNotFoundException.getMessage());
        verify(parentsRepository, times(1)).findById(parentId);
        verify(parentsRepository, never()).save(any());
    }
}