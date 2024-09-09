package pl.kasprzak.dawid.myfirstwords.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.kasprzak.dawid.myfirstwords.model.milestones.*;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.security.annotations.ChildOwnerOrAdmin;
import pl.kasprzak.dawid.myfirstwords.security.annotations.IsLoggedUser;
import pl.kasprzak.dawid.myfirstwords.service.converters.milestones.UpdateMilestonesConverter;
import pl.kasprzak.dawid.myfirstwords.service.milestones.CreateMilestoneService;
import pl.kasprzak.dawid.myfirstwords.service.milestones.DeleteMilestoneService;
import pl.kasprzak.dawid.myfirstwords.service.milestones.GetMilestoneService;
import pl.kasprzak.dawid.myfirstwords.service.milestones.UpdateMilestoneService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/milestones")
public class MilestonesController {

    private final CreateMilestoneService createMilestoneService;
    private final GetMilestoneService getMilestoneService;
    private final DeleteMilestoneService deleteMilestoneService;
    private final UpdateMilestoneService updateMilestoneService;
    private final UpdateMilestonesConverter updateMilestonesConverter;


    @Operation(summary = "Add a new milestone", description = "Creates a new milestone for the specified child. This endpoint is accessible to authenticated parents and administrators and verifies the parent-child relationship.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Milestone successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied, authentication required"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @IsLoggedUser
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{childId}")
    public CreateMilestoneResponse addMilestone(@PathVariable Long childId, @Valid @RequestBody CreateMilestoneRequest request) {
        return createMilestoneService.addMilestone(childId, request);
    }

    @Operation(summary = "Delete a milestone by ID",
            description = "Deletes a milestone by its ID for the specified child for the authenticated parent or an administrator. " +
                    "If the authenticated user is a parent, they can delete a word for their own child without providing a parentID. " +
                    "If the authenticated user is an administrator, they must provide a parentID to delete a word associated with a child of that parent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Milestone successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Bad Request, parentID is required for administrators"),
            @ApiResponse(responseCode = "403", description = "Access denied, parent is not the owner of the child or user is not an administrator"),
            @ApiResponse(responseCode = "404", description = "Parent, child or milestone not found")
    })
    @ChildOwnerOrAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{childId}/{milestoneId}")
    public void deleteMilestone(@PathVariable Long childId,
                                @PathVariable Long milestoneId,
                                @RequestParam(value = "parentID", required = false) Long parentID) {
        deleteMilestoneService.deleteMilestone(childId, milestoneId, parentID);
    }

    @Operation(summary = "Get milestones before a specified date",
            description = "Fetches all milestones added before the specified date for the specified child. " +
                    "If the authenticated user is a parent, they can retrieve milestones for their own child without providing a parentID. " +
                    "If the authenticated user is an administrator, they must provide a parentID to retrieve milestones associated with a child of that parent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Milestone successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad Request, parentID is required for administrators"),
            @ApiResponse(responseCode = "403", description = "Access denied, parent is not the owner of the child or user is not an administrator"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @ChildOwnerOrAdmin
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/before/{date}")
    public List<GetMilestoneResponse> getByDateAchieveBefore(@PathVariable Long childId,
                                                             @PathVariable LocalDate date,
                                                             @RequestParam(value = "parentID", required = false) Long parentID) {
        return getMilestoneService.getByDateAchieveBefore(childId, date, parentID);
    }

    @Operation(summary = "Get milestones after a specified date",
            description = "Fetches all milestones added after the specified date for the specified child. " +
                    "If the authenticated user is a parent, they can retrieve milestones for their own child without providing a parentID. " +
                    "If the authenticated user is an administrator, they must provide a parentID to retrieve milestones associated with a child of that parent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Milestone successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad Request, parentID is required for administrators"),
            @ApiResponse(responseCode = "403", description = "Access denied, parent is not the owner of the child or user is not an administrator"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @ChildOwnerOrAdmin
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/after/{date}")
    public List<GetMilestoneResponse> getByDateAchieveAfter(@PathVariable Long childId,
                                                            @PathVariable LocalDate date,
                                                            @RequestParam(value = "parentID", required = false) Long parentID) {
        return getMilestoneService.getByDateAchieveAfter(childId, date, parentID);
    }

    @Operation(summary = "Get milestones between a specified dates",
            description = "Fetches all milestones added between the specified dates for the specified child. " +
                    "If the authenticated user is a parent, they can retrieve milestones for their own child without providing a parentID. " +
                    "If the authenticated user is an administrator, they must provide a parentID to retrieve milestones associated with a child of that parent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Milestones successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "400", description = "Bad Request, parentID is required for administrators"),
            @ApiResponse(responseCode = "403", description = "Access denied, parent is not the owner of the child or user is not an administrator"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @ChildOwnerOrAdmin
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/between")
    public List<GetMilestoneResponse> getMilestoneBetweenDays(@PathVariable Long childId,
                                                              @RequestParam LocalDate startDate,
                                                              @RequestParam LocalDate endDate,
                                                              @RequestParam(value = "parentID", required = false) Long parentID) {
        return getMilestoneService.getMilestonesBetweenDays(childId, startDate, endDate, parentID);
    }

    @Operation(summary = "Get all milestones", description = "Fetches all milestone for a specific child. This endpoint is accessible to authenticated parents and administrators, and verifies the parent-child relationship.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Milestones successfully retrieved"),
            @ApiResponse(responseCode = "403", description = "Access denied, user is not authorized to access the child"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @ChildOwnerOrAdmin
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}")
    public GetAllMilestoneResponse getAllMilestones(@PathVariable Long childId) {
        return getMilestoneService.getAllMilestone(childId);
    }

    @Operation(summary = "Retrieve milestones by title", description = "Fetches all milestones for a specific child that match a given title. This endpoint is accessible to authenticated parents and administrators and verifies the parent-child relationship.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Milestones successfully retrieved"),
            @ApiResponse(responseCode = "403", description = "Access denied, user is not authorized to access the child"),
            @ApiResponse(responseCode = "404", description = "Parent, child or milestone not found")
    })
    @ChildOwnerOrAdmin
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/title")
    public GetAllMilestoneResponse getByTitle(@PathVariable Long childId, @RequestParam String title) {
        return getMilestoneService.getByTitle(childId, title);
    }

    @Operation(summary = "Update a milestone", description = "Updates the details of an existing milestone for a specific child. This endpoint is accessible to authenticated parents and administrators, and verifies the parent-child relationship.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Milestone successfully updated"),
            @ApiResponse(responseCode = "403", description = "Access denied, user is not authorized to update this milestone"),
            @ApiResponse(responseCode = "404", description = "Parent, child or milestone not found")
    })
    @ChildOwnerOrAdmin
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{childId}/{milestoneId}")
    public UpdateMilestoneResponse updateMilestone(@PathVariable Long childId, @PathVariable Long milestoneId,
                                                   @Valid @RequestBody UpdateMilestoneRequest request) {
        MilestoneEntity milestone = updateMilestoneService.updateMilestone(childId, milestoneId, request);
        return updateMilestonesConverter.toDto(milestone);
    }
}
