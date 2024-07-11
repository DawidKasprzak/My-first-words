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
import org.springframework.transaction.annotation.Transactional;
import pl.kasprzak.dawid.myfirstwords.model.milestones.CreateMilestoneRequest;
import pl.kasprzak.dawid.myfirstwords.model.milestones.CreateMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.model.milestones.GetMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

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
class MilestonesControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MilestonesRepository milestonesRepository;
    @Autowired
    private ParentsRepository parentsRepository;
    @Autowired
    private ChildrenRepository childrenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private ParentEntity parentEntity;
    private ChildEntity childEntity;
    private List<MilestoneEntity> milestoneEntities;
    private MilestoneEntity milestoneEntity1, milestoneEntity2, milestoneEntity3, milestoneEntity4;
    private CreateMilestoneRequest createMilestoneRequest;
    private CreateMilestoneResponse createMilestoneResponse;
    private List<GetMilestoneResponse> allMilestoneResponses;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        milestonesRepository.deleteAll();

        parentEntity = new ParentEntity();
        parentEntity.setUsername("user");
        parentEntity.setPassword(passwordEncoder.encode("password"));
        parentEntity = parentsRepository.save(parentEntity);

        childEntity = new ChildEntity();
        childEntity.setName("childName");
        childEntity.setParent(parentEntity);
        childEntity = childrenRepository.save(childEntity);

        createMilestoneRequest = CreateMilestoneRequest.builder()
                .title("milestone title1")
                .description("this is test description")
                .dateAchieve(LocalDate.of(2024, 7, 7))
                .build();

        createMilestoneResponse = CreateMilestoneResponse.builder()
                .title(createMilestoneRequest.getTitle())
                .description(createMilestoneRequest.getDescription())
                .dateAchieve(createMilestoneRequest.getDateAchieve())
                .build();

        date = LocalDate.of(2024, 7, 7);

        milestoneEntity1 = new MilestoneEntity();
        milestoneEntity1.setId(1L);
        milestoneEntity1.setTitle(createMilestoneRequest.getTitle());
        milestoneEntity1.setDateAchieve(createMilestoneRequest.getDateAchieve().minusDays(1));
        milestoneEntity1.setChild(childEntity);

        milestoneEntity2 = new MilestoneEntity();
        milestoneEntity2.setId(2L);
        milestoneEntity2.setTitle("milestone title2");
        milestoneEntity2.setDateAchieve(date.minusDays(2));
        milestoneEntity2.setChild(childEntity);

        milestoneEntity3 = new MilestoneEntity();
        milestoneEntity3.setId(3L);
        milestoneEntity3.setTitle("milestone title3");
        milestoneEntity3.setDateAchieve(date.plusDays(1));
        milestoneEntity3.setChild(childEntity);

        milestoneEntity4 = new MilestoneEntity();
        milestoneEntity4.setId(4L);
        milestoneEntity4.setTitle("milestone title4");
        milestoneEntity4.setDateAchieve(date.plusDays(2));
        milestoneEntity4.setChild(childEntity);

        milestoneEntities = Arrays.asList(milestoneEntity1, milestoneEntity2, milestoneEntity3, milestoneEntity4);

        milestonesRepository.saveAll(milestoneEntities);

        allMilestoneResponses = milestoneEntities.stream()
                .map(milestoneEntity -> GetMilestoneResponse.builder()
                        .id(milestoneEntity.getId())
                        .title(milestoneEntity.getTitle())
                        .dateAchieve(milestoneEntity.getDateAchieve())
                        .build())
                .collect(Collectors.toList());
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_addMilestone_then_milestoneShouldBeAddedToSpecificChild() throws Exception {
        String jsonResponse = mockMvc.perform(post("/api/milestones/{childId}", childEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMilestoneRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CreateMilestoneResponse response = objectMapper.readValue(jsonResponse, CreateMilestoneResponse.class);

        assertNotNull(response.getId());
        assertTrue(response.getTitle().contains(createMilestoneRequest.getTitle()));
        assertEquals(createMilestoneRequest.getDateAchieve(), response.getDateAchieve());

        List<MilestoneEntity> milestones = milestonesRepository.findAll();
        assertEquals(5, milestones.size());
        MilestoneEntity milestoneEntity = milestones.get(0);
        String expectedTitlePart = "title1";
        assertTrue(milestoneEntity.getTitle().contains(expectedTitlePart));
        assertEquals(LocalDate.of(2024, 7, 6), milestoneEntity.getDateAchieve());
        assertEquals(childEntity.getId(), milestoneEntity.getChild().getId());

    }

    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_deleteMilestone_then_milestoneShouldBeDeletedFromChildAccount() throws Exception {
        mockMvc.perform(delete("/api/milestones/{childId}/{milestoneId}", childEntity.getId(), milestoneEntity1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(milestonesRepository.findByChildIdAndId(childEntity.getId(), milestoneEntity1.getId()).isPresent());
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_deleteMilestoneAndMilestoneNotFound_then_throwMilestoneNotFoundException() throws Exception {
        Long nonExistentMilestoneId = 999L;

        mockMvc.perform(delete("/api/milestones/{childId}/{milestoneId}", childEntity.getId(), nonExistentMilestoneId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Milestone not found"));

    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getByDateAchieveBefore_then_milestoneShouldBeReturnedBeforeTheGivenDate() throws Exception {

        List<GetMilestoneResponse> expectedResponse = allMilestoneResponses.stream()
                .filter(getMilestoneResponse -> getMilestoneResponse.getDateAchieve().isBefore(date))
                .collect(Collectors.toList());

        mockMvc.perform(get("/api/milestones/{childId}/before/{date}", childEntity.getId(), date)
                        .param("date", date.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getByDateAchieveAfter_then_milestonesShouldBeReturnedAfterTheGivenDate() throws Exception {

        List<GetMilestoneResponse> expectedResponse = allMilestoneResponses.stream()
                .filter(getMilestoneResponse -> getMilestoneResponse.getDateAchieve().isAfter(date))
                .collect(Collectors.toList());

        mockMvc.perform(get("/api/milestones/{childId}/after/{date}", childEntity.getId(), date)
                        .param("date", date.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getMilestonesBetweenDays_then_milestonesShouldBeReturnedBetweenTheGivenDates() throws Exception {
        LocalDate startDate = date.minusDays(2);
        LocalDate endDate = date.plusDays(2);

        List<GetMilestoneResponse> expectedResponse = allMilestoneResponses.stream()
                .filter(getMilestoneResponse -> !getMilestoneResponse.getDateAchieve().isBefore(startDate) && !getMilestoneResponse.getDateAchieve().isAfter(endDate))
                .collect(Collectors.toList());

        mockMvc.perform(get("/api/milestones/{childId}/between", childEntity.getId())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    void getAllMilestones() {
    }

    @Test
    void getByTitle() {
    }

    @Test
    void updateMilestone() {
    }
}