package postman.bottler.notification.application.repository;

import postman.bottler.notification.domain.Subscription;
import postman.bottler.notification.domain.Subscriptions;
import postman.bottler.notification.domain.UserDevice;

public interface SubscriptionRepository {
    Subscription save(Subscription subscription);

    Subscriptions findByUserId(Long userId);

    Subscriptions findAll();

    void deleteAllByUserId(Long userId);

    void deleteByToken(String token);

    Boolean isDuplicate(UserDevice userDevice);
}
