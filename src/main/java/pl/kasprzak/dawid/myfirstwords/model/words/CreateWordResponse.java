package pl.kasprzak.dawid.myfirstwords.model.words;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWordResponse {

    private long id;
    private String word;
    private LocalDate dateAchieve;


}
