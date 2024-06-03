package pl.kasprzak.dawid.myfirstwords.model.words;

import lombok.Builder;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
@Getter
@Builder
public class GetWordResponse {

    private long id;
    private String word;
    private LocalDate dateAchieve;

}
