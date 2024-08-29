package pl.kasprzak.dawid.myfirstwords.model.children;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;


@Getter
@Builder
public class GetChildResponse {

    private long id;
    private String name;
    private LocalDate birthDate;
}
