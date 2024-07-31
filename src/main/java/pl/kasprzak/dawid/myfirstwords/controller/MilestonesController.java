package pl.kasprzak.dawid.myfirstwords.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.kasprzak.dawid.myfirstwords.exception.*;
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


    /**
     * Adds a new milestone for a specific child.
     * This endpoint allows an authenticated parent to add a new milestone to their child's.
     *
     * @param childId        the ID of the child to whom the milestone belongs.
     * @param request        the CreateMilestoneRequest object containing the details of the milestone.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a CreateMilestoneResponse containing the details of the newly added milestone.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException  if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException   if the parent is not authorized to view the child (HTTP 403).
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{childId}")
    public CreateMilestoneResponse addMilestone(@PathVariable Long childId, @Valid @RequestBody CreateMilestoneRequest request,
                                                Authentication authentication) {
        return createMilestoneService.addMilestone(childId, request, authentication);
    }

    /**
     * Deletes a milestone by the given ID for a specific child.
     * This endpoint allows an authenticated parent to delete a milestone from their child's.
     *
     * @param childId        the ID of the child whose milestone is to be deleted.
     * @param milestoneId    the ID of the milestone to be deleted.
     * @param authentication the authentication object containing the parent's credentials.
     * @throws ParentNotFoundException    if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException     if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException      if the parent is not authorized to view the child (HTTP 403).
     * @throws MilestoneNotFoundException if the milestone with the given ID is not found (HTTP 404).
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{childId}/{milestoneId}")
    public void deleteMilestone(@PathVariable Long childId, @PathVariable Long milestoneId, Authentication authentication) {
        deleteMilestoneService.deleteMilestone(childId, milestoneId, authentication);
    }

    /**
     * Retrieves all milestones added before the specified date for a specific child.
     * This endpoint allows an authenticated parent to fetch milestones before a certain date.
     *
     * @param childId        the ID of the child whose milestones are to be retrieved.
     * @param date           the date after which the milestones were added.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a list of GetMilestoneResponse containing the details of the milestones.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException  if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException   if the parent is not authorized to view the child (HTTP 403).
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/before/{date}")
    public List<GetMilestoneResponse> getByDateAchieveBefore(@PathVariable Long childId, @PathVariable LocalDate date,
                                                             Authentication authentication) {
        return getMilestoneService.getByDateAchieveBefore(childId, date, authentication);
    }

    /**
     * Retrieves all milestones added after the specified date for a specific child.
     * This endpoint allows an authenticated parent to fetch milestones after a certain date.
     *
     * @param childId        the ID of the child whose milestones are to be retrieved.
     * @param date           the date after which the milestones were added.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a list of GetMilestoneResponse containing the details of the milestones.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException  if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException   if the parent is not authorized to view the child (HTTP 403).
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/after/{date}")
    public List<GetMilestoneResponse> getByDateAchieveAfter(@PathVariable Long childId, @PathVariable LocalDate date,
                                                            Authentication authentication) {
        return getMilestoneService.getByDateAchieveAfter(childId, date, authentication);
    }

    /**
     * Retrieves all milestones added between the specified start and end dates for a specific child.
     * This endpoint allows an authenticated parent to fetch milestones within a specific date range.
     *
     * @param childId        the ID of the child whose milestones are to be retrieved.
     * @param startDate      the start date of the range.
     * @param endDate        the end date of the range.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a list of GetMilestoneResponse containing the details of the milestones.
     * @throws ParentNotFoundException   if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException    if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException     if the parent is not authorized to view the child (HTTP 403).
     * @throws DateValidationException   if the start date or end date is invalid (HTTP 400).
     * @throws InvalidDateOrderException if the start date is after the end date (HTTP 400).
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/between")
    public List<GetMilestoneResponse> getMilestoneBetweenDays(@PathVariable Long childId, @RequestParam LocalDate startDate,
                                                              @RequestParam LocalDate endDate, Authentication authentication) {
        return getMilestoneService.getMilestonesBetweenDays(childId, startDate, endDate, authentication);
    }

    /**
     * Retrieves all milestones for a specific child.
     * This endpoint allows an authenticated parent to fetch all milestones associated with their child.
     *
     * @param childId        the ID of the child whose milestones are to be retrieved.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a GetAllMilestoneResponse containing the details of all milestones.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException  if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException   if the parent is not authorized to view the child (HTTP 403).
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}")
    public GetAllMilestoneResponse getAllMilestones(@PathVariable Long childId, Authentication authentication) {
        return getMilestoneService.getAllMilestone(childId, authentication);
    }

    /**
     * Retrieves all milestones for a specific child based on a title search.
     * This endpoint allows an authenticated parent to fetch milestones for their child that match a specified title.
     *
     * @param childId        the ID of the child whose milestones are to be retrieved.
     * @param title          the title or part of the title to search for in the milestones.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a GetAllMilestoneResponse containing a list of milestones that match the search criteria.
     * @throws ParentNotFoundException    if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException     if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException      if the parent is not authorized to view the child (HTTP 403).
     * @throws MilestoneNotFoundException if no milestones are found with the specified title (HTTP 404).
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/title")
    public GetAllMilestoneResponse getByTitle(@PathVariable Long childId, @RequestParam String title, Authentication authentication) {
        return getMilestoneService.getByTitle(childId, title, authentication);
    }

    /**
     * Updates an existing milestone for a specific child.
     * This endpoint allows an authenticated parent to update details of a milestone associated with their child.
     *
     * @param childId        the ID of the child whose milestone is to be updated.
     * @param milestoneId    the ID of the milestone to be updated.
     * @param request        the UpdateMilestoneRequest object containing the new details for the milestone.
     * @param authentication the authentication object containing the parent's credentials.
     * @return an UpdateMilestoneResponse containing the updated details of the milestone.
     * @throws ParentNotFoundException    if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException     if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException      if the parent is not authorized to view the child (HTTP 403).
     * @throws MilestoneNotFoundException if the milestone with the given ID is not found (HTTP 404).
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{childId}/{milestoneId}")
    public UpdateMilestoneResponse updateMilestone(@PathVariable Long childId, @PathVariable Long milestoneId,
                                                   @Valid @RequestBody UpdateMilestoneRequest request, Authentication authentication) {
        MilestoneEntity milestone = updateMilestoneService.updateMilestone(childId, milestoneId, request, authentication);
        return updateMilestonesConverter.toDto(milestone);
    }
}
