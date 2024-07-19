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


    @Test
    void when_fromDto_then_returnMilestoneEntity() {
        MilestoneEntity entity = createMilestoneConverter.fromDto(createMilestoneRequest);

        assertEquals(createMilestoneRequest.getTitle(), entity.getTitle());
        assertEquals(createMilestoneRequest.getDescription(), entity.getDescription());
        assertEquals(createMilestoneRequest.getDateAchieve(), entity.getDateAchieve());
    }

    @Test
    void when_toDto_then_returnCreateMilestoneResponse() {
        CreateMilestoneResponse result = createMilestoneConverter.toDto(milestoneEntity);

        assertEquals(milestoneEntity.getId(), result.getId());
        assertEquals(milestoneEntity.getTitle(), result.getTitle());
        assertEquals(milestoneEntity.getDescription(), result.getDescription());
        assertEquals(milestoneEntity.getDateAchieve(), result.getDateAchieve());
    }
}