package postman.bottler.letter.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import postman.bottler.letter.infra.entity.LetterKeywordEntity;
import postman.bottler.letter.infra.entity.QLetterKeywordEntity;

@Repository
@RequiredArgsConstructor
public class LetterKeywordQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<LetterKeywordEntity> findKeywordsByLetterId(Long letterId) {
        QLetterKeywordEntity letterKeywordEntity = QLetterKeywordEntity.letterKeywordEntity;

        return queryFactory
                .selectFrom(letterKeywordEntity)
                .where(letterKeywordEntity.letterId.eq(letterId))
                .fetch();
    }

    public List<Long> getMatchedLetters(List<String> userKeywords, List<Long> letterIds, int limit) {
        QLetterKeywordEntity qLetterKeyword = QLetterKeywordEntity.letterKeywordEntity;

        return queryFactory
                .select(qLetterKeyword.letterId)
                .from(qLetterKeyword)
                .where(qLetterKeyword.keyword.in(userKeywords)
                        .and(qLetterKeyword.letterId.notIn(letterIds))
                        .and(qLetterKeyword.isDeleted.eq(false)))
                .groupBy(qLetterKeyword.letterId)
                .orderBy(qLetterKeyword.letterId.count().desc())
                .limit(limit)
                .fetch();
    }

    public List<String> getFrequentKeywords(List<Long> letterIds) {
        QLetterKeywordEntity letterKeyword = QLetterKeywordEntity.letterKeywordEntity;

        return queryFactory
                .select(letterKeyword.keyword)
                .from(letterKeyword)
                .where(letterKeyword.letterId.in(letterIds)
                        .and(letterKeyword.isDeleted.isFalse()))
                .groupBy(letterKeyword.keyword)
                .orderBy(letterKeyword.keyword.count().desc())
                .limit(5)
                .fetch();
    }
}
