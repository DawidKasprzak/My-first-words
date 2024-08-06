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
import pl.kasprzak.dawid.myfirstwords.model.milestones.*;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
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


    @Operation(summary = "Add a new milestone", description = "Creates a new milestone for the specified child. This endpoint is accessible to authenticated parents and verifies the parent-child relationship.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Milestone successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{childId}")
    public CreateMilestoneResponse addMilestone(@PathVariable Long childId, @Valid @RequestBody CreateMilestoneRequest request,
                                                @Parameter(hidden = true) Authentication authentication) {
        return createMilestoneService.addMilestone(childId, request, authentication);
    }

    @Operation(summary = "Delete a milestone", description = "Deletes a milestone by its ID for the specified child. This endpoint is accessible to authenticated parents and verifies the parent-child relationship.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Milestone successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Parent, child or milestone not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{childId}/{milestoneId}")
    public void deleteMilestone(@PathVariable Long childId, @PathVariable Long milestoneId,
                                @Parameter(hidden = true) Authentication authentication) {
        deleteMilestoneService.deleteMilestone(childId, milestoneId, authentication);
    }

    @Operation(summary = "Get milestones before a date", description = "Fetches all milestones added before the specified date for a specific child. This endpoint is accessible to authenticated parents and verifies the parent-child relationship.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Milestone successfully retrieved"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/before/{date}")
    public List<GetMilestoneResponse> getByDateAchieveBefore(@PathVariable Long childId, @PathVariable LocalDate date,
                                                             @Parameter(hidden = true) Authentication authentication) {
        return getMilestoneService.getByDateAchieveBefore(childId, date, authentication);
    }

    @Operation(summary = "Get milestones after a date", description = "Fetches all milestones added after the specified date for a specific child. This endpoint is accessible to authenticated parents and verifies the parent-child relationship.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Milestone successfully retrieved"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/after/{date}")
    public List<GetMilestoneResponse> getByDateAchieveAfter(@PathVariable Long childId, @PathVariable LocalDate date,
                                                            @Parameter(hidden = true) Authentication authentication) {
        return getMilestoneService.getByDateAchieveAfter(childId, date, authentication);
    }

    @Operation(summary = "Get milestones between dates", description = "Fetches all milestones added between the specified start and end dates for a specific child. This endpoint is accessible to authenticated parents and verifies the parent-child relationship.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Milestones successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/between")
    public List<GetMilestoneResponse> getMilestoneBetweenDays(@PathVariable Long childId, @RequestParam LocalDate startDate,
                                                              @RequestParam LocalDate endDate,
                                                              @Parameter(hidden = true) Authentication authentication) {
        return getMilestoneService.getMilestonesBetweenDays(childId, startDate, endDate, authentication);
    }

    @Operation(summary = "Get all milestones", description = "Fetches all milestone for a specific child. This endpoint is accessible to authenticated parents and verifies the parent-child relationship.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Milestones successfully retrieved"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Parent or child not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}")
    public GetAllMilestoneResponse getAllMilestones(@PathVariable Long childId,
                                                    @Parameter(hidden = true) Authentication authentication) {
        return getMilestoneService.getAllMilestone(childId, authentication);
    }

    @Operation(summary = "Retrieve milestones by title", description = "Fetches all milestones for a specific child that match a given title. This endpoint is accessible to authenticated parents and verifies the parent-child relationship.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Milestones successfully retrieved"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Parent, child or milestone not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/title")
    public GetAllMilestoneResponse getByTitle(@PathVariable Long childId, @RequestParam String title,
                                              @Parameter(hidden = true) Authentication authentication) {
        return getMilestoneService.getByTitle(childId, title, authentication);
    }

    @Operation(summary = "Update a milestone", description = "Updates the details of an existing milestone for a specific child. This endpoint is accessible to authenticated parents and verifies the parent-child relationship.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Milestone successfully updated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Parent, child or milestone not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{childId}/{milestoneId}")
    public UpdateMilestoneResponse updateMilestone(@PathVariable Long childId, @PathVariable Long milestoneId,
                                                   @Valid @RequestBody UpdateMilestoneRequest request,
                                                   @Parameter(hidden = true) Authentication authentication) {
        MilestoneEntity milestone = updateMilestoneService.updateMilestone(childId, milestoneId, request, authentication);
        return updateMilestonesConverter.toDto(milestone);
    }
}
