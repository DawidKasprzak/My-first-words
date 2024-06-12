package pl.kasprzak.dawid.myfirstwords.service.words;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kasprzak.dawid.myfirstwords.exception.WordNotFoundException;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;
import pl.kasprzak.dawid.myfirstwords.repository.WordsRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.WordEntity;

@Service
@RequiredArgsConstructor
public class DeleteWordService {

    private final WordsRepository wordsRepository;
    private final AuthorizationHelper authorizationHelper;

    public void deleteWord(Long childId, Long wordId, Authentication authentication){
        authorizationHelper.validateAndAuthorizeChild(childId, authentication);
        WordEntity word = wordsRepository.findById(wordId)
                .orElseThrow(()-> new WordNotFoundException("Word not found"));
        if (!word.getChild().getId().equals(childId)){
            throw new AccessDeniedException("This word does not belong to this child");
        }
        wordsRepository.delete(word);
    }
}
