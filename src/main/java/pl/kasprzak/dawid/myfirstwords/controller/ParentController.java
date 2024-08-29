package pl.kasprzak.dawid.myfirstwords.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.kasprzak.dawid.myfirstwords.model.parents.*;
import pl.kasprzak.dawid.myfirstwords.security.annotations.AccountOwnerOrAdmin;
import pl.kasprzak.dawid.myfirstwords.security.annotations.AllowedForAdmin;
import pl.kasprzak.dawid.myfirstwords.security.annotations.PasswordOwnerOrAdmin;
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

    @Operation(summary = "Register a new parent account", description = "Creates a new parent account based on the provided request data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parent successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CreateParentResponse registerParent(@Valid @RequestBody CreateParentRequest request) {
        return createParentService.saveParent(request);
    }


    @Operation(summary = "Retrieve all register parents", description = "Fetches all registered parents.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all parents")
    @AllowedForAdmin
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public GetAllParentsResponse getAllRegisterParents() {
        return getParentService.getAll();
    }


    @Operation(summary = "Retrieve a parent by ID", description = "Fetches a parent by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the parent"),
            @ApiResponse(responseCode = "404", description = "Parent not found")
    })
    @AllowedForAdmin
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{parentId}")
    public ParentInfoResponse getParentsById(@PathVariable Long parentId) {
        return getParentService.getById(parentId);
    }

    @Operation(summary = "Delete a parent account", description = "Deletes a parent account based on the provided parent ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Parent successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Parent not found")
    })
    @AccountOwnerOrAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{parentId}")
    public void deleteAccount(@PathVariable Long parentId) {
        deleteParentService.deleteAccount(parentId);
    }

    @Operation(summary = "Change parent password", description = "Changes the password for a parent account based on the provided parent ID and request data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Parent not found")
    })
    @PasswordOwnerOrAdmin
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{parentId}/password")
    public void changePassword(@PathVariable Long parentId, @RequestBody ChangePasswordRequest request) {
        changePasswordService.changePasswordForParent(parentId, request);
    }
}
