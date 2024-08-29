package pl.kasprzak.dawid.myfirstwords.service.words;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.DateValidationException;
import pl.kasprzak.dawid.myfirstwords.exception.InvalidDateOrderException;
import pl.kasprzak.dawid.myfirstwords.exception.WordNotFoundException;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;
import pl.kasprzak.dawid.myfirstwords.model.words.GetAllWordsResponse;
import pl.kasprzak.dawid.myfirstwords.model.words.GetWordResponse;
import pl.kasprzak.dawid.myfirstwords.repository.WordsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.words.GetWordsConverter;

import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetWordService {

    private final WordsRepository wordsRepository;
    private final GetWordsConverter getWordsConverter;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Service method for retrieving words for a child that were achieved before the given date.
     * This method validates and authorizes the parent using the AuthorizationHelper,
     * and fetches words achieved before the specified date.
     *
     * @param childId        the ID of the child whose words are to be retrieved.
     * @param date           the date before which words were achieved.
     * @return a list of GetWordResponse DTOs containing the words achieved before the given date.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     */
    public List<GetWordResponse> getByDateAchieveBefore(Long childId, LocalDate date) {
        authorizationHelper.validateAndAuthorizeChild(childId);
        List<WordEntity> words = wordsRepository.findByChildIdAndDateAchieveBefore(childId, date);
        return words.stream()
                .map(getWordsConverter::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Service method for retrieving words for a child that were achieved after the given date.
     * This method validates and authorizes the parent using the AuthorizationHelper,
     * and fetches words achieved after the specified date.
     *
     * @param childId        the ID of the child whose words are to be retrieved.
     * @param date           the date after which words were achieved.
     * @return a list of GetWordResponse DTOs containing the words achieved after the given date.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     */
    public List<GetWordResponse> getByDateAchieveAfter(Long childId, LocalDate date) {
        authorizationHelper.validateAndAuthorizeChild(childId);
        List<WordEntity> words = wordsRepository.findByChildIdAndDateAchieveAfter(childId, date);
        return words.stream()
                .map(getWordsConverter::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Service method for retrieving words for a child that were achieved between the given date.
     * This method validates and authorizes the parent using the AuthorizationHelper,
     * and fetches words achieved between the specified date.
     *
     * @param childId        the ID of the child whose words are to be retrieved.
     * @param startDate      the start date of the range.
     * @param endDate        the end date of the range.
     * @return a list of GetWordResponse DTOs containing the words achieved between the given dates.
     * @throws ParentNotFoundException   if the authenticated parent is not found.
     * @throws ChildNotFoundException    if the child with the given ID is not found.
     * @throws AccessDeniedException     if the authenticated parent does not have access to the child.
     * @throws DateValidationException   if either start date or end date is null.
     * @throws InvalidDateOrderException if the start date is after the end date.
     */
    public List<GetWordResponse> getWordsBetweenDays(Long childId, LocalDate startDate, LocalDate endDate) {
        authorizationHelper.validateAndAuthorizeChild(childId);
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

    /**
     * Service method for retrieving word for a child by the given word.
     * This method validates and authorizes the parent using the AuthorizationHelper,
     * and fetches the word for the specified child.
     *
     * @param childId        the ID of the child whose word is to be retrieved.
     * @param word           the word to be retrieved.
     * @return a GetWordResponse DTO containing the word details.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     * @throws WordNotFoundException   if the word with the given ID is not found for the specified child.
     */
    public GetWordResponse getByWord(Long childId, String word) {
        authorizationHelper.validateAndAuthorizeChild(childId);
        return wordsRepository.findByWordIgnoreCaseAndChildId(word.toLowerCase(), childId)
                .map(getWordsConverter::toDto)
                .orElseThrow(() -> new WordNotFoundException("Word not found"));
    }

    /**
     * Service method for retrieving all words for a child.
     * This method validates and authorizes the parent using the AuthorizationHelper,
     * and fetches all words for the specified child.
     *
     * @param childId        the ID of the child whose words are to be retrieved.
     * @return a GetAllWordsResponse DTO containing a list of all words for the child.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     */
    public GetAllWordsResponse getAllWords(Long childId) {
        authorizationHelper.validateAndAuthorizeChild(childId);
        return GetAllWordsResponse.builder()
                .words(wordsRepository.findAllByChildId(childId).stream()
                        .map(getWordsConverter::toDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
