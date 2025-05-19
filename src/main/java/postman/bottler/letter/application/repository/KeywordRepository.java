package postman.bottler.letter.application.repository;

import java.util.List;
import postman.bottler.letter.domain.Keyword;

public interface KeywordRepository {

    List<Keyword> getKeywords();
}
