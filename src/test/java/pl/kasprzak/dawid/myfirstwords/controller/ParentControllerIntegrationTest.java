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
import org.springframework.transaction.annotation.Transactional;
import pl.kasprzak.dawid.myfirstwords.model.parents.*;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class ParentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ParentsRepository parentsRepository;
    private CreateParentRequest createParentRequest;
    private ParentInfoResponse parentInfoResponse1, parentInfoResponse2;


    @BeforeEach
    void setUp() {
        parentsRepository.deleteAll();

        createParentRequest = CreateParentRequest.builder()
                .username("testUser")
                .password("testPassword")
                .mail("test@mail.com")
                .build();

        ParentEntity parent1 = new ParentEntity();
        parent1.setUsername("parent1");
        parent1.setMail("parent1@mail.com");
        parent1.setPassword(passwordEncoder.encode("password1"));
        parent1 = parentsRepository.save(parent1);

        ParentEntity parent2 = new ParentEntity();
        parent2.setUsername("parent2");
        parent2.setMail("parent2@mail.com");
        parent2.setPassword(passwordEncoder.encode("password2"));
        parent2 = parentsRepository.save(parent2);

        parentInfoResponse1 = ParentInfoResponse.builder()
                .id(parent1.getId())
                .username(parent1.getUsername())
                .mail(parent1.getMail())
                .children(Collections.emptyList())
                .build();
        parentInfoResponse2 = ParentInfoResponse.builder()
                .id(parent2.getId())
                .username(parent2.getUsername())
                .mail(parent2.getMail())
                .children(Collections.emptyList())
                .build();

    }

    /**
     * Integration test for registering a new Parent.
     * This test verifies that a new parent is correctly persisted in the database when a valid
     * registration request is made. It checks the HTTP response status, response content, and verifies that
     * the parent data is correctly saved in the database.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @Transactional
    void when_registerParent_then_parentShouldBePersistedInDatabase() throws Exception {

        mockMvc.perform(post("/api/parents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createParentRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"username\":\"testUser\", \"mail\": \"test@mail.com\"}", false));

        ParentEntity parentEntity = parentsRepository.findByUsername("testUser").orElse(null);
        assertNotNull(parentEntity);
        assertEquals("testUser", parentEntity.getUsername());
        assertEquals("test@mail.com", parentEntity.getMail());
    }

    /**
     * Integration test for registering a new parent with an existing username.
     * This test verifies that if a registration request is made with a username that already exist,
     * the service returns an HTTP 409 Conflict status and the appropriate error message.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @Transactional
    void when_usernameAlreadyExists_then_throwUsernameAlreadyExistsException() throws Exception {
        createParentRequest.setUsername("parent1");

        mockMvc.perform(post("/api/parents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createParentRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Username already exists: parent1"));
    }

    /**
     * Integration test for registering a new parent with an existing email.
     * This test verifies that if a registration request is made with an email that is already associated
     * with another account, the service returns an HTTP 409 Conflict status and the appropriate error message.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @Transactional
    void when_emailAlreadyExists_then_throwEmailAlreadyExistsException() throws Exception {
        createParentRequest.setUsername("usernameTest");
        createParentRequest.setMail("parent1@mail.com");

        mockMvc.perform(post("/api/parents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createParentRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already exists: parent1@mail.com"));
    }

    /**
     * Integration test for retrieving all registered parents.
     * This test verifies that the service returns a list of all parents when the request is made.
     * Note: Currently, this endpoint is accessible by regular users, but it is planned to restrict
     * this functionality to administrators only. This will be updated in future implementation.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getAllRegisterParents_then_allParentsShouldBeReturned() throws Exception {
        List<ParentInfoResponse> parents = Arrays.asList(parentInfoResponse1, parentInfoResponse2);
        GetAllParentsResponse expectResponse = GetAllParentsResponse.builder()
                .parents(parents)
                .build();

        mockMvc.perform(get("/api/parents")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectResponse)));
    }

    /**
     * Integration test for retrieving a parent by their ID.
     * This test verifies that the service returns the correct parent details when a valid parent ID
     * is provided in the request. It checks the HTTP response status and content.
     * Note: Currently, this endpoint is accessible by regular users, but it is planned to restrict
     * this functionality to administrators only. This will be updated in future implementation.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getParentsById_then_parentShouldBeReturned() throws Exception {
        Long parentId = parentInfoResponse1.getId();

        mockMvc.perform(get("/api/parents/{parentId}", parentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(parentInfoResponse1)));
    }

    /**
     * Integration test for the scenario where a parent is not found by ID.
     * This test verifies that the service returns a 404 Not Found status when a request is made
     * for a parent ID that does not exist.
     * Note: Currently, this endpoint is accessible by regular users, but it is planned to restrict
     * this functionality to administrators only. This will be updated in future implementation.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getParentsById_then_throwParentNotFoundException() throws Exception {
        Long nonExistentParentId = 999L;

        mockMvc.perform(get("/api/parents/{parentId}", nonExistentParentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Parent not found with id: 999"));
    }

    /**
     * Integration test for deleting a parent account.
     * This test verifies that a parent account is removed from the database when a valid delete request is made.
     * It checks the response status and ensures the parent record is deleted.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_deleteParent_then_parentShouldBeRemovedFromDatabase() throws Exception {
        Long parentId = parentInfoResponse1.getId();

        mockMvc.perform(delete("/api/parents/{parentId}", parentId))
                .andExpect(status().isNoContent());

        assertFalse(parentsRepository.findById(parentId).isPresent());
    }

    /**
     * Integration test for attempting to delete a nonexistent parent account.
     * This test verifies that the service returns a 404 Not Found status when attempting to delete a parent
     * with an ID that does not exist.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_deleteNonexistentParent_then_throwParentNotFoundException() throws Exception {
        Long nonExistentParentId = 999L;

        mockMvc.perform(delete("/api/parents/{parentId}", nonExistentParentId))
                .andExpect(status().isNotFound());

    }

    /**
     * Integration test for changing a parent's password.
     * This test verifies that a parent's password is updated in the database when a valid password change request is made.
     * It checks the response status and validates that the new password is correctly hashed and stored.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_changePassword_then_passwordShouldBeChanged() throws Exception {
        Long parentId = parentInfoResponse1.getId();
        String newPassword = "newPassword";

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setPassword(newPassword);

        mockMvc.perform(put("/api/parents/" + parentId + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        ParentEntity updateParent = parentsRepository.findById(parentId).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, updateParent.getPassword()));
    }

    /**
     * Integration test for changing the password of a nonexistent parent account.
     * This test verifies that the service returns a 404 Not Found status when attempting to change the password
     * for a parent with an ID that does not exist.
     *
     * @throws Exception if an error occurs during the request or response processing.
     */
    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_changePasswordForNonexistentParent_then_throwParentNotFoundException() throws Exception {
        Long nonExistentParentId = 999L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setPassword("newPassword");

        mockMvc.perform(put("/api/parents/" + nonExistentParentId + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}