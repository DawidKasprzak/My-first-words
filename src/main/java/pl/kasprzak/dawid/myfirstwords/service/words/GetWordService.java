package pl.kasprzak.dawid.myfirstwords.service.words;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.DateValidationException;
import pl.kasprzak.dawid.myfirstwords.exception.InvalidDateOrderException;
import pl.kasprzak.dawid.myfirstwords.exception.WordNotFoundException;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;
import pl.kasprzak.dawid.myfirstwords.model.words.GetAllWordsResponse;
import pl.kasprzak.dawid.myfirstwords.model.words.GetWordResponse;
import pl.kasprzak.dawid.myfirstwords.repository.WordsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.words.GetWordsConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetWordService {

    private final WordsRepository wordsRepository;
    private final GetWordsConverter getWordsConverter;
    private final AuthorizationHelper authorizationHelper;

    public List<GetWordResponse> getByDateAchieveBefore(Long childId, LocalDate date, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        List<WordEntity> words = wordsRepository.findByChildIdAndDateAchieveBefore(childId, date);
        return words.stream()
                .map(getWordsConverter::toDto)
                .collect(Collectors.toList());
    }

    public List<GetWordResponse> getByDateAchieveAfter(Long childId, LocalDate date, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        List<WordEntity> words = wordsRepository.findByChildIdAndDateAchieveAfter(childId, date);
        return words.stream()
                .map(getWordsConverter::toDto)
                .collect(Collectors.toList());
    }

    public List<GetWordResponse> getWordsBetweenDays(Long childId, LocalDate startDate, LocalDate endDate, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        if (startDate == null || endDate == null) {
            throw new DateValidationException("Start date and end date must not be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateOrderException("Start date must be before or equal to end date");
        }
        List<WordEntity> words = wordsRepository.findByChildIdAndDateAchieveBetween(childId, startDate, endDate);
        return words.stream()
                .map(getWordsConverter::toDto)
                .collect(Collectors.toList());
    }

    public GetWordResponse getByWord(Long childId, String word, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        return wordsRepository.findByWordIgnoreCaseAndChildId(word.toLowerCase(), childId)
                .map(getWordsConverter::toDto)
                .orElseThrow(() -> new WordNotFoundException("Word not found"));
    }

    public GetAllWordsResponse getAllWords(Long childId, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        return GetAllWordsResponse.builder()
                .words(wordsRepository.findAllByChildId(childId).stream()
                        .map(getWordsConverter::toDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
