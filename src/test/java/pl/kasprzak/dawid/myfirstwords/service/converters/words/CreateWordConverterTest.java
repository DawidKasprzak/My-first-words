package pl.kasprzak.dawid.myfirstwords.service.converters.words;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
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

        // Initialize a CreateWordRequest with test data
        createWordRequest = CreateWordRequest.builder()
                .word("testWord")
                .dateAchieve(LocalDate.now().minusDays(10))
                .build();

        // Initialize WordEntity with test data
        wordEntity = new WordEntity();
        wordEntity.setId(1L);
        wordEntity.setWord(createWordRequest.getWord());
        wordEntity.setDateAchieve(createWordRequest.getDateAchieve());
    }

    /**
     * Unit test for the fromDto method of CreateWordConverter.
     * Verifies that the WordEntity is correctly created from the CreateWordRequest.
     */
    @Test
    void when_fromDto_then_returnWordEntity() {
        // Convert the request to entity
        WordEntity entity = createWordConverter.fromDto(createWordRequest);

        // Verify the conversion
        assertEquals(createWordRequest.getWord(), entity.getWord());
        assertEquals(createWordRequest.getDateAchieve(), entity.getDateAchieve());
    }

    /**
     * Unit test for the toDto method of CreateWordConverter.
     * Verifies that the CreateWordResponse is correctly created from the WordEntity.
     */
    @Test
    void when_toDto_then_returnCreateWordResponse() {
        // Convert the entity to response
        CreateWordResponse response = createWordConverter.toDto(wordEntity);

        // Verify the conversion
        assertEquals(wordEntity.getId(), response.getId());
        assertEquals(wordEntity.getWord(), response.getWord());
        assertEquals(wordEntity.getDateAchieve(), response.getDateAchieve());
    }
}