package pl.kasprzak.dawid.myfirstwords.model.milestones;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMilestoneResponse {
    private long id;
    private String title;
    private String description;
    private LocalDate dateAchieve;

}
