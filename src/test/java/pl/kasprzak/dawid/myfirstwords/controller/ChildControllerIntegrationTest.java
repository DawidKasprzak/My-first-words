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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
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
    private CreateChildRequest createChildRequest;
    private CreateChildResponse createChildResponse;
    private GetChildResponse getChildResponse2;


    @BeforeEach
    void setUp(){
        childrenRepository.deleteAll();
        parentsRepository.deleteAll();

        parentEntity = new ParentEntity();
        parentEntity.setUsername("user");
        parentEntity.setPassword(passwordEncoder.encode("password"));
        parentsRepository.save(parentEntity);

        createChildRequest = new CreateChildRequest();
        createChildRequest.setName("childName");
        createChildRequest.setBirthDate(LocalDate.of(2024,01,22));
        createChildRequest.setGender(Gender.GIRL);

        createChildResponse = CreateChildResponse.builder()
                .name(createChildRequest.getName())
                .birthDate(createChildRequest.getBirthDate())
                .gender(createChildRequest.getGender())
                .build();

    }

    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_addChild_then_returnCreateChildResponse() throws Exception {

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

    @Test
    @Transactional
    @WithMockUser(username = "nonExistentUser", roles = "USER")
    void when_parentNotFound_then_throwParentNotFoundException() throws Exception{
        mockMvc.perform(post("/api/children")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createChildRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Parent not found"));
    }


    @Test
    void deleteChild() {
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getAllChildrenOfParent_then_allChildrenShouldBeReturned() throws Exception {

    }

    @Test
    void getChildById() {
    }
}