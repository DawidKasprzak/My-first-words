package pl.kasprzak.dawid.myfirstwords.model.children;

import lombok.Builder;
import lombok.Getter;
import pl.kasprzak.dawid.myfirstwords.model.words.GetAllWordsResponse;
import pl.kasprzak.dawid.myfirstwords.model.words.GetWordResponse;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class GetChildResponse {

    private long id;
    private String name;
    private LocalDate birthDate;
    private List<GetWordResponse> allWords;
}
