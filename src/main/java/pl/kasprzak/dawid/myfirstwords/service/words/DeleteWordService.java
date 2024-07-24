package pl.kasprzak.dawid.myfirstwords.service.words;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.WordNotFoundException;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;
import pl.kasprzak.dawid.myfirstwords.repository.WordsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeleteWordService {

    private final WordsRepository wordsRepository;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Service method for deleting a word identified by the given word ID for a specific child.
     * This method validates and authorizes the parent using AuthorizationHelper, and if authorized,
     * finds the word associated with the given child ID and word ID, and deletes it from the repository.
     *
     * @param childId        the ID of the child to whom the word belongs.
     * @param wordId         the ID of the word to be deleted.
     * @param authentication the authentication object containing the parent's credentials.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     * @throws WordNotFoundException   if the word with the given ID is not found for the specified child.
     */
    public void deleteWord(Long childId, Long wordId, Authentication authentication) {
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        Optional<WordEntity> word = wordsRepository.findByChildIdAndId(childId, wordId);
        WordEntity wordEntity = word.orElseThrow(() -> new WordNotFoundException("Word not found"));
        wordsRepository.delete(wordEntity);
    }
}
