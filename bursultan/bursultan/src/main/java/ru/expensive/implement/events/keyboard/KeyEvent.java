package ru.expensive.implement.events.keyboard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.expensive.api.event.events.Event;

@Getter
@RequiredArgsConstructor
public class KeyEvent implements Event {
    private final int key;
    private final int action;
    private final boolean isMouse;

    public KeyEvent(int key, int action) {
        this(key, action, false);
    }

    public boolean isKeyDown(int key) {
        return this.key == key && action == 1;
    }
}