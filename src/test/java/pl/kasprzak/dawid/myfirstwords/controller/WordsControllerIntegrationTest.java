package pl.kasprzak.dawid.myfirstwords.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordRequest;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordResponse;
import pl.kasprzak.dawid.myfirstwords.model.words.GetAllWordsResponse;
import pl.kasprzak.dawid.myfirstwords.model.words.GetWordResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.WordsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WordsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WordsRepository wordsRepository;
    @Autowired
    private ParentsRepository parentsRepository;
    @Autowired
    private ChildrenRepository childrenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private ParentEntity parentEntity;
    private ChildEntity childEntity;
    private CreateWordRequest createWordRequest;
    private CreateWordResponse createWordResponse;
    private WordEntity wordEntity;
    private List<WordEntity> wordEntities;
    private WordEntity wordEntity1;
    private WordEntity wordEntity2;
    private WordEntity wordEntity3;
    private WordEntity wordEntity4;
    private LocalDate date;

    @BeforeEach
    void setUp() {

        parentEntity = new ParentEntity();
        parentEntity.setUsername("user");
        parentEntity.setPassword(passwordEncoder.encode("password"));
        parentEntity = parentsRepository.save(parentEntity);

        childEntity = new ChildEntity();
        childEntity.setName("childName");
        childEntity.setParent(parentEntity);
        childEntity = childrenRepository.save(childEntity);

        createWordRequest = new CreateWordRequest();
        createWordRequest.setWord("wordTest");
        createWordRequest.setDateAchieve(LocalDate.of(2020, 01, 07));

        createWordResponse = new CreateWordResponse();
        createWordResponse.setWord(createWordRequest.getWord());
        createWordResponse.setDateAchieve(createWordRequest.getDateAchieve());

        date = LocalDate.of(2024, 1, 1);

        wordEntity1 = new WordEntity(1L, "word1", date.minusDays(1), childEntity);
        wordEntity2 = new WordEntity(2L, "word2", date.minusDays(2), childEntity);
        wordEntity3 = new WordEntity(3L, "word3", date.plusDays(1), childEntity);
        wordEntity4 = new WordEntity(4L, "word4", date.plusDays(2), childEntity);

        wordEntities = Arrays.asList(wordEntity1, wordEntity2, wordEntity3, wordEntity4);

        wordsRepository.saveAll(wordEntities);
    }


    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_addWord_then_wordShouldBeAddedToSpecificChild() throws Exception {

        MvcResult result = mockMvc.perform(post("/api/words/{childId}", childEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWordRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        CreateWordResponse response = objectMapper.readValue(jsonResponse, CreateWordResponse.class);

        assertNotNull(response.getId());
        assertEquals(createWordRequest.getWord(), response.getWord());
        assertEquals(createWordRequest.getDateAchieve(), response.getDateAchieve());

        List<WordEntity> words = wordsRepository.findAll();
        assertEquals(5, words.size());
        WordEntity wordEntity = words.get(0);
        assertEquals("word1", wordEntity.getWord());
        assertEquals(LocalDate.of(2023, 12, 31), wordEntity.getDateAchieve());
        assertEquals(childEntity.getId(), wordEntity.getChild().getId());
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_deleteWord_then_wordShouldBeDeletedFromChildAccount() throws Exception {
        wordEntity = new WordEntity();
        wordEntity.setWord(createWordRequest.getWord());
        wordEntity.setChild(childEntity);
        wordEntity = wordsRepository.save(wordEntity);

        mockMvc.perform(delete("/api/words/{childId}/{wordId}", childEntity.getId(), wordEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(wordsRepository.findByChildIdAndId(childEntity.getId(), wordEntity.getId()).isPresent());
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_deleteWordAndWordNotFound_then_throwWordNotFoundException() throws Exception {
        Long nonExistentWordId = 999L;

        mockMvc.perform(delete("/api/words/{childId}/{wordId}", childEntity.getId(), nonExistentWordId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getByDateAchieveBefore_then_wordsShouldBeReturnedBeforeTheGivenDate() throws Exception {

        List<GetWordResponse> expectedResponse = wordEntities.stream()
                .filter(wordEntity -> wordEntity.getDateAchieve().isBefore(date))
                .map(wordEntity -> new GetWordResponse(wordEntity.getId(), wordEntity.getWord(), wordEntity.getDateAchieve()))
                .collect(Collectors.toList());

        mockMvc.perform(get("/api/words/{childId}/before/{date}", childEntity.getId(), date)
                        .param("date", date.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getByDateAchieveAfter_then_wordsShouldBeReturnedAfterTheGivenDate() throws Exception {

        List<GetWordResponse> expectedResponse = wordEntities.stream()
                .filter(wordEntity -> wordEntity.getDateAchieve().isAfter(date))
                .map(wordEntity -> new GetWordResponse(wordEntity.getId(), wordEntity.getWord(), wordEntity.getDateAchieve()))
                .collect(Collectors.toList());

        mockMvc.perform(get("/api/words/{childId}/after/{date}", childEntity.getId(), date)
                        .param("date", date.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getWordsBetweenDays_then_wordsShouldBeReturnedBetweenTheGivenDates() throws Exception {
        LocalDate startDate = date.minusDays(2);
        LocalDate endDate = date.plusDays(2);

        List<GetWordResponse> expectedResponse = wordEntities.stream()
                .filter(wordEntity -> !wordEntity.getDateAchieve().isBefore(startDate) && !wordEntity.getDateAchieve().isAfter(endDate))
                .map(wordEntity -> new GetWordResponse(wordEntity.getId(), wordEntity.getWord(), wordEntity.getDateAchieve()))
                .collect(Collectors.toList());

        mockMvc.perform(get("/api/words/{childId}/between", childEntity.getId())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    //sprawdzić komunikat
    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getWordsBetweenDays__and_startDateIsNull_then_throwDateValidationException() throws Exception {
        LocalDate endDate = date.plusDays(2);

        mockMvc.perform(get("/api/words/{childId}/between", childEntity.getId())
                        .param("endDate", endDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    //sprawdzić komunikat
    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getWordsBetweenDays_and_endDateIsNull_then_throwDateValidationException() throws Exception {
        LocalDate startDate = date.minusDays(2);

        mockMvc.perform(get("/api/words/{childId}/between", childEntity.getId())
                        .param("startDate", startDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getWordsBetweenDays_and_startDateIsAfterEndDate_then_throwInvalidDateOrderException() throws Exception {
        LocalDate startDate = date.plusDays(2);
        LocalDate endDate = date.minusDays(2);

        mockMvc.perform(get("/api/words/{childId}/between", childEntity.getId())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Start date must be before or equal to end date"));
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getAllWords_then_allWordsTheChildShouldBeReturned() throws Exception {
        List<GetWordResponse> wordResponses = wordEntities.stream()
                .map(wordEntity -> new GetWordResponse(wordEntity.getId(), wordEntity.getWord(), wordEntity.getDateAchieve()))
                .collect(Collectors.toList());

        GetAllWordsResponse expectedResponse = new GetAllWordsResponse(wordResponses);

        mockMvc.perform(get("/api/words/{childId}", childEntity.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getWordByChildIdAndWord_then_wordShouldBeReturn() throws Exception {
        String word = "word1";

        mockMvc.perform(get("/api/words/{childId}/word", childEntity.getId())
                        .param("word", word.toLowerCase())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.word").value(word));
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getWordByChildIdAndWord_then_throwWordNotFoundException() throws Exception {
        String word = "nonexistentWord";

        mockMvc.perform(get("/api/words/{childId}/word", childEntity.getId())
                        .param("word", word.toLowerCase())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Word not found"));

    }
}


