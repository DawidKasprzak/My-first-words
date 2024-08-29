package pl.kasprzak.dawid.myfirstwords.security.authorities;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordOwnerProvider implements AuthorityProvider {
    @Override
    public boolean canHandle(HttpServletRequest request) {

        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match("/api/parents/*/password", request.getRequestURI()) && "PUT".equals(request.getMethod());
    }

    @Override
    public List<String> getAdditionalAuthorities(HttpServletRequest request, ParentEntity parentEntity) {
        Optional<Long> id = Optional.ofNullable(request.getRequestURI())
                .map(uri -> uri.replaceAll("/api/parents/([1-9]+)/password", "$1"))
                .filter(str -> !str.isBlank())
                .filter(str -> str.matches("[1-9]+"))
                .map(Long::valueOf);

        if (id.isPresent() && parentEntity.getId().equals(id.get())) {
            return Collections.singletonList("PASSWORD_OWNER");
        }
        return Collections.emptyList();
    }
}
