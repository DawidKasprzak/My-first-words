package pl.kasprzak.dawid.myfirstwords.service.words;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
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
     * This method validates and authorizes the parent or admin using AuthorizationHelper.
     * If the authenticated user is a parent, the `parentID` parameter can be null,
     * and the authorization will be based on the current user's session.
     * If the authenticated user is an admin, the `parentID` parameter must be provided
     * to specify the parent associated with the child.
     * Once authorized, the method finds the word associated with the given child ID and word ID,
     * and deletes it from the repository.
     *
     * @param childId  the ID of the child to whom the word belongs.
     * @param wordId   the ID of the word to be deleted.
     * @param parentID the ID of the parent, required if the authenticated user is an administrator.
     * @throws IllegalArgumentException if the authenticated user is an admin and the parentID is null.
     * @throws ParentNotFoundException  if the specified parent (for admin) or authenticated parent (for regular user) is not found.
     * @throws ChildNotFoundException   if the child with the given ID is not found.
     * @throws AccessDeniedException    if the authenticated parent does not have access to the child.
     * @throws WordNotFoundException    if the word with the given ID is not found for the specified child.
     */
    public void deleteWord(Long childId, Long wordId, Long parentID) {
        authorizationHelper.validateAndAuthorizeForAdminOrParent(childId, parentID);
        Optional<WordEntity> word = wordsRepository.findByChildIdAndId(childId, wordId);
        WordEntity wordEntity = word.orElseThrow(() -> new WordNotFoundException("Word not found"));
        wordsRepository.delete(wordEntity);
    }
}
