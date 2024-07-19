package pl.kasprzak.dawid.myfirstwords.service.converters.milestones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.model.milestones.GetMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class GetMilestoneConverterTest {

    @InjectMocks
    private GetMilestoneConverter getMilestoneConverter;
    private MilestoneEntity milestoneEntity;

    @BeforeEach
    void setUp(){
        milestoneEntity = new MilestoneEntity();
        milestoneEntity.setId(1L);
        milestoneEntity.setTitle("testTitle");
        milestoneEntity.setDescription("example description");
        milestoneEntity.setDateAchieve(LocalDate.now().minusDays(15));
    }

    @Test
    void when_callFromDto_then_throwUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> getMilestoneConverter.fromDto(null));
    }

    @Test
    void when_toDto_then_returnGetMilestoneResponse() {
        GetMilestoneResponse result = getMilestoneConverter.toDto(milestoneEntity);

        assertEquals(milestoneEntity.getId(), result.getId());
        assertEquals(milestoneEntity.getTitle(), result.getTitle());
        assertEquals(milestoneEntity.getDescription(), result.getDescription());
        assertEquals(milestoneEntity.getDateAchieve(), result.getDateAchieve());
    }
}