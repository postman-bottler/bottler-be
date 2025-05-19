package postman.bottler.letter.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import postman.bottler.letter.application.dto.RecommendedLetterDTO;
import postman.bottler.letter.application.repository.RecommendedLetterRepository;
import postman.bottler.letter.application.repository.RedisLetterRepository;
import postman.bottler.letter.application.dto.LetterBoxDTO;
import postman.bottler.letter.application.repository.LetterBoxRepository;
import postman.bottler.letter.application.repository.LetterRepository;
import postman.bottler.letter.domain.BoxType;
import postman.bottler.letter.domain.LetterType;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLetterService {

    private final LetterRepository letterRepository;
    private final RedisLetterRepository redisLetterRepository;
    private final LetterBoxRepository letterBoxRepository;
    private final RecommendedLetterRepository recommendedLetterRepository;

    public void saveTempRecommendations(Long userId, List<Long> recommendations) {
        redisLetterRepository.saveTempRecommendations(userId, recommendations);
    }

    public void saveDeveloperLetter(Long userId, List<Long> recommendations) {
        redisLetterRepository.saveDeveloperLetter(userId, recommendations);
    }

    public void saveReplyToRedis(Long letterId, String labelUrl, Long receiverId) {
        redisLetterRepository.saveReply(receiverId, letterId, labelUrl);
    }

    public List<Long> fetchActiveRecommendations(Long userId) {
        return redisLetterRepository.fetchActiveRecommendations(userId);
    }

    public List<Long> fetchTempRecommendations(Long userId) {
        return redisLetterRepository.fetchTempRecommendations(userId);
    }

    @Transactional
    public Optional<Long> updateRecommendationsFromTemp(Long userId) {
        List<Long> tempRecommendations = fetchTempRecommendations(userId);

        Optional<Long> recommendId = findFirstValidLetter(tempRecommendations);
        if (recommendId.isEmpty()) {
            log.info("userId={}에 대한 유효한 추천이 없음. 추천을 건너뜁니다.", userId);
            return Optional.empty();
        }

        redisLetterRepository.updateActiveRecommendations(userId, recommendId.get());

        saveLetterToBox(userId, recommendId.get());
        saveLetterToRecommendedLetter(userId, recommendId.get());

        return recommendId;
    }

    public void deleteRecentReply(Long receiverId, Long replyLetterId, String label) {
        redisLetterRepository.deleteRecentReply(receiverId, replyLetterId, label);
    }

    private Optional<Long> findFirstValidLetter(List<Long> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            return Optional.empty();
        }

        List<Long> validLetters = recommendations.stream().filter(this::isValidLetter).toList();

        if (validLetters.isEmpty()) {
            log.warn("추천할 편지가 모두 삭제됨. 새로운 추천이 필요함.");
            return Optional.empty();
        }

        return Optional.of(validLetters.get(0));
    }

    private boolean isValidLetter(Long letterId) {
        return letterRepository.existsById(letterId);
    }

    private void saveLetterToBox(Long userId, Long letterId) {
        LetterBoxDTO letterBoxDTO = LetterBoxDTO.of(userId, letterId, LetterType.LETTER, BoxType.RECEIVE,
                LocalDateTime.now());
        letterBoxRepository.save(letterBoxDTO.toDomain());
    }

    private void saveLetterToRecommendedLetter(Long userId, Long letterId) {
        RecommendedLetterDTO recommendedLetterDTO = RecommendedLetterDTO.of(userId, letterId);
        recommendedLetterRepository.saveRecommendedLetter(recommendedLetterDTO.toDomain());
    }
}
