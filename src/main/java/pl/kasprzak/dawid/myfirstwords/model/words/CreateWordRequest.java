package pl.kasprzak.dawid.myfirstwords.model.words;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@Builder
public class CreateWordRequest {
    @NotBlank
    @Length(min = 2, max = 20)
    private String word;
    @NotNull
    private LocalDate dateAchieve;

}
