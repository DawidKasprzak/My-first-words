package pl.kasprzak.dawid.myfirstwords.model.parents;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
public class CreateParentRequest {
    @NotNull
    private String username;
    @NotNull(message = "Password cannot be empty")
    @Length(min = 5, max = 40, message = "Password must be at least 5 characters long")
    private String password;
    @Email
    private String mail;
}
