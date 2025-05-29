package postman.bottler.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserDevice {

    private final Long userId;

    private final String token;

}
