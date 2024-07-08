package pl.kasprzak.dawid.myfirstwords.model.milestones;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
public class CreateMilestoneRequest {

    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private LocalDate dateAchieve;
}
