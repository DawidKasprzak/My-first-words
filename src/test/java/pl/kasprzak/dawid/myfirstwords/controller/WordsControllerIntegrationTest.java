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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordRequest;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordResponse;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
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
    private List<GetWordResponse> expectedResponse;
    private LocalDate date;

    @BeforeEach
    void setUp() {

        wordsRepository.deleteAll();

        wordsRepository.deleteAll();
        childrenRepository.deleteAll();
        parentsRepository.deleteAll();

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

        wordEntities = Arrays.asList(
                new WordEntity(1L, "word1", date.minusDays(1), childEntity),
                new WordEntity(2L, "word2", date.minusDays(2), childEntity),
                new WordEntity(3L, "word3", date.plusDays(1), childEntity),
                new WordEntity(4L, "word4", date.plusDays(2), childEntity)
        );

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
        assertEquals(1, words.size());
        WordEntity wordEntity = words.get(0);
        assertEquals("wordTest", wordEntity.getWord());
        assertEquals(LocalDate.of(2020, 01, 07), wordEntity.getDateAchieve());
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

        expectedResponse = wordEntities.stream()
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

        expectedResponse = wordEntities.stream()
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


}


