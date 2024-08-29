package pl.kasprzak.dawid.myfirstwords.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.kasprzak.dawid.myfirstwords.repository.ParentsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.security.AuthoritiesProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdditionalParentAuthoritiesFilter extends OncePerRequestFilter {

    private final AuthoritiesProvider authoritiesProvider;
    private final ParentsRepository parentsRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            Optional<ParentEntity> parentEntityOptional = parentsRepository.findByUsername(auth.getName());

            if (parentEntityOptional.isPresent()) {
                ParentEntity parentEntity = parentEntityOptional.get();

                Collection<String> additionalAuthorities = authoritiesProvider.checkAdditionalAuthorities(parentEntity);

                List<GrantedAuthority> updateAuthorities = new ArrayList<>(auth.getAuthorities());
                additionalAuthorities.forEach(role -> updateAuthorities.add(new SimpleGrantedAuthority(role)));

                Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updateAuthorities);
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
