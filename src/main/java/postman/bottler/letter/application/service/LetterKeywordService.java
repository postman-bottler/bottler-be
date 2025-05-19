package postman.bottler.letter.application.service;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import postman.bottler.letter.application.dto.response.FrequentKeywordsDTO;
import postman.bottler.letter.application.repository.LetterKeywordRepository;
import postman.bottler.letter.domain.LetterKeyword;
import postman.bottler.letter.application.repository.LetterRepository;
import postman.bottler.letter.domain.Letter;

@Slf4j
@Service
@RequiredArgsConstructor
public class LetterKeywordService {

    private final LetterKeywordRepository letterKeywordRepository;
    private final LetterRepository letterRepository;

    @Transactional
    public List<LetterKeyword> createLetterKeywords(Long letterId, List<String> keywords) {
        log.info("편지 키워드 생성 요청: letterId={}, 키워드 개수={}", letterId, keywords.size());

        List<LetterKeyword> letterKeywords = keywords.stream().map(keyword -> LetterKeyword.from(letterId, keyword))
                .toList();

        return letterKeywordRepository.saveAll(letterKeywords);
    }

    @Transactional(readOnly = true)
    public List<LetterKeyword> getKeywords(Long letterId) {
        return letterKeywordRepository.getKeywordsByLetterId(letterId);
    }

    @Transactional
    public void markKeywordsAsDeleted(List<Long> letterIds) {
        log.info("편지 키워드 삭제 요청: letterIds={}", letterIds);
        letterKeywordRepository.markKeywordsAsDeleted(letterIds);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public FrequentKeywordsDTO getTopFrequentKeywords(Long userId) {
        List<Long> letterIds = letterRepository.findAllByUserId(userId).stream().map(Letter::getId).toList();
        if (letterIds.isEmpty()) {
            log.warn("사용자의 편지 ID가 없음: userId={}", userId);
            return FrequentKeywordsDTO.from(Collections.emptyList());
        }

        List<String> frequentKeywords = letterKeywordRepository.getFrequentKeywords(letterIds);
        return FrequentKeywordsDTO.from(frequentKeywords);
    }
}
