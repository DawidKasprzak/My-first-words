package pl.kasprzak.dawid.myfirstwords.service.converters.milestones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.model.milestones.UpdateMilestoneRequest;
import pl.kasprzak.dawid.myfirstwords.model.milestones.UpdateMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UpdateMilestonesConverterTest {

    @InjectMocks
    private UpdateMilestonesConverter updateMilestonesConverter;
    private UpdateMilestoneRequest updateMilestoneRequest;
    private MilestoneEntity milestoneEntity;

    @BeforeEach
    void setUp() {
        // Initialize UpdateMilestoneRequest with test data
        updateMilestoneRequest = UpdateMilestoneRequest.builder()
                .title("update milestone title")
                .description("update milestone description")
                .dateAchieve(LocalDate.now().minusDays(5))
                .build();

        // Initialize MilestoneEntity with test data
        milestoneEntity = new MilestoneEntity();
        milestoneEntity.setId(1L);
        milestoneEntity.setTitle(updateMilestoneRequest.getTitle());
        milestoneEntity.setDescription(updateMilestoneRequest.getDescription());
        milestoneEntity.setDateAchieve(updateMilestoneRequest.getDateAchieve());
    }

    /**
     * Unit test for converting UpdateMilestoneRequest to MilestoneEntity.
     * Verifies that the MilestoneEntity is correctly created from the UpdateMilestoneRequest.
     */
    @Test
    void when_fromDto_then_returnMilestoneEntity() {
        // Convert the request to entity
        MilestoneEntity entity = updateMilestonesConverter.fromDto(updateMilestoneRequest);

        // Verify the conversion
        assertEquals(updateMilestoneRequest.getTitle(), entity.getTitle());
        assertEquals(updateMilestoneRequest.getDescription(), entity.getDescription());
        assertEquals(updateMilestoneRequest.getDateAchieve(), entity.getDateAchieve());
    }

    /**
     * Unit test for converting MilestoneEntity to UpdateMilestoneResponse.
     * Verifies that the UpdateMilestoneResponse is correctly created from MilestoneEntity.
     */
    @Test
    void when_toDto_then_returnUpdateMilestoneResponse() {
        // Convert the entity to response
        UpdateMilestoneResponse response = updateMilestonesConverter.toDto(milestoneEntity);

        // Verify the conversion
        assertEquals(milestoneEntity.getId(), response.getId());
        assertEquals(milestoneEntity.getTitle(), response.getTitle());
        assertEquals(milestoneEntity.getDescription(), response.getDescription());
        assertEquals(milestoneEntity.getDateAchieve(), response.getDateAchieve());
    }
}