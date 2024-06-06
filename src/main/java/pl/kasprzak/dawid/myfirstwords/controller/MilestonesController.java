package pl.kasprzak.dawid.myfirstwords.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.kasprzak.dawid.myfirstwords.model.milestones.CreateMilestoneRequest;
import pl.kasprzak.dawid.myfirstwords.model.milestones.CreateMilestoneResponse;
import pl.kasprzak.dawid.myfirstwords.service.milestones.CreateMilestoneService;
import pl.kasprzak.dawid.myfirstwords.service.milestones.GetMilestoneService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/milestones")
public class MilestonesController {

    private final CreateMilestoneService createMilestoneService;
    private final GetMilestoneService getMilestoneService;


    @PostMapping(path = "/{childId}")
    public CreateMilestoneResponse addMilestone(@PathVariable Long childId, @Valid @RequestBody CreateMilestoneRequest request,
                                                Authentication authentication){
        return createMilestoneService.addMilestone(childId, request, authentication);
    }
}
