package pl.kasprzak.dawid.myfirstwords.model.milestones;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class GetAllMilestoneResponse {

    private List<GetMilestoneResponse> milestones;
}
