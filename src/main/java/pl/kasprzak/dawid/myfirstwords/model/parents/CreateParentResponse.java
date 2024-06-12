package pl.kasprzak.dawid.myfirstwords.model.parents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateParentResponse {

    private String username;
    private String mail;
}
