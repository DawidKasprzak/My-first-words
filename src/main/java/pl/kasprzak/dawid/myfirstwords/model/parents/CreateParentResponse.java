package pl.kasprzak.dawid.myfirstwords.model.parents;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
@Builder
public class CreateParentResponse {

    private String username;
    private String mail;
}
