package pl.kasprzak.dawid.myfirstwords.model.words;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GetAllWordsResponse {

    private List<GetWordResponse> words;
}
