package pl.kasprzak.dawid.myfirstwords.model.milestones;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetAllMilestoneResponse {

    private List<GetMilestoneResponse> milestones;
}
