package pl.kasprzak.dawid.myfirstwords.controller;

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


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{childId}")
    public CreateMilestoneResponse addMilestone(@PathVariable Long childId, @Valid @RequestBody CreateMilestoneRequest request,
                                                Authentication authentication) {
        return createMilestoneService.addMilestone(childId, request, authentication);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{childId}/{milestoneId}")
    public void deleteMilestone(@PathVariable Long childId, @PathVariable Long milestoneId, Authentication authentication) {
        deleteMilestoneService.deleteMilestone(childId, milestoneId, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/before/{date}")
    public List<GetMilestoneResponse> getByDateAchieveBefore(@PathVariable Long childId, @PathVariable LocalDate date,
                                                             Authentication authentication) {
        return getMilestoneService.getByDateAchieveBefore(childId, date, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/after/{date}")
    public List<GetMilestoneResponse> getByDateAchieveAfter(@PathVariable Long childId, @PathVariable LocalDate date,
                                                            Authentication authentication) {
        return getMilestoneService.getByDateAchieveAfter(childId, date, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/between")
    public List<GetMilestoneResponse> getMilestoneBetweenDays(@PathVariable Long childId, @RequestParam LocalDate startDate,
                                                              @RequestParam LocalDate endDate, Authentication authentication) {
        return getMilestoneService.getMilestonesBetweenDays(childId, startDate, endDate, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}")
    public GetAllMilestoneResponse getAllMilestones(@PathVariable Long childId, Authentication authentication) {
        return getMilestoneService.getAllMilestone(childId, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/title")
    public GetMilestoneResponse getByTitle(@PathVariable Long childId, @RequestParam String title, Authentication authentication) {
        return getMilestoneService.getByTitle(childId, title, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{childId}/{milestoneId}")
    public UpdateMilestoneResponse updateMilestone(@PathVariable Long childId, @PathVariable Long milestoneId,
                                                   @Valid @RequestBody UpdateMilestoneRequest request, Authentication authentication) {
        MilestoneEntity milestone = updateMilestoneService.updateMilestone(childId, milestoneId, request, authentication);
        return updateMilestonesConverter.toDto(milestone);
    }
}
