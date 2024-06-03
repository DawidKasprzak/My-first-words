package pl.kasprzak.dawid.myfirstwords.service.converters.milestones;

import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.model.milestones.CreateMilestoneRequest;
import pl.kasprzak.dawid.myfirstwords.model.milestones.CreateMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.Convertable;
@Service
public class CreateMilestoneConverter implements Convertable<CreateMilestoneRequest, MilestoneEntity, CreateMilestoneResponse> {
    @Override
    public MilestoneEntity fromDto(CreateMilestoneRequest input) {
        MilestoneEntity milestoneEntity = new MilestoneEntity();
        milestoneEntity.setTitle(input.getTitle());
        milestoneEntity.setDescription(input.getDescription());
        milestoneEntity.setDateAchieve(input.getDateAchieve());
        return milestoneEntity;
    }

    @Override
    public CreateMilestoneResponse toDto(MilestoneEntity milestoneEntity) {
        return CreateMilestoneResponse.builder()
                .id(milestoneEntity.getId())
                .title(milestoneEntity.getTitle())
                .description(milestoneEntity.getDescription())
                .dateAchieve(milestoneEntity.getDateAchieve())
                .build();
    }
}
