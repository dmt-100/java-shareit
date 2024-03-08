package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ShareItTests {
    @Test
    void mainTest() {
        assertDoesNotThrow(ShareItServer::new);
        assertThat(ShareItTests.class).isNotNull();
        assertDoesNotThrow(() -> ShareItServer.main(new String[]{}));
    }
}
