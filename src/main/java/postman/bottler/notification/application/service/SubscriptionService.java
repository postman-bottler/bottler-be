package postman.bottler.notification.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import postman.bottler.notification.domain.Subscription;
import postman.bottler.notification.application.dto.response.SubscriptionResponseDTO;
import postman.bottler.notification.domain.Device;
import postman.bottler.notification.exception.DuplicateTokenException;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionReader subscriptionReader;
    private final SubscriptionWriter subscriptionWriter;

    @Transactional
    public SubscriptionResponseDTO subscribe(Long userId, String token) {
        Subscription subscribe = Subscription.create(userId, token);
        if (subscriptionReader.isSubscribed(subscribe.getDevice())) {
            throw new DuplicateTokenException();
        }
        Subscription save = subscriptionWriter.writeThrough(subscribe);
        return SubscriptionResponseDTO.from(save);
    }

    @Transactional
    public void unsubscribeAll(Long userId) {
        subscriptionWriter.deleteAll(userId);
    }

    @Transactional
    public void unsubscribe(String token, Long userId) {
        Device device = new Device(userId, token);
        subscriptionWriter.deleteDevice(device);
    }
}
