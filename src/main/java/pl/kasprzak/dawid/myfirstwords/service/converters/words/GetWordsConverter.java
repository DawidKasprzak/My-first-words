package pl.kasprzak.dawid.myfirstwords.service.converters.words;

import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.model.words.GetWordResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.Convertable;

import java.util.List;

@Service
public class GetWordsConverter implements Convertable<Void, WordEntity, GetWordResponse> {
    @Override
    public WordEntity fromDto(Void input) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetWordResponse toDto(WordEntity wordEntity) {
        return GetWordResponse.builder()
                .word(wordEntity.getWord())
                .dateAchieve(wordEntity.getDateAchieve())
                .build();

    }
}
