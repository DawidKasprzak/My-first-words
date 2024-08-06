package pl.kasprzak.dawid.myfirstwords.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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

    @Operation(summary = "Add a new child", description = "Creates a new child for the authenticated parent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Child successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Parent not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CreateChildResponse addChild(@Valid @RequestBody CreateChildRequest request,
                                        @Parameter(hidden = true) Authentication authentication) {
        return createChildService.addChild(request, authentication);
    }

    @Operation(summary = "Delete a child by ID", description = "Deletes a child by their ID for the authenticated parent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Child deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{childId}")
    public void deleteChild(@PathVariable Long childId,
                            @Parameter(hidden = true) Authentication authentication) {
        deleteChildService.deleteChild(childId, authentication);
    }

    @Operation(summary = "Retrieve all children", description = "Fetches all children associated with the authenticated parent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Children details retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Parent not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public GetAllChildResponse getAllChildren(@Parameter(hidden = true) Authentication authentication) {
        return getChildService.getAllChildrenOfParent(authentication);
    }

    @Operation(summary = "Retrieve a child by their ID", description = "Fetches details of a specific child by their ID for the authenticated parent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Child details retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}")
    public GetChildResponse getChildById(@PathVariable Long childId,
                                         @Parameter(hidden = true) Authentication authentication) {
        return getChildService.getChildById(childId, authentication);
    }
}
