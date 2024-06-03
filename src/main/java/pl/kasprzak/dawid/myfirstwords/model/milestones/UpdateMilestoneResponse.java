package pl.kasprzak.dawid.myfirstwords.model.milestones;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UpdateMilestoneResponse {

    private long id;
    private String title;
    private String description;
    private LocalDate dateAchieve;

}
