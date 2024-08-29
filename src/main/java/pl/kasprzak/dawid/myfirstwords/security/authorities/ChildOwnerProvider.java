package pl.kasprzak.dawid.myfirstwords.security.authorities;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ChildOwnerProvider implements AuthorityProvider {

    private static final List<String> SUPPORTED_PATHS = List.of(
            "/api/children/**",
            "/api/words/**",
            "/api/milestones/**"
    );

    @Override
    public boolean canHandle(HttpServletRequest request) {

        AntPathMatcher matcher = new AntPathMatcher();
        return SUPPORTED_PATHS.stream()
                .anyMatch(path -> matcher.match(path, request.getRequestURI()));

    }

    @Override
    public List<String> getAdditionalAuthorities(HttpServletRequest request, ParentEntity parentEntity) {
        Optional<Long> childId = extractChildIdFromUri(request.getRequestURI());

        log.info("Extracted child ID: {}", childId.orElse(null));
        log.info("Parent has children: {}", parentEntity.getChildren().stream()
                .map(ChildEntity::getId)
                .collect(Collectors.toList()));

        if (childId.isPresent() && parentEntity.getChildren().stream().anyMatch(child -> child.getId().equals(childId.get()))) {
            log.info("Child Id {} belongs to parent ID {}", childId.get(), parentEntity.getId());
            return Collections.singletonList("CHILD_OWNER");
        }
        if (!childId.isPresent() && request.getRequestURI().endsWith("/api/children") && !parentEntity.getChildren().isEmpty()){
            return Collections.singletonList("CHILD_OWNER");
        }

        log.warn("Child Id {} does not belong to parent ID {}", childId.orElse(null), parentEntity.getId());
        return Collections.emptyList();

    }

    private Optional<Long> extractChildIdFromUri(String uri) {
        return Optional.ofNullable(uri)
                .map(this::extractIdFromSupportedPaths)
                .filter(str -> !str.isBlank())
                .filter(str -> str.matches("[1-9][0-9]*"))
                .map(Long::valueOf);
    }

    private String extractIdFromSupportedPaths(String uri) {
        if (uri.matches("/api/children(/([1-9]+))?")) {
            return uri.replaceAll("/api/children(/([1-9]+))?", "$2");

        } else if (uri.matches("/api/words/([1-9]+)(/.*)?")) {
            return uri.replaceAll("/api/words/([1-9]+)(/.*)?", "$1");

        } else if (uri.matches("/api/milestones/([1-9]+)(/.*)?")) {
            return uri.replaceAll("/api/milestones/([1-9]+)(/.*)?", "$1");
        }
        return "";
    }
}
