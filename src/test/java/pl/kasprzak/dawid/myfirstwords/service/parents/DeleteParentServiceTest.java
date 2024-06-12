package pl.kasprzak.dawid.myfirstwords.service.parents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeleteParentServiceTest {

    @Mock
    private ParentsRepository parentsRepository;
    @InjectMocks
    private DeleteParentService deleteParentService;

    @Test
    void testDeleteAccount_Success() {
        Long parentId = 1L;

        Mockito.when(parentsRepository.existsById(parentId)).thenReturn(true);

        deleteParentService.deleteAccount(parentId);

        Mockito.verify(parentsRepository).existsById(parentId);
        Mockito.verify(parentsRepository).deleteById(parentId);

    }

    @Test
    void testDeleteAccount_ParentNotFound(){
        Long parentId = 1L;

        Mockito.when(parentsRepository.existsById(parentId)).thenReturn(false);

        assertThrows(RuntimeException.class, ()-> deleteParentService.deleteAccount(parentId));

        Mockito.verify(parentsRepository).existsById(parentId);
        Mockito.verify(parentsRepository, Mockito.never()).deleteById(parentId);
    }
}