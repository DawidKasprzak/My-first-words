package pl.kasprzak.dawid.myfirstwords.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.kasprzak.dawid.myfirstwords.exception.EmailAlreadyExistsException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.UsernameAlreadyExistsException;
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

    /**
     * Registers a new parent account.
     * This endpoint creates a new parent account based on the provided request data.
     *
     * @param request the CreateParentRequest object containing the parent's registration details.
     * @return a CreateParentResponse containing the details of the newly registered parent.
     * @throws UsernameAlreadyExistsException if the username is already taken (HTTP 400).
     * @throws EmailAlreadyExistsException    if the email is already used by another account (HTTP 400).
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CreateParentResponse registerParent(@Valid @RequestBody CreateParentRequest request) {
        return createParentService.saveParent(request);
    }

    /**
     * Retrieves all registered parents.
     * This endpoint returns a list of all parent accounts currently registered in the system.
     *
     * @return a GetAllParentsResponse containing a list of parent details.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public GetAllParentsResponse getAllRegisterParents() {
        return getParentService.getAll();
    }

    /**
     * Retrieves a parent by their ID.
     * This endpoint fetches the details of a specific parent based on the provided ID.
     *
     * @param parentId the ID of the parent to be retrieved.
     * @return a ParentInfoResponse containing the parent's details.
     * @throws ParentNotFoundException if the parent with the given ID is not found (HTTP 404).
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{parentId}")
    public ParentInfoResponse getParentsById(@PathVariable Long parentId) {
        return getParentService.getById(parentId);
    }

    /**
     * Deletes a parent account by their ID.
     * This endpoint deletes the parent account associated with the provided ID.
     *
     * @param parentId the ID of the parent account to be deleted.
     * @throws ParentNotFoundException if the parent with the given ID is not found (HTTP 404).
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{parentId}")
    public void deleteAccount(@PathVariable Long parentId) {
        deleteParentService.deleteAccount(parentId);
    }

    /**
     * Changes the password for a parent account.
     * This endpoint allows changing the password for the parent account identified by the provided ID.
     *
     * @param parentId the ID of the parent whose password is to be changed.
     * @param request  the ChangePasswordRequest object containing the new password details.
     * @throws ParentNotFoundException if the parent with the given ID is not found (HTTP 404).
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{parentId}/password")
    public void changePassword(@PathVariable Long parentId, @RequestBody ChangePasswordRequest request) {
        changePasswordService.changePasswordForParent(parentId, request);
    }
}
