package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.LoderunnerAction;

/**
 * Описание направлений движения бота
 */
public enum Direction {
    RIGHT(LoderunnerAction.GO_RIGHT),
    LEFT(LoderunnerAction.GO_LEFT),
    DOWN(LoderunnerAction.GO_DOWN),
    UP(LoderunnerAction.GO_UP);

    private LoderunnerAction action;//Действие, соответствующее направлению
    private Direction(LoderunnerAction action){
        this.action = action;
    }

    public LoderunnerAction getAction() {
        return action;
    }
}
