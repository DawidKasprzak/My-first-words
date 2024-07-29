package pl.kasprzak.dawid.myfirstwords.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildRequest;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.GetAllChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.GetChildResponse;
import pl.kasprzak.dawid.myfirstwords.service.children.CreateChildService;
import pl.kasprzak.dawid.myfirstwords.service.children.DeleteChildService;
import pl.kasprzak.dawid.myfirstwords.service.children.GetChildService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/children")
public class ChildController {

    private final CreateChildService createChildService;
    private final DeleteChildService deleteChildService;
    private final GetChildService getChildService;

    /**
     * Creates a new child for the authenticated parent.
     * This endpoint allows an authenticated parent to add a new child to their account.
     *
     * @param request        the CreateChildRequest object containing the child's details.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a CreateChildResponse containing the details of the newly created child.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CreateChildResponse addChild(@Valid @RequestBody CreateChildRequest request, Authentication authentication) {
        return createChildService.addChild(request, authentication);
    }

    /**
     * Deletes a child by the given ID.
     * This endpoint allows an authenticated parent to delete a child from their account.
     *
     * @param childId        the ID of the child to be deleted.
     * @param authentication the authentication object containing the parent's credentials.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException  if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException   if the parent is not authorized to delete the child (HTTP 403).
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{childId}")
    public void deleteChild(@PathVariable Long childId, Authentication authentication) {
        deleteChildService.deleteChild(childId, authentication);
    }

    /**
     * Retrieves all children associated with the authenticated parent.
     * This endpoint allows an authenticated parent to fetch all child records linked to their account.
     *
     * @param authentication the authentication object containing the parent's credentials.
     * @return a GetAllChildResponse containing a list of child details.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public GetAllChildResponse getAllChildren(Authentication authentication) {
        return getChildService.getAllChildrenOfParent(authentication);
    }

    /**
     * Retrieves a child by the given ID.
     * This endpoint allows an authenticated parent to fetch the details of a specific child linked to their account.
     *
     * @param childId        the ID of the child to be retrieved.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a GetChildResponse containing the child's details.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException  if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException   if the parent is not authorized to view the child (HTTP 403).
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}")
    public GetChildResponse getChildById(@PathVariable Long childId, Authentication authentication) {
        return getChildService.getChildById(childId, authentication);
    }
}
