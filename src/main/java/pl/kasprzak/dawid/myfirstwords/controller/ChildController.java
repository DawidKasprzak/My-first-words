package pl.kasprzak.dawid.myfirstwords.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildRequest;
import pl.kasprzak.dawid.myfirstwords.model.children.CreateChildResponse;
import pl.kasprzak.dawid.myfirstwords.repository.ChildrenRepository;
import pl.kasprzak.dawid.myfirstwords.service.children.CreateChildService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/parents")
public class ChildController {

    private final CreateChildService createChildService;

    @PostMapping("/children")
    public CreateChildResponse addChild(@Valid @RequestBody CreateChildRequest request, Authentication authentication){
        return createChildService.addChild(request, authentication);
    }
}
