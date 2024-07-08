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
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

import java.time.LocalDate;
import java.util.List;


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
    private MilestoneEntity milestoneEntity;
    private MilestoneEntity milestoneEntity2;
    private MilestoneEntity milestoneEntity3;
    private MilestoneEntity milestoneEntity4;
    private CreateMilestoneRequest createMilestoneRequest;
    private CreateMilestoneResponse createMilestoneResponse;
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

        createMilestoneRequest = new CreateMilestoneRequest();
        createMilestoneRequest.setTitle("this is example title");
        createMilestoneRequest.setDescription("this is test description");
        createMilestoneRequest.setDateAchieve(LocalDate.of(2024, 5, 5));

        createMilestoneResponse = new CreateMilestoneResponse();
        createMilestoneResponse.setTitle(createMilestoneRequest.getTitle());
        createMilestoneResponse.setDescription(createMilestoneRequest.getDescription());
        createMilestoneResponse.setDateAchieve(createMilestoneRequest.getDateAchieve());

        milestoneEntity = new MilestoneEntity();
        milestoneEntity.setTitle(createMilestoneRequest.getTitle());
        milestoneEntity.setDescription(createMilestoneRequest.getDescription());
        milestoneEntity.setDateAchieve(createMilestoneRequest.getDateAchieve());
        milestoneEntity.setChild(childEntity);
        milestoneEntity = milestonesRepository.save(milestoneEntity);




        date = LocalDate.of(2024, 7, 7);

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
        assertEquals(2, milestones.size());
        MilestoneEntity milestoneEntity = milestones.get(0);
        String expectedTitlePart = "example";
        assertTrue(milestoneEntity.getTitle().contains(expectedTitlePart));
        assertEquals(LocalDate.of(2024, 5, 5), milestoneEntity.getDateAchieve());
        assertEquals(childEntity.getId(), milestoneEntity.getChild().getId());

    }

    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_deleteMilestone_then_milestoneShouldBeDeletedFromChildAccount() throws Exception {
        mockMvc.perform(delete("/api/milestones/{childId}/{milestoneId}", childEntity.getId(), milestoneEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(milestonesRepository.findByChildIdAndId(childEntity.getId(), milestoneEntity.getId()).isPresent());
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
    void getByDateAchieveBefore() {
    }

    @Test
    void getByDateAchieveAfter() {
    }

    @Test
    void getMilestoneBetweenDays() {
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