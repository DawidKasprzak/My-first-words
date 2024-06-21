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
    private CreateParentResponse createParentResponse;
    private ParentInfoResponse parentInfoResponse1;
    private ParentInfoResponse parentInfoResponse2;


    @BeforeEach
    void setUp() {
        parentsRepository.deleteAll();

        createParentRequest = new CreateParentRequest();
        createParentRequest.setUsername("testUser");
        createParentRequest.setPassword("testPassword");
        createParentRequest.setMail("test@mail.com");

        createParentResponse = new CreateParentResponse();
        createParentResponse.setUsername("testUser");
        createParentResponse.setMail("test@mail.com");

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

    @Test
    @Transactional
    void when_registerParent_then_parentShouldBePersistedInDatabase() throws Exception {

        mockMvc.perform(post("/api/parents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createParentRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(createParentResponse)));

        ParentEntity parentEntity = parentsRepository.findByUsername("testUser").orElse(null);
        assertNotNull(parentEntity);
        assertEquals("testUser", parentEntity.getUsername());
        assertEquals("test@mail.com", parentEntity.getMail());
    }

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

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getAllRegisterParents_then_allParentsShouldBeReturned() throws Exception {
        List<ParentInfoResponse> parents = Arrays.asList(parentInfoResponse1, parentInfoResponse2);
        GetAllParentsResponse expectResponse = new GetAllParentsResponse(parents);

        mockMvc.perform(get("/api/parents")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectResponse)));
    }


    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getParentsById_then_parentShouldBeReturned() throws Exception {
        Long parentId = parentInfoResponse1.getId();

        mockMvc.perform(get("/api/parents/{parentId}", parentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(parentInfoResponse1)));
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getParentsById_then_throwParentNotFoundException() throws Exception {
        Long nonExistentParentId = 999L;

        mockMvc.perform(get("/api/parents/{parentId}", nonExistentParentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Parent not found with id: 999"));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_deleteParent_then_parentShouldBeRemovedFromDatabase() throws Exception {
        Long parentId = parentInfoResponse1.getId();

        mockMvc.perform(delete("/api/parents/{parentId}", parentId))
                .andExpect(status().isNoContent());

        assertFalse(parentsRepository.findById(parentId).isPresent());
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_deleteNonexistentParent_then_throwParentNotFoundException() throws Exception {
        Long nonExistentParentId = 999L;

        mockMvc.perform(delete("/api/parents/{parentId}", nonExistentParentId))
                .andExpect(status().isNotFound());

    }

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