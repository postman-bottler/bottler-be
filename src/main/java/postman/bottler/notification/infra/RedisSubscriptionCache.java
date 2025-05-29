package postman.bottler.notification.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import postman.bottler.notification.application.repository.SubscriptionCache;
import postman.bottler.notification.domain.Subscription;
import postman.bottler.notification.domain.Subscriptions;
import postman.bottler.notification.domain.Device;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisSubscriptionCache implements SubscriptionCache {

    private final static String SUBSCRIPTION_KEY = "subscription:";
    private final RedisTemplate<String, String> subscriptionTemplate;

    @Override
    public Subscriptions findByUserId(Long userId) {
        List<Subscription> subscriptions = new ArrayList<>();
        String key = getKey(userId);
        List<String> tokens = subscriptionTemplate.opsForList().range(key, 0, -1);
        for (String token : tokens) {
            subscriptions.add(Subscription.create(userId, token));
        }
        return Subscriptions.from(subscriptions);
    }

    @Override
    public void save(Device device) {
        String key = getKey(device.getUserId());
        subscriptionTemplate.opsForList().rightPush(key, device.getToken());
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        subscriptionTemplate.delete(getKey(userId));
    }

    @Override
    public void deleteDevice(Device device) {
        String key = getKey(device.getUserId());
        subscriptionTemplate.opsForList().remove(key, 0, device.getToken());
    }

    private String getKey(Long userId) {
        return SUBSCRIPTION_KEY + userId;
    }
}
