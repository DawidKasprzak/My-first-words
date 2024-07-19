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
        updateMilestoneRequest = UpdateMilestoneRequest.builder()
                .title("update milestone title")
                .description("update milestone description")
                .dateAchieve(LocalDate.now().minusDays(5))
                .build();

        milestoneEntity = new MilestoneEntity();
        milestoneEntity.setId(1L);
        milestoneEntity.setTitle(updateMilestoneRequest.getTitle());
        milestoneEntity.setDescription(updateMilestoneRequest.getDescription());
        milestoneEntity.setDateAchieve(updateMilestoneRequest.getDateAchieve());
    }

    @Test
    void when_fromDto_then_returnMilestoneEntity() {
        MilestoneEntity entity = updateMilestonesConverter.fromDto(updateMilestoneRequest);

        assertEquals(updateMilestoneRequest.getTitle(), entity.getTitle());
        assertEquals(updateMilestoneRequest.getDescription(), entity.getDescription());
        assertEquals(updateMilestoneRequest.getDateAchieve(), entity.getDateAchieve());
    }

    @Test
    void when_toDto_then_returnUpdateMilestoneResponse() {
        UpdateMilestoneResponse result = updateMilestonesConverter.toDto(milestoneEntity);

        assertEquals(milestoneEntity.getId(), result.getId());
        assertEquals(milestoneEntity.getTitle(), result.getTitle());
        assertEquals(milestoneEntity.getDescription(), result.getDescription());
        assertEquals(milestoneEntity.getDateAchieve(), result.getDateAchieve());
    }
}