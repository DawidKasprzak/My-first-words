package pl.kasprzak.dawid.myfirstwords.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.kasprzak.dawid.myfirstwords.exception.*;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordRequest;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordResponse;
import pl.kasprzak.dawid.myfirstwords.model.words.GetAllWordsResponse;
import pl.kasprzak.dawid.myfirstwords.model.words.GetWordResponse;
import pl.kasprzak.dawid.myfirstwords.service.words.CreateWordService;
import pl.kasprzak.dawid.myfirstwords.service.words.DeleteWordService;
import pl.kasprzak.dawid.myfirstwords.service.words.GetWordService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/words")
public class WordsController {

    private final CreateWordService createWordService;
    private final DeleteWordService deleteWordService;
    private final GetWordService getWordService;

    /**
     * Adds a new word for a specific child.
     * This endpoint allows an authenticated parent to add a new word to their child's vocabulary.
     *
     * @param childId        the ID of the child to whom the word belongs.
     * @param request        the CreateWordRequest object containing the details of the word.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a CreateWordResponse containing the details of the newly added word.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException  if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException   if the parent is not authorized to view the child (HTTP 403).
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{childId}")
    public CreateWordResponse addWord(@PathVariable Long childId, @Valid @RequestBody CreateWordRequest request,
                                      Authentication authentication) {
        return createWordService.addWord(childId, request, authentication);
    }

    /**
     * Deletes a word by the given ID for a specific child.
     * This endpoint allows an authenticated parent to delete a word from their child's vocabulary.
     *
     * @param childId        the ID of the child whose word is to be deleted.
     * @param wordId         the ID of the word to be deleted.
     * @param authentication the authentication object containing the parent's credentials.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException  if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException   if the parent is not authorized to view the child (HTTP 403).
     * @throws WordNotFoundException   if the word with the given ID is not found (HTTP 404).
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{childId}/{wordId}")
    public void deleteWord(@PathVariable Long childId, @PathVariable Long wordId,
                           Authentication authentication) {
        deleteWordService.deleteWord(childId, wordId, authentication);
    }

    /**
     * Retrieves all words added before the specified date for a specific child.
     * This endpoint allows an authenticated parent to fetch words learned before a certain date.
     *
     * @param childId        the ID of the child whose words are to be retrieved.
     * @param date           the date before which the words were added.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a list of GetWordResponse containing the details of the words.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException  if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException   if the parent is not authorized to view the child (HTTP 403).
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/before/{date}")
    public List<GetWordResponse> getByDateAchieveBefore(@PathVariable Long childId, @PathVariable LocalDate date,
                                                        Authentication authentication) {
        return getWordService.getByDateAchieveBefore(childId, date, authentication);
    }

    /**
     * Retrieves all words added after the specified date for a specific child.
     * This endpoint allows an authenticated parent to fetch words learned after a certain date.
     *
     * @param childId        the ID of the child whose words are to be retrieved.
     * @param date           the date after which the words were added.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a list of GetWordResponse containing the details of the words.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException  if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException   if the parent is not authorized to view the child (HTTP 403).
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/after/{date}")
    public List<GetWordResponse> getByDateAchieveAfter(@PathVariable Long childId, @PathVariable LocalDate date,
                                                       Authentication authentication) {
        return getWordService.getByDateAchieveAfter(childId, date, authentication);
    }

    /**
     * Retrieves all words added between the specified start and end dates for a specific child.
     * This endpoint allows an authenticated parent to fetch words learned within a specific date range.
     *
     * @param childId        the ID of the child whose words are to be retrieved.
     * @param startDate      the start date of the range.
     * @param endDate        the end date of the range.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a list of GetWordResponse containing the details of the words.
     * @throws ParentNotFoundException   if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException    if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException     if the parent is not authorized to view the child (HTTP 403).
     * @throws DateValidationException   if the start date or end date is invalid (HTTP 400).
     * @throws InvalidDateOrderException if the start date is after the end date (HTTP 400).
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/between")
    public List<GetWordResponse> getWordsBetweenDays(@PathVariable Long childId, @RequestParam LocalDate startDate,
                                                     @RequestParam LocalDate endDate, Authentication authentication) {
        return getWordService.getWordsBetweenDays(childId, startDate, endDate, authentication);
    }

    /**
     * Retrieves all words for a specific child.
     * This endpoint allows an authenticated parent to fetch all words associated with their child.
     *
     * @param childId        the ID of the child whose words are to be retrieved.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a GetAllWordsResponse containing the details of all words.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException  if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException   if the parent is not authorized to view the child (HTTP 403).
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}")
    public GetAllWordsResponse getAllWords(@PathVariable Long childId, Authentication authentication) {
        return getWordService.getAllWords(childId, authentication);
    }

    /**
     * Retrieves a specific word for a child based on the word content.
     * This endpoint allows an authenticated parent to fetch a specific word learned by their child.
     *
     * @param childId        the ID of the child whose word is to be retrieved.
     * @param word           the word to be retrieved.
     * @param authentication the authentication object containing the parent's credentials.
     * @return a GetWordResponse containing the details of the word.
     * @throws ParentNotFoundException if the authenticated parent is not found (HTTP 404).
     * @throws ChildNotFoundException  if the child with the given ID is not found (HTTP 404).
     * @throws AccessDeniedException   if the parent is not authorized to view the child (HTTP 403).
     * @throws WordNotFoundException   if the word is not found (HTTP 404).
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "{childId}/word")
    public GetWordResponse getWordByChildIdAndWord(@PathVariable Long childId, @RequestParam String word, Authentication authentication) {
        return getWordService.getByWord(childId, word, authentication);
    }
}
