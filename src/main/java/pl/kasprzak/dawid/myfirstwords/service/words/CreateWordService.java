package pl.kasprzak.dawid.myfirstwords.service.words;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordRequest;
import pl.kasprzak.dawid.myfirstwords.model.words.CreateWordResponse;
import pl.kasprzak.dawid.myfirstwords.repository.WordsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;
import pl.kasprzak.dawid.myfirstwords.service.converters.words.CreateWordConverter;

@Service
@RequiredArgsConstructor
public class CreateWordService {

    private final WordsRepository wordsRepository;
    private final CreateWordConverter createWordConverter;
    private final AuthorizationHelper authorizationHelper;


    public CreateWordResponse addWord(Long childId, CreateWordRequest request, Authentication authentication) {
        ChildEntity child = authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        WordEntity wordToSave = createWordConverter.fromDto(request);
        wordToSave.setChild(child);
        WordEntity savedEntity = wordsRepository.save(wordToSave);
        return createWordConverter.toDto(savedEntity);
    }
}
