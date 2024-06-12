package pl.kasprzak.dawid.myfirstwords.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.kasprzak.dawid.myfirstwords.model.parents.*;
import pl.kasprzak.dawid.myfirstwords.service.parents.ChangePasswordService;
import pl.kasprzak.dawid.myfirstwords.service.parents.CreateParentService;
import pl.kasprzak.dawid.myfirstwords.service.parents.DeleteParentService;
import pl.kasprzak.dawid.myfirstwords.service.parents.GetParentService;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/parents")
public class ParentController {

    private final CreateParentService createParentService;
    private final GetParentService getParentService;
    private final DeleteParentService deleteParentService;
    private final ChangePasswordService changePasswordService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CreateParentResponse registerParent(@Valid @RequestBody CreateParentRequest request){
        return createParentService.saveParent(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public GetAllParentsResponse getAllRegisterParents(){
       return getParentService.getAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{parentId}")
    public ParentInfoResponse getRegisterParentsById(@PathVariable Long parentId){
        return getParentService.getById(parentId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{parentId}")
    public void deleteAccount(@PathVariable Long parentId){
        deleteParentService.deleteAccount(parentId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{parentId}/password")
    public void changePassword(@PathVariable Long parentId, @RequestBody ChangePasswordRequest request){
        changePasswordService.changePasswordForParent(parentId, request);
    }
}
