package pl.kasprzak.dawid.myfirstwords.service.converters.words;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.model.words.GetWordResponse;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class GetWordsConverterTest {

    @InjectMocks
    private GetWordsConverter getWordsConverter;
    private WordEntity wordEntity;

    @BeforeEach
    void setUp(){

        wordEntity = new WordEntity();
        wordEntity.setId(1L);
        wordEntity.setWord("testWord");
        wordEntity.setDateAchieve(LocalDate.now().minusDays(5));
    }

    @Test
    void when_callFromDto_then_throwUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> getWordsConverter.fromDto(null));
    }

    @Test
    void when_toDto_then_returnGetWordResponse() {
        GetWordResponse result = getWordsConverter.toDto(wordEntity);

        assertEquals(wordEntity.getId(), result.getId());
        assertEquals(wordEntity.getWord(), result.getWord());
        assertEquals(wordEntity.getDateAchieve(), result.getDateAchieve());
    }
}