package pl.kasprzak.dawid.myfirstwords.model.words;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetAllWordsResponse {

    private List<GetWordResponse> words;
}
