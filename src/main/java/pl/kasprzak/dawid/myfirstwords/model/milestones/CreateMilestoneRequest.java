package pl.kasprzak.dawid.myfirstwords.model.milestones;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

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
