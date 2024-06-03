package pl.kasprzak.dawid.myfirstwords.model.words;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CreateWordResponse {

    private long id;
    private String word;
    private LocalDate dateAchieve;


}
