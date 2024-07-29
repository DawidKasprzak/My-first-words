package pl.kasprzak.dawid.myfirstwords.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Lombok;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.kasprzak.dawid.myfirstwords.model.children.*;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ChildControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ChildrenRepository childrenRepository;
    @Autowired
    private ParentsRepository parentsRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private ParentEntity parentEntity;
    private ChildEntity childEntity, childEntity2;
    private CreateChildRequest createChildRequest;
    private CreateChildResponse createChildResponse;
    private GetChildResponse getChildResponse, getChildResponse2;


    @BeforeEach
    void setUp() {


        parentEntity = new ParentEntity();
        parentEntity.setUsername("user");
        parentEntity.setPassword(passwordEncoder.encode("password"));
        parentsRepository.save(parentEntity);

        createChildRequest = CreateChildRequest.builder()
                .name("childName")
                .birthDate(LocalDate.of(2024, 01, 22))
                .gender(Gender.GIRL)
                .build();

        childEntity = new ChildEntity();
        childEntity.setId(1L);
        childEntity.setName(createChildRequest.getName());
        childEntity.setBirthDate(createChildRequest.getBirthDate());
        childEntity.setGender(Gender.GIRL);
        childEntity.setParent(parentEntity);
        childrenRepository.save(childEntity);

        childEntity2 = new ChildEntity();
        childEntity2.setId(2L);
        childEntity2.setName("childName2");
        childEntity2.setBirthDate(LocalDate.of(2023, 05, 15));
        childEntity2.setParent(parentEntity);
        childrenRepository.save(childEntity2);

        createChildResponse = CreateChildResponse.builder()
                .name(createChildRequest.getName())
                .birthDate(createChildRequest.getBirthDate())
                .gender(createChildRequest.getGender())
                .build();

        getChildResponse = GetChildResponse.builder()
                .id(childEntity.getId())
                .name(childEntity.getName())
                .birthDate(childEntity.getBirthDate())
                .build();

        getChildResponse2 = GetChildResponse.builder()
                .id(childEntity2.getId())
                .name(childEntity2.getName())
                .birthDate(childEntity2.getBirthDate())
                .build();

    }

    /**
     * Integration test for adding a child to a parent's account.
     * This test verifies that a child is successfully added to the parent's account and that the child
     * details are correctly stored in the database.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_parentAddsAChildToTheAccount_then_childShouldBeReturned() throws Exception {

        mockMvc.perform(post("/api/children")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createChildRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(createChildResponse)));

        ChildEntity savedChild = childrenRepository.findAll().get(0);
        assertEquals("childName", savedChild.getName());
        assertEquals(LocalDate.of(2024, 01, 22), savedChild.getBirthDate());
        assertEquals(Gender.GIRL, savedChild.getGender());
        assertEquals("user", savedChild.getParent().getUsername());
    }

    /**
     * Integration test for adding a child when the parent is not found.
     * This test verifies that if a child is being added and the parent does not exist,
     * the service returns n HTTP 404 Not Found status with the appropriate error message.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @Transactional
    @WithMockUser(username = "nonExistentUser", roles = "USER")
    void when_addChildAndParentNotFound_then_throwParentNotFoundException() throws Exception {
        mockMvc.perform(post("/api/children")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createChildRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Parent not found"));
    }

    /**
     * Integration test for deleting a child from a parent's account.
     * This test verifies that a child is successfully deleted from the parent's account
     * and that the child no longer exist in the database.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_parentDeleteChild_then_childShouldBeDeletedFromParentAccount() throws Exception {
        Long childId = childEntity.getId();

        mockMvc.perform(delete("/api/children/{childId}", childId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(childrenRepository.findById(childId).isPresent());
    }

    /**
     * Integration test for retrieving all children of a parent.
     * This test verifies that all child associated with the authenticated parent are returned
     * and the response content matches the expected data.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getAllChildrenOfParent_then_allChildrenShouldBeReturned() throws Exception {
        List<GetChildResponse> children = Arrays.asList(getChildResponse, getChildResponse2);
        GetAllChildResponse expectedResponse = GetAllChildResponse.builder()
                .children(children)
                .build();

        mockMvc.perform(get("/api/children")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    /**
     * Integration test for retrieving all children when the parent is not found.
     * This test verifies that the service returns an HTTP 404 Not Found status with the appropriate error
     * message when an authenticated parent attempts to retrieve children, but the parent does not exists
     * in the system.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @WithMockUser(username = "nonExistentUser", roles = "USER")
    void when_getAllChildrenOfParentAndParentNotFound_then_throwParentNotFoundException() throws Exception {
        mockMvc.perform(get("/api/children")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Parent not found"));
    }

    /**
     * Integration test for retrieving a child by their ID.
     * This test verifies that the service returns the correct child details for a given child ID.
     * It checks that the response status is HTTP 200 OK and that the returned JSON matches the expected data.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getChildById_then_childWithSpecificIdShouldBeReturned() throws Exception {
        Long childId = childEntity.getId();

        mockMvc.perform(get("/api/children/{childId}", childId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(getChildResponse)));

    }

    /**
     * Integration test for retrieving a child by their ID when the child is not found.
     * This test verifies that the service returns an HTTP 404 Not Found status with the appropriate error
     * message when a child with the given ID does not exist.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getChildByIdAndChildNotFound_then_throwChildNotFoundException() throws Exception {
        Long nonExistentChildId = 999L;

        mockMvc.perform(get("/api/children/{childId}", nonExistentChildId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Child not found"));
    }
}