package postman.bottler.notification.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import postman.bottler.notification.application.repository.SubscriptionCache;
import postman.bottler.notification.application.repository.SubscriptionRepository;
import postman.bottler.notification.domain.Device;
import postman.bottler.notification.domain.Subscriptions;

@Component
@RequiredArgsConstructor
public class SubscriptionReader {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionCache subscriptionCache;

    public Subscriptions lookAsideSubscription(Long userId) {
        Subscriptions cachedSubscriptions = subscriptionCache.findByUserId(userId);
        if (cachedSubscriptions.isEmpty()) {
            cachedSubscriptions = subscriptionRepository.findByUserId(userId);
        }
        return cachedSubscriptions;
    }

    public Boolean isSubscribed(Device device) {
        return subscriptionRepository.isDuplicate(device);
    }

    public Subscriptions findAll() {
        return subscriptionRepository.findAll();
    }
}
