package postman.bottler.notification.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Subscription {
    private Long id;

    private UserDevice userDevice;

    public static Subscription create(Long userId, String token) {
        return Subscription.builder()
                .userDevice(new UserDevice(userId, token))
                .build();
    }

    public PushMessage makeMessage(NotificationType type) {
        return PushMessage.builder()
                .token(userDevice.getToken())
                .title(type.getTitle())
                .content(type.getContent())
                .build();
    }
}
