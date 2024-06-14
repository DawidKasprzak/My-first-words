package pl.kasprzak.dawid.myfirstwords.service.parents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
class DeleteParentServiceTest {

    @Mock
    private ParentsRepository parentsRepository;
    @InjectMocks
    private DeleteParentService deleteParentService;

    @Test
    void when_deleteParent_then_parentShouldBeRemoved() {
        Long parentId = 1L;

        when(parentsRepository.existsById(parentId)).thenReturn(true);

        deleteParentService.deleteAccount(parentId);

        verify(parentsRepository).existsById(parentId);
        verify(parentsRepository).deleteById(parentId);

    }

    @Test
    void when_deleteNonexistentParent_then_throwParentNotFoundException(){
        Long parentId = 1L;

        when(parentsRepository.existsById(parentId)).thenReturn(false);

        assertThrows(ParentNotFoundException.class, ()-> deleteParentService.deleteAccount(parentId));

        verify(parentsRepository).existsById(parentId);
        verify(parentsRepository, never()).deleteById(parentId);
    }
}