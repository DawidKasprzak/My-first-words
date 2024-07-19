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

        // Initialize WordEntity with test data
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
        // Assert that UnsupportedOperationException is thrown when calling fromDto with null input
        assertThrows(UnsupportedOperationException.class, () -> getMilestoneConverter.fromDto(null));
    }

    /**
     * Unit test for converting MilestoneEntity to GetMilestoneResponse.
     * Verifies that the GetMilestoneResponse is correctly created from MilestoneEntity.
     */
    @Test
    void when_toDto_then_returnGetMilestoneResponse() {
        // Convert the entity to response
        GetMilestoneResponse response = getMilestoneConverter.toDto(milestoneEntity);

        // Verify the conversion
        assertEquals(milestoneEntity.getId(), response.getId());
        assertEquals(milestoneEntity.getTitle(), response.getTitle());
        assertEquals(milestoneEntity.getDescription(), response.getDescription());
        assertEquals(milestoneEntity.getDateAchieve(), response.getDateAchieve());
    }
}