package pl.kasprzak.dawid.myfirstwords.model.parents;

import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateParentResponse {

    private Long id;
    private String username;
    private String mail;
}
