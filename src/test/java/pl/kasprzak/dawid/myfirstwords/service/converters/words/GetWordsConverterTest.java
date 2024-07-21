package pl.kasprzak.dawid.myfirstwords.service.converters.words;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

        // Initialize WordEntity with test data
        wordEntity = new WordEntity();
        wordEntity.setId(1L);
        wordEntity.setWord("testWord");
        wordEntity.setDateAchieve(LocalDate.now().minusDays(5));
    }

    /**
     * Unit test for calling fromDto method on GetWordsConverter.
     * Verifies that an UnsupportedOperationException is thrown when fromDto is called.
     */
    @Test
    void when_callFromDto_then_throwUnsupportedOperationException() {
        // Assert that UnsupportedOperationException is thrown when calling fromDto with null input
        assertThrows(UnsupportedOperationException.class, () -> getWordsConverter.fromDto(null));
    }

    /**
     * Unit test for the toDto method of GetWordsConverter.
     * Verifies that the GetWordResponse is correctly created from the WordEntity.
     */
    @Test
    void when_toDto_then_returnGetWordResponse() {
        // Convert the entity to response
        GetWordResponse response = getWordsConverter.toDto(wordEntity);

        // Verify the conversion
        assertEquals(wordEntity.getId(), response.getId());
        assertEquals(wordEntity.getWord(), response.getWord());
        assertEquals(wordEntity.getDateAchieve(), response.getDateAchieve());
    }
}