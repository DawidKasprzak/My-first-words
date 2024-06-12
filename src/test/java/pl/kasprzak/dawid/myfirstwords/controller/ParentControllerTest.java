package pl.kasprzak.dawid.myfirstwords.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentRequest;
import pl.kasprzak.dawid.myfirstwords.model.parents.CreateParentResponse;
import pl.kasprzak.dawid.myfirstwords.service.parents.CreateParentService;
import pl.kasprzak.dawid.myfirstwords.service.parents.DeleteParentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ParentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CreateParentService createParentService;
    @MockBean
    private DeleteParentService deleteParentService;

    @Test
    void testRegisterParent() throws Exception{
        CreateParentRequest request = new CreateParentRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");
        request.setMail("test@mail.com");
        CreateParentResponse response = new CreateParentResponse();

        Mockito.when(createParentService.saveParent(any(CreateParentRequest.class))).thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();

        String requestJson = objectMapper.writeValueAsString(request);
        String responseJson = objectMapper.writeValueAsString(response);

        ResultActions resultActions = mockMvc.perform(post("/api/parents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        resultActions.andExpect(status().isCreated())
                .andExpect(content().json(responseJson));

        Mockito.verify(createParentService).saveParent(any(CreateParentRequest.class));
    }

    @Test
    void testGetAllRegisterParents() {
    }

    @Test
    void getRegisterParentsById() {
    }

    @Test
    void testDeleteAccount_Success() throws Exception{
        Long parentId = 1L;

        Mockito.doNothing().when(deleteParentService).deleteAccount(anyLong());

        mockMvc.perform(delete("/api/parents/{parentId}", parentId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteAccount_ParentNotFound() throws Exception{
        Long parentId = 1L;

        Mockito.doThrow(new ParentNotFoundException("Parent not found")).when(deleteParentService).deleteAccount(anyLong());

        mockMvc.perform(delete("/api/parents/{parentId}", parentId))
                .andExpect(status().isNotFound());

    }

    @Test
    void changePassword() {
    }
}