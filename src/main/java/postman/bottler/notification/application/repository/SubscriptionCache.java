package postman.bottler.notification.application.repository;

import postman.bottler.notification.domain.Subscriptions;
import postman.bottler.notification.domain.Device;

public interface SubscriptionCache {

    Subscriptions findByUserId(Long userId);

    void save(Device userDevice);

    void deleteAllByUserId(Long userId);

    void deleteDevice(Device device);

}
