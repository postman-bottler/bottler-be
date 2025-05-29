package postman.bottler.notification.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Subscription {
    private Long id;

    private Device device;

    public static Subscription create(Long userId, String token) {
        return Subscription.builder()
                .device(new Device(userId, token))
                .build();
    }

    public PushMessage makeMessage(NotificationType type) {
        return PushMessage.builder()
                .token(device.getToken())
                .title(type.getTitle())
                .content(type.getContent())
                .build();
    }
}
