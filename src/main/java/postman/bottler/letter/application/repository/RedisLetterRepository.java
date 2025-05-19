package postman.bottler.letter.application.repository;

import java.util.List;

public interface RedisLetterRepository {
    void saveTempRecommendations(Long userId, List<Long> recommendations);
    void saveDeveloperLetter(Long userId, List<Long> recommendations);
    void saveReply(Long receiverId, Long letterId, String labelUrl);
    List<Long> fetchActiveRecommendations(Long userId);
    List<Long> fetchTempRecommendations(Long userId);
    void updateActiveRecommendations(Long userId, Long letterId);
    void deleteRecentReply(Long receiverId, Long letterId, String labelUrl);
}
