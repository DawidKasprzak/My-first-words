package pl.kasprzak.dawid.myfirstwords.service.converters.words;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordRequest;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CreateWordConverterTest {

    @InjectMocks
    private CreateWordConverter createWordConverter;
    private CreateWordRequest createWordRequest;
    private WordEntity wordEntity;

    @BeforeEach
    void setUp() {

        createWordRequest = CreateWordRequest.builder()
                .word("testWord")
                .dateAchieve(LocalDate.now().minusDays(10))
                .build();

        wordEntity = new WordEntity();
        wordEntity.setId(1L);
        wordEntity.setWord(createWordRequest.getWord());
        wordEntity.setDateAchieve(createWordRequest.getDateAchieve());
    }

    @Test
    void when_fromDto_then_returnWordEntity() {
        WordEntity entity = createWordConverter.fromDto(createWordRequest);

        assertEquals(createWordRequest.getWord(), entity.getWord());
        assertEquals(createWordRequest.getDateAchieve(), entity.getDateAchieve());
    }

    @Test
    void when_toDto_then_returnCreateWordResponse() {
        CreateWordResponse result = createWordConverter.toDto(wordEntity);

        assertEquals(wordEntity.getId(), result.getId());
        assertEquals(wordEntity.getWord(), result.getWord());
        assertEquals(wordEntity.getDateAchieve(), result.getDateAchieve());
    }
}