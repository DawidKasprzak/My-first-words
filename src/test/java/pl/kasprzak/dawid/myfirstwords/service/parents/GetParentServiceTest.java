package pl.kasprzak.dawid.myfirstwords.service.parents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetParentServiceTest {
    @Mock
    private ParentsRepository parentsRepository;
    @InjectMocks
    private GetParentService getParentService;

    @Test
    void testGetAll() {
    }

    @Test
    void testGetById() {
    }
}