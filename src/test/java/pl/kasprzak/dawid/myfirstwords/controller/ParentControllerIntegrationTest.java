package pl.kasprzak.dawid.myfirstwords.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.parents.*;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.service.parents.CreateParentService;
import pl.kasprzak.dawid.myfirstwords.service.parents.DeleteParentService;
import pl.kasprzak.dawid.myfirstwords.service.parents.GetParentService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ParentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ParentsRepository parentsRepository;
    @MockBean
    private CreateParentService createParentService;
    @MockBean
    private DeleteParentService deleteParentService;
    @MockBean
    private GetParentService getParentService;
    private ParentInfoResponse parentInfoResponse1;
    private ParentInfoResponse parentInfoResponse2;


    @BeforeEach
    void setUp() {
        parentInfoResponse1 = ParentInfoResponse.builder()
                .id(1L)
                .username("parent1")
                .mail("parent1@mail.com")
                .children(Collections.emptyList())
                .build();

        parentInfoResponse2 = ParentInfoResponse.builder()
                .id(2L)
                .username("parent2")
                .mail("parent2@mail.com")
                .children(Collections.emptyList())
                .build();

    }

    @Test
    @Transactional
    void when_registerParent_then_parentShouldBePersistedInDatabase() throws Exception {
        CreateParentRequest request = new CreateParentRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");
        request.setMail("test@mail.com");
        CreateParentResponse response = new CreateParentResponse();

        when(createParentService.saveParent(any(CreateParentRequest.class))).thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);
        String responseJson = objectMapper.writeValueAsString(response);

        ResultActions resultActions = mockMvc.perform(post("/api/parents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        resultActions.andExpect(status().isCreated())
                .andExpect(content().json(responseJson));

        verify(createParentService).saveParent(any(CreateParentRequest.class));
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getAllRegisterParents_then_allParentsShouldBeReturned() throws Exception {
        List<ParentInfoResponse> parents = Arrays.asList(parentInfoResponse1, parentInfoResponse2);
        GetAllParentsResponse response = new GetAllParentsResponse(parents);

        when(getParentService.getAll()).thenReturn(response);


        mockMvc.perform(get("/api/parents")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }


    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getParentsById_then_parentShouldBeReturned() throws Exception {
        Long parentId = 1L;

        when(getParentService.getById(parentId)).thenReturn(parentInfoResponse1);


        mockMvc.perform(get("/api/parents/{parentId}", parentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(parentInfoResponse1)));
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_getParentsById_then_throwParentNotFoundException() throws Exception {
        Long parentId = 1L;

        when(getParentService.getById(parentId)).thenThrow(new ParentNotFoundException("Parent not found with id:" + parentId));


        String expectedResponse = "Parent not found with id:" + parentId;

        mockMvc.perform(get("/api/parents/{parentId}", parentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_deleteParent_then_parentShouldBeRemovedFromDatabase() throws Exception {
        Long parentId = 1L;

        Mockito.doNothing().when(deleteParentService).deleteAccount(anyLong());

        mockMvc.perform(delete("/api/parents/{parentId}", parentId))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_deleteNonexistentParent_then_throwParentNotFoundException() throws Exception {
        Long parentId = 1L;

        Mockito.doThrow(new ParentNotFoundException("Parent not found")).when(deleteParentService).deleteAccount(anyLong());

        mockMvc.perform(delete("/api/parents/{parentId}", parentId))
                .andExpect(status().isNotFound());

    }

    @Test
    @Transactional
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsServiceForTest")
    void when_changePassword_then_passwordShouldBeChanged() throws Exception {
        Long parentId = 1L;
        String newPassword = "newPassword";
        ParentEntity parent = new ParentEntity();
        parent.setId(parentId);
        parent.setPassword(passwordEncoder.encode("oldPassword"));

        parentsRepository.save(parent);

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
    void when_changePasswordForNonexistentParent_then_throwParentNotFoundException() throws Exception{
        Long parentId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setPassword("newPassword");

        mockMvc.perform(put("/api/parents/" + parentId + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}