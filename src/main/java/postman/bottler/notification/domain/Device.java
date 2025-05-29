package postman.bottler.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public class Device {

    private final Long userId;

    private final String token;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(userId, device.userId) && Objects.equals(token, device.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, token);
    }
}
