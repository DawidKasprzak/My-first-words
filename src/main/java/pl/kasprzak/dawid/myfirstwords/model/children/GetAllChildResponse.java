package pl.kasprzak.dawid.myfirstwords.model.children;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
public class GetAllChildResponse {

    private List<GetChildResponse> children;
}
