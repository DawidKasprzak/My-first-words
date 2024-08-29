package pl.kasprzak.dawid.myfirstwords.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildRequest;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.GetAllChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.GetChildResponse;
import pl.kasprzak.dawid.myfirstwords.security.annotations.ChildOwnerOrAdmin;
import pl.kasprzak.dawid.myfirstwords.security.annotations.IsLoggedUser;
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

    @Operation(summary = "Add a new child", description = "Creates a new child for the authenticated parent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Child successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied, authentication required"),
            @ApiResponse(responseCode = "404", description = "Parent not found")
    })
    @IsLoggedUser
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CreateChildResponse addChild(@Valid @RequestBody CreateChildRequest request) {
        return createChildService.addChild(request);
    }


    @Operation(summary = "Delete a child by ID", description = "Deletes a child by their ID for the authenticated parent or an administrator.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Child deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied, parent is not the owner of the child or an administrator"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @ChildOwnerOrAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{childId}")
    public void deleteChild(@PathVariable Long childId) {
        deleteChildService.deleteChild(childId);
    }


    @Operation(summary = "Retrieve all children",
            description = "Fetches all children associated with the authenticated parent or an administrator. " +
                    "If the authenticated user is a parent, they can retrieve their own children without providing a parentID. " +
                    "If the authenticated user is an administrator, they must provide a parentID to retrieve the children associated with that parent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Children details retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request, parentID is required for administrators"),
            @ApiResponse(responseCode = "403", description = "Access denied, user is not authorized"),
            @ApiResponse(responseCode = "404", description = "Parent not found")
    })
    @ChildOwnerOrAdmin
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public GetAllChildResponse getAllChildren(@RequestParam(value = "parentID", required = false) Long parentID) {
        return getChildService.getAllChildrenOfParent(parentID);
    }


    @Operation(summary = "Retrieve a child by their ID", description = "Fetches details of a specific child by their ID for the authenticated parent or an administrator.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Child details retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied, user is not authorized to access the child"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @ChildOwnerOrAdmin
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}")
    public GetChildResponse getChildById(@PathVariable Long childId) {
        return getChildService.getChildById(childId);
    }
}
