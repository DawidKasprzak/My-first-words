package pl.kasprzak.dawid.myfirstwords.model.parents;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotEmpty
    private String password;
}
