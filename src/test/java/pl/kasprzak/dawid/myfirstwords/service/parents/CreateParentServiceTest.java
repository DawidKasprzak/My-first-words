package pl.kasprzak.dawid.myfirstwords.service.parents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentRequest;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.parents.CreateParentConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateParentServiceTest {

    @Mock
    private ParentsRepository parentsRepository;
    @Mock
    private CreateParentConverter createParentConverter;
    @InjectMocks
    private CreateParentService createParentService;

    @Test
    void when_createNewParent_then_parentShouldBeSaved() {

        CreateParentRequest request = new CreateParentRequest();
        ParentEntity entity = new ParentEntity();
        CreateParentResponse response = new CreateParentResponse();

        when(createParentConverter.fromDto(request)).thenReturn(entity);
        when(parentsRepository.save(entity)).thenReturn(entity);
        when(createParentConverter.toDto(entity)).thenReturn(response);

        CreateParentResponse result = createParentService.saveParent(request);

        assertEquals(result, response);
        verify(createParentConverter).fromDto(request);
        verify(parentsRepository).save(entity);
        verify(createParentConverter).toDto(entity);

    }
}