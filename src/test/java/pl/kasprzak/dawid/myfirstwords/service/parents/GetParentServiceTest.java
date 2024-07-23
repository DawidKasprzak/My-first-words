package pl.kasprzak.dawid.myfirstwords.service.parents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.parents.GetAllParentsResponse;
import pl.kasprzak.dawid.myfirstwords.model.parents.ParentInfoResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.parents.GetParentsConverter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class GetParentServiceTest {
    @Mock
    private ParentsRepository parentsRepository;
    @Mock
    private GetParentsConverter getParentsConverter;
    @InjectMocks
    private GetParentService getParentService;

    private ParentEntity parent1, parent2;
    private ParentInfoResponse parentInfoResponse1, parentInfoResponse2;

    @BeforeEach
    void setUp(){
        parent1 = new ParentEntity();
        parent1.setId(1L);
        parent1.setUsername("parent1");
        parent1.setMail("parent1@mail.com");

        parent2 = new ParentEntity();
        parent2.setId(2L);
        parent2.setUsername("parent2");
        parent2.setMail("parent2@mail.com");

        parentInfoResponse1 = ParentInfoResponse.builder()
                .id(1L)
                .username("parent1")
                .mail("parent1@mail.com")
                .children(Collections.emptyList())
                .build();

        parentInfoResponse2 = ParentInfoResponse.builder()
                .id(2L)
                .username("parent2")
                .mail("parent2@mail.com")
                .children(Collections.emptyList())
                .build();

    }


    /**
     * Unit test for getAll method in GetParentService.
     * Verifies that all parents are retrieved and correctly converted to DTOs.
     */
    @Test
    void when_getAll_then_returnAllParents() {

        List<ParentEntity> parents = Arrays.asList(parent1, parent2);
        List<ParentInfoResponse> result = Arrays.asList(parentInfoResponse1, parentInfoResponse2);

        when(parentsRepository.findAll()).thenReturn(parents);
        when(getParentsConverter.toDto(parent1)).thenReturn(parentInfoResponse1);
        when(getParentsConverter.toDto(parent2)).thenReturn(parentInfoResponse2);

        GetAllParentsResponse response = getParentService.getAll();

        assertEquals(result, response.getParents());
        verify(parentsRepository, times(1)).findAll();
        verify(getParentsConverter, times(1)).toDto(parent1);
        verify(getParentsConverter, times(1)).toDto(parent2);

    }

    /**
     * Unit test for getByID method in GetParentService.
     * Verifies that a parent is retrieved by ID and correctly converted to a DTO.
     */
    @Test
    void when_getById_then_returnParent() {
        Long parentId = 1L;

        when(parentsRepository.findById(parentId)).thenReturn(Optional.of(parent1));
        when(getParentsConverter.toDto(parent1)).thenReturn(parentInfoResponse1);

        ParentInfoResponse response = getParentService.getById(parentId);

        assertEquals(parentInfoResponse1, response);
        verify(parentsRepository, times(1)).findById(parentId);
        verify(getParentsConverter, times(1)).toDto(parent1);
    }

    /**
     * Unit test for getByID method in GetParentService.
     * Verifies that a ParentNotFoundException is thrown when the parent ID does not exist.
     */
    @Test
    void when_getById_then_throwParentNotFoundException(){
        Long parentId = 1L;

        when(parentsRepository.findById(parentId)).thenReturn(Optional.empty());

        ParentNotFoundException parentNotFoundException = assertThrows(ParentNotFoundException.class, () -> getParentService.getById(parentId));

        assertEquals("Parent not found with id: " + parentId,parentNotFoundException.getMessage());
        verify(parentsRepository, times(1)).findById(parentId);
        verify(getParentsConverter, never()).toDto(any());
    }
}