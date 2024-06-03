package pl.kasprzak.dawid.myfirstwords.service.converters.milestones;

import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.model.milestones.GetMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.Convertable;
@Service
public class GetMilestoneConverter implements Convertable<Void, MilestoneEntity, GetMilestoneResponse> {
    @Override
    public MilestoneEntity fromDto(Void input) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetMilestoneResponse toDto(MilestoneEntity milestoneEntity) {
        return GetMilestoneResponse.builder()
                .id(milestoneEntity.getId())
                .title(milestoneEntity.getTitle())
                .description(milestoneEntity.getDescription())
                .dateAchieve(milestoneEntity.getDateAchieve())
                .build();
    }
}
