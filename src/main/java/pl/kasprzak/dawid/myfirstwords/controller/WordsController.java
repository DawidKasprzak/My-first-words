package pl.kasprzak.dawid.myfirstwords.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordRequest;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordResponse;
import pl.kasprzak.dawid.myfirstwords.model.words.GetAllWordsResponse;
import pl.kasprzak.dawid.myfirstwords.model.words.GetWordResponse;
import pl.kasprzak.dawid.myfirstwords.repository.WordsRepository;
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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{childId}")
    public CreateWordResponse addWord(@PathVariable Long childId, @Valid @RequestBody CreateWordRequest request,
                                      Authentication authentication) {
        return createWordService.addWord(childId, request, authentication);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{childId}/{wordId}")
    public void deleteWord(@PathVariable Long childId, @PathVariable Long wordId,
                           Authentication authentication) {
        deleteWordService.deleteWord(childId, wordId, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/before/{date}")
    public List<GetWordResponse> getByDateAchieveBefore(@PathVariable Long childId, @PathVariable LocalDate date,
                                                        Authentication authentication) {
        return getWordService.getByDateAchieveBefore(childId, date, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/after/{date}")
    public List<GetWordResponse> getByDateAchieveAfter(@PathVariable Long childId, @PathVariable LocalDate date,
                                                       Authentication authentication) {
        return getWordService.getByDateAchieveAfter(childId, date, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}/between")
    public List<GetWordResponse> getWordsBetweenDays(@PathVariable Long childId, @RequestParam LocalDate startDate,
                                                     @RequestParam LocalDate endDate, Authentication authentication) {
        return getWordService.getWordsBetweenDays(childId, startDate, endDate, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{childId}")
    public GetAllWordsResponse getAllWords(@PathVariable Long childId, Authentication authentication) {
        return getWordService.getAllWords(childId, authentication);
    }
}
