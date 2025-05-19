package postman.bottler.letter.infra;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import postman.bottler.letter.application.repository.RedisLetterRepository;
import postman.bottler.letter.utiil.RedisLetterKeyUtil;
import postman.bottler.reply.application.dto.ReplyType;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisLetterRepositoryImpl implements RedisLetterRepository {

    private final RedisTemplate<String, List<Long>> redisTemplate;
    private final RedisTemplate<String, Object> redisTemplateForReply;

    @Value("${recommendation.limit.active-recommendations}")
    private int maxRecommendations;

    @Value("${recommendation.saved-replies}")
    private int redisSavedReply;

    @Override
    public void saveTempRecommendations(Long userId, List<Long> recommendations) {
        redisTemplate.opsForValue().set(getTempRecommendationKey(userId), recommendations);
    }

    @Override
    public void saveDeveloperLetter(Long userId, List<Long> recommendations) {
        redisTemplate.opsForValue().set(getActiveRecommendationKey(userId), recommendations);
    }

    @Override
    public void saveReply(Long receiverId, Long letterId, String labelUrl) {
        String replyKey = getReplyKey(receiverId);
        String replyValue = getReplyValue(letterId, labelUrl);

        Long size = redisTemplateForReply.opsForList().size(replyKey);
        if (size != null && size >= redisSavedReply) {
            redisTemplateForReply.opsForList().rightPop(replyKey);
        }

        if (!Objects.requireNonNull(redisTemplateForReply.opsForList().range(replyKey, 0, -1)).contains(replyValue)) {
            redisTemplateForReply.opsForList().leftPush(replyKey, replyValue);
        }
    }

    @Override
    public List<Long> fetchActiveRecommendations(Long userId) {
        return fetchRecommendations(getActiveRecommendationKey(userId));
    }

    @Override
    public List<Long> fetchTempRecommendations(Long userId) {
        return fetchRecommendations(getTempRecommendationKey(userId));
    }

    @Override
    public void updateActiveRecommendations(Long userId, Long letterId) {
        List<Long> activeRecommendations = fetchActiveRecommendations(userId);

        if (activeRecommendations.size() - maxRecommendations >= 0) {
            activeRecommendations.subList(0, activeRecommendations.size() - maxRecommendations).clear();
        }
        activeRecommendations.add(letterId);

        redisTemplate.opsForValue().set(getActiveRecommendationKey(userId), activeRecommendations);
        redisTemplate.delete(getTempRecommendationKey(userId));
    }

    @Override
    public void deleteRecentReply(Long receiverId, Long replyLetterId, String label) {
        String key = getReplyKey(receiverId);
        String value = getReplyValue(replyLetterId, label);

        redisTemplate.opsForList().remove(key, 1, value);
    }

    private String getTempRecommendationKey(Long userId) {
        return RedisLetterKeyUtil.getTempRecommendationKey(userId);
    }

    private String getActiveRecommendationKey(Long userId) {
        return RedisLetterKeyUtil.getActiveRecommendationKey(userId);
    }

    private String getReplyKey(Long receiverId) {
        return RedisLetterKeyUtil.getReplyKey(receiverId);
    }

    private String getReplyValue(Long letterId, String labelUrl) {
        return ReplyType.KEYWORD + ":" + letterId + ":" + labelUrl;
    }

    private List<Long> fetchRecommendations(String key) {
        List<Long> recommendations = redisTemplate.opsForValue().get(key);
        if (isExistRecommendations(recommendations)) {
            return recommendations;
        } else {
            return new ArrayList<>();
        }
    }

    private boolean isExistRecommendations(List<Long> recommendations) {
        return !(recommendations == null || recommendations.isEmpty());
    }
}
