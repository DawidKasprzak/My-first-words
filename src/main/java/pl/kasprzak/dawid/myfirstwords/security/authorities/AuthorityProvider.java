package pl.kasprzak.dawid.myfirstwords.security.authorities;

import jakarta.servlet.http.HttpServletRequest;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;

import java.util.List;

public interface AuthorityProvider {

    boolean canHandle(HttpServletRequest request);

    List<String> getAdditionalAuthorities(HttpServletRequest request, ParentEntity parentEntity);
}
