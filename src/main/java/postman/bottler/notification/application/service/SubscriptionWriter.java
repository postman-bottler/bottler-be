package postman.bottler.notification.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import postman.bottler.notification.application.repository.SubscriptionCache;
import postman.bottler.notification.application.repository.SubscriptionRepository;
import postman.bottler.notification.domain.Device;
import postman.bottler.notification.domain.Subscription;

@Component
@RequiredArgsConstructor
public class SubscriptionWriter {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionCache subscriptionCache;

    public Subscription writeThrough(Subscription subscription) {
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        subscriptionCache.save(savedSubscription.getDevice());
        return savedSubscription;
    }

    public void deleteAll(Long userId) {
        subscriptionRepository.deleteAllByUserId(userId);
        subscriptionCache.deleteAllByUserId(userId);
    }

    public void deleteDevice(Device device) {
        subscriptionRepository.deleteByToken(device.getToken());
        subscriptionCache.deleteDevice(device);
    }
}
