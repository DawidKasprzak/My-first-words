package pl.kasprzak.dawid.myfirstwords.model.children;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChildRequest {

    @NotEmpty
    @Length(min = 2, max = 40)
    private String name;
    @NotEmpty
    @Past
    private LocalDate birthDate;
    private Gender gender;
}
