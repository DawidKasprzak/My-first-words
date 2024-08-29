package pl.kasprzak.dawid.myfirstwords.model.children;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetAllChildResponse {

    private List<GetChildResponse> children;
}
