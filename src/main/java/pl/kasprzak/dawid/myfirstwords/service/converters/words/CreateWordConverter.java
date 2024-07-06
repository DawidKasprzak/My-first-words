package pl.kasprzak.dawid.myfirstwords.service.converters.words;

import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordRequest;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.Convertable;

@Service
public class CreateWordConverter implements Convertable<CreateWordRequest, WordEntity, CreateWordResponse> {
    @Override
    public WordEntity fromDto(CreateWordRequest input) {
        WordEntity wordEntity = new WordEntity();
        wordEntity.setWord(input.getWord());
        wordEntity.setDateAchieve(input.getDateAchieve());
        return wordEntity;
    }

    @Override
    public CreateWordResponse toDto(WordEntity wordEntity) {
        return CreateWordResponse.builder()
                .id(wordEntity.getId())
                .word(wordEntity.getWord())
                .dateAchieve(wordEntity.getDateAchieve())
                .build();
    }
}
