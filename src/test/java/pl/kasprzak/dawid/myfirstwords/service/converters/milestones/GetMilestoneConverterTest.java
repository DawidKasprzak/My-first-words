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

    /**
     * Unit test for calling fromDto method on GetMilestoneConverter.
     * Verifies that an UnsupportedOperationException is thrown when fromDto is called.
     */
    @Test
    void when_callFromDto_then_throwUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> getMilestoneConverter.fromDto(null));
    }

    /**
     * Unit test for the toDto method of GetMilestoneConverter.
     * Verifies that the GetMilestoneResponse is correctly created from MilestoneEntity.
     */
    @Test
    void when_toDto_then_returnGetMilestoneResponse() {
        GetMilestoneResponse response = getMilestoneConverter.toDto(milestoneEntity);

        assertEquals(milestoneEntity.getId(), response.getId());
        assertEquals(milestoneEntity.getTitle(), response.getTitle());
        assertEquals(milestoneEntity.getDescription(), response.getDescription());
        assertEquals(milestoneEntity.getDateAchieve(), response.getDateAchieve());
    }
}