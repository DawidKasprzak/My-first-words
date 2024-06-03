package pl.kasprzak.dawid.myfirstwords.model.parents;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateParentRequest {
    @NotNull
    private String username;
    @NotNull(message = "Hasło nie może być puste")
    @Length(min = 5, max = 40, message = "Hasło musi mieć minimalnie 5 znaków")
    private String password;
    @NotNull
    private String mail;
}
