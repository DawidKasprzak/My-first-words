package pl.kasprzak.dawid.myfirstwords.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.security.authorities.AuthorityProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthoritiesProvider {

    private final List<AuthorityProvider> authorityProviderList;
    private final HttpServletRequest request;

    public Collection<String> checkAdditionalAuthorities(ParentEntity parentEntity){
        List<String> authorities = new ArrayList<>();
        for (AuthorityProvider provider : authorityProviderList){
            if (provider.canHandle(request)){
                authorities.addAll(provider.getAdditionalAuthorities(request, parentEntity));
            }
        }
        return authorities;
    }
}
