package pl.kasprzak.dawid.myfirstwords.model.parents;

import lombok.*;

@Getter
@Setter
@Builder
public class CreateParentResponse {

    private Long id;
    private String username;
    private String mail;
}
