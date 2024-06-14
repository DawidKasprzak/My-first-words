package pl.kasprzak.dawid.myfirstwords.model.parents;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import pl.kasprzak.dawid.myfirstwords.model.children.GetChildResponse;

import java.util.List;

@Data
@Builder
public class ParentInfoResponse {

    private Long id;
    private String username;
    private String mail;
    private List<GetChildResponse> children;

}
