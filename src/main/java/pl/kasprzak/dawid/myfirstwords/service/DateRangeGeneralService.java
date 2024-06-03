package pl.kasprzak.dawid.myfirstwords.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;
import pl.kasprzak.dawid.myfirstwords.util.TriFunction;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
public class DateRangeGeneralService {

    private final AuthorizationHelper authorizationHelper;

    private <T> List<T> getEntitiesByDate(Long childId, LocalDate date, Authentication authentication,
                                          BiFunction<Long, LocalDate, List<T>> repositoryMethod){
        if (date == null){
            throw new IllegalArgumentException("Date must not be null");
        }
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        return repositoryMethod.apply(childId, date);
    }

    private <T> List<T> getEntitiesByRangeDate(Long childId, LocalDate startDate, LocalDate endDate, Authentication authentication,
                                               TriFunction<Long, LocalDate, LocalDate, List<T>> repositoryMethod){
        if (startDate == null || endDate == null){
            throw new IllegalArgumentException("Start date and end date must not be null");
        }
        if (startDate.isAfter(endDate)){
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        return repositoryMethod.apply(childId, startDate, endDate);
    }

    public <T> List<T> getByDateAchieveBefore(Long childId, LocalDate date, Authentication authentication,
                                             BiFunction<Long, LocalDate, List<T>> repositoryMethod){
        return getEntitiesByDate(childId, date, authentication, repositoryMethod);
    }

    public <T> List<T> getByDateAchieveAfter(Long childId, LocalDate date, Authentication authentication,
                                            BiFunction<Long, LocalDate, List<T>> repositoryMethod){
        return getEntitiesByDate(childId, date, authentication, repositoryMethod);
    }

    public <T> List<T> getWordsBetweenDays(Long childId, LocalDate startDate, LocalDate endDate, Authentication authentication,
                                           TriFunction<Long, LocalDate, LocalDate, List<T>> repositoryMethod){
        return getEntitiesByRangeDate(childId, startDate, endDate, authentication, repositoryMethod);
    }
 }
