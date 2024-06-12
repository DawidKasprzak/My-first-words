package pl.kasprzak.dawid.myfirstwords.model.milestones;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Data
public class UpdateMilestoneRequest {

    @NotEmpty
    private String title;
    @NotEmpty
    private String description;
    @NotNull
    private LocalDate dateAchieve;
}
