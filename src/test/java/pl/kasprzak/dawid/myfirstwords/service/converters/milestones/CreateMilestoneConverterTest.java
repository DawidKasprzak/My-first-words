package pl.kasprzak.dawid.myfirstwords.service.converters.milestones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.model.milestones.CreateMilestoneRequest;
import pl.kasprzak.dawid.myfirstwords.model.milestones.CreateMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CreateMilestoneConverterTest {

    @InjectMocks
    private CreateMilestoneConverter createMilestoneConverter;
    private CreateMilestoneRequest createMilestoneRequest;
    private MilestoneEntity milestoneEntity;

    @BeforeEach
    void setUp() {

        createMilestoneRequest = CreateMilestoneRequest.builder()
                .title("testTitle")
                .description("example description")
                .dateAchieve(LocalDate.now().minusDays(15))
                .build();

        milestoneEntity = new MilestoneEntity();
        milestoneEntity.setId(1L);
        milestoneEntity.setTitle(createMilestoneRequest.getTitle());
        milestoneEntity.setDescription(createMilestoneRequest.getDescription());
        milestoneEntity.setDateAchieve(createMilestoneRequest.getDateAchieve());
    }

    /**
     * Unit test for the fromDto method of CreateMilestoneConverter.
     * Verifies that the MilestoneEntity is correctly created from the CreateMilestoneRequest.
     */
    @Test
    void when_fromDto_then_returnMilestoneEntity() {
        MilestoneEntity entity = createMilestoneConverter.fromDto(createMilestoneRequest);

        assertEquals(createMilestoneRequest.getTitle(), entity.getTitle());
        assertEquals(createMilestoneRequest.getDescription(), entity.getDescription());
        assertEquals(createMilestoneRequest.getDateAchieve(), entity.getDateAchieve());
    }

    /**
     * Unit test for the toDto method of CreateMilestoneConverter.
     * Verifies that the CreateMilestoneResponse is correctly created from the MilestoneEntity.
     */
    @Test
    void when_toDto_then_returnCreateMilestoneResponse() {
        CreateMilestoneResponse response = createMilestoneConverter.toDto(milestoneEntity);

        assertEquals(milestoneEntity.getId(), response.getId());
        assertEquals(milestoneEntity.getTitle(), response.getTitle());
        assertEquals(milestoneEntity.getDescription(), response.getDescription());
        assertEquals(milestoneEntity.getDateAchieve(), response.getDateAchieve());
    }
}