package pl.kasprzak.dawid.myfirstwords.model.milestones;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@Builder
public class UpdateMilestoneRequest {

    @NotEmpty
    private String title;
    @NotEmpty
    private String description;
    @NotNull
    private LocalDate dateAchieve;
}
