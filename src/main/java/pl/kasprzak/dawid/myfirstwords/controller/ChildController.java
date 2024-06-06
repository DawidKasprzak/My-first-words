package pl.kasprzak.dawid.myfirstwords.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildRequest;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildResponse;
import pl.kasprzak.dawid.myfirstwords.model.children.GetAllChildResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
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

    @PostMapping
    public CreateChildResponse addChild(@Valid @RequestBody CreateChildRequest request, Authentication authentication){
        return createChildService.addChild(request, authentication);
    }

    @DeleteMapping(path = "/{childId}")
    public void deleteChild(@PathVariable Long childId, Authentication authentication){
        deleteChildService.deleteChild(childId, authentication);
    }

    @GetMapping
    public GetAllChildResponse getAllChildren(Authentication authentication){
        return getChildService.getAllChildrenForParent(authentication);
    }
}
