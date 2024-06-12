package pl.kasprzak.dawid.myfirstwords.service.converters.milestones;

import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.model.milestones.UpdateMilestoneRequest;
import pl.kasprzak.dawid.myfirstwords.model.milestones.UpdateMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.Convertable;

@Service
public class UpdateMilestonesConverter implements Convertable<UpdateMilestoneRequest, MilestoneEntity, UpdateMilestoneResponse> {
    @Override
    public MilestoneEntity fromDto(UpdateMilestoneRequest input) {
        MilestoneEntity updateMilestone = new MilestoneEntity();
        updateMilestone.setTitle(input.getTitle());
        updateMilestone.setDescription(input.getDescription());
        updateMilestone.setDateAchieve(input.getDateAchieve());
        return updateMilestone;
    }

    @Override
    public UpdateMilestoneResponse toDto(MilestoneEntity milestoneEntity) {
        return UpdateMilestoneResponse.builder()
                .id(milestoneEntity.getId())
                .title(milestoneEntity.getTitle())
                .description(milestoneEntity.getDescription())
                .dateAchieve(milestoneEntity.getDateAchieve())
                .build();
    }
}
