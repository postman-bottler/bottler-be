package postman.bottler.letter.infra;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import postman.bottler.letter.domain.Keyword;
import postman.bottler.letter.infra.entity.KeywordEntity;
import postman.bottler.letter.application.repository.KeywordRepository;

@Repository
@RequiredArgsConstructor
public class KeywordRepositoryImpl implements KeywordRepository {

    private final KeywordJpaRepository keywordJpaRepository;

    @Override
    public List<Keyword> getKeywords() {
        List<KeywordEntity> keywords = keywordJpaRepository.findAll();
        return keywords.stream().map(KeywordEntity::toDomain).toList();
    }
}
