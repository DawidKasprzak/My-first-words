package pl.kasprzak.dawid.myfirstwords.model.words;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
public class CreateWordRequest {
    @NotBlank
    @Length(min = 2, max = 20)
    private String word;
    @NotEmpty
    private LocalDate dateAchieve;

}
