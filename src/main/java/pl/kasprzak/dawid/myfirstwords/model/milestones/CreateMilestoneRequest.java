package pl.kasprzak.dawid.myfirstwords.model.milestones;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
public class CreateMilestoneRequest {

    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private LocalDate dateAchieve;
}
