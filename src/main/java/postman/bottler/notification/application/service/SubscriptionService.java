package postman.bottler.notification.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import postman.bottler.notification.application.repository.SubscriptionCache;
import postman.bottler.notification.application.repository.SubscriptionRepository;
import postman.bottler.notification.domain.Subscription;
import postman.bottler.notification.application.dto.response.SubscriptionResponseDTO;
import postman.bottler.notification.domain.Device;
import postman.bottler.notification.exception.DuplicateTokenException;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionCache subscriptionCache;

    @Transactional
    public SubscriptionResponseDTO subscribe(Long userId, String token) {
        Subscription subscribe = Subscription.create(userId, token);
        if (subscriptionRepository.isDuplicate(subscribe.getDevice())) {
            throw new DuplicateTokenException();
        }
        Subscription save = subscriptionRepository.save(subscribe);
        subscriptionCache.save(subscribe.getDevice());
        return SubscriptionResponseDTO.from(save);
    }

    @Transactional
    public void unsubscribeAll(Long userId) {
        subscriptionRepository.deleteAllByUserId(userId);
        subscriptionCache.deleteAllByUserId(userId);
    }

    @Transactional
    public void unsubscribe(String token, Long userId) {
        Device device = new Device(userId, token);
        subscriptionRepository.deleteByToken(device.getToken());
        subscriptionCache.deleteDevice(device);
    }
}
