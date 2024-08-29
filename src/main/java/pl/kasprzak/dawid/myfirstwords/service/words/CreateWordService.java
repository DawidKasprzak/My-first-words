package pl.kasprzak.dawid.myfirstwords.service.words;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;
import pl.kasprzak.dawid.myfirstwords.exception.ChildNotFoundException;
import pl.kasprzak.dawid.myfirstwords.exception.ParentNotFoundException;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordRequest;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordResponse;
import pl.kasprzak.dawid.myfirstwords.repository.WordsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.words.CreateWordConverter;

import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class CreateWordService {

    private final WordsRepository wordsRepository;
    private final CreateWordConverter createWordConverter;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Service method for adding a new word for a specific child.
     * This method validates and authorizes the parent using AuthorizationHelper, and if authorized,
     * converts the CreateWordRequest DTO to a WordEntity, sets the child for the word, saves the word entity to the
     * repository, and converts the saved entity to a CreateWordResponse DTO.
     *
     * @param childId        the ID of the child to whom the word will be added.
     * @param request        the CreateWordRequest containing the word details.
     * @return a CreateWordResponse DTO containing the details of the newly created word.
     * @throws ParentNotFoundException if the authenticated parent is not found.
     * @throws ChildNotFoundException  if the child with the given ID is not found.
     * @throws AccessDeniedException   if the authenticated parent does not have access to the child.
     */
    public CreateWordResponse addWord(Long childId, CreateWordRequest request) {
        ChildEntity child = authorizationHelper.validateAndAuthorizeChild(childId);
        WordEntity wordToSave = createWordConverter.fromDto(request);
        wordToSave.setChild(child);
        WordEntity savedEntity = wordsRepository.save(wordToSave);
        return createWordConverter.toDto(savedEntity);
    }
}
