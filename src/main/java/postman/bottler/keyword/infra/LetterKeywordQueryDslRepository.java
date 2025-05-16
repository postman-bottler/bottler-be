package postman.bottler.keyword.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import postman.bottler.keyword.infra.entity.LetterKeywordEntity;
import postman.bottler.keyword.infra.entity.QLetterKeywordEntity;
import postman.bottler.letter.infra.entity.QLetterEntity;

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

        List<Long> matchedLetters = queryFactory
                .select(qLetterKeyword.letterId)
                .from(qLetterKeyword)
                .where(qLetterKeyword.keyword.in(userKeywords)
                        .and(qLetterKeyword.letterId.notIn(letterIds))
                        .and(qLetterKeyword.isDeleted.eq(false)))
                .groupBy(qLetterKeyword.letterId)
                .orderBy(qLetterKeyword.letterId.count().desc())
                .limit(limit)
                .fetch();

        if (matchedLetters.size() < limit) {
            int remaining = limit - matchedLetters.size();
            List<Long> randomLetters = getRandomLetters(remaining, letterIds);
            matchedLetters.addAll(randomLetters);
        }

        return matchedLetters;
    }

    private List<Long> getRandomLetters(int limit, List<Long> excludedLetterIds) {
        QLetterEntity qLetter = QLetterEntity.letterEntity;

        Long maxId = queryFactory
                .select(qLetter.id.max())
                .from(qLetter)
                .where(qLetter.isDeleted.isFalse())
                .fetchOne();

        if (maxId == null || maxId == 0) {
            return new ArrayList<>();
        }

        List<Long> result = new ArrayList<>();
        Random random = new Random();
        int tryCount = 0;

        while (result.size() < limit && tryCount < 5) {
            long randomId = 1L + random.nextLong(maxId); // 1 ~ maxId 사이에서 랜덤

            List<Long> partial = queryFactory
                    .select(qLetter.id)
                    .from(qLetter)
                    .where(
                            qLetter.isDeleted.isFalse(),
                            qLetter.id.goe(randomId),
                            qLetter.id.notIn(excludedLetterIds)
                    )
                    .orderBy(qLetter.id.asc()) // 랜덤 시작점 이후 순차 탐색
                    .limit(limit - result.size())
                    .fetch();

            result.addAll(partial);
            tryCount++;
        }

        return result;
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
