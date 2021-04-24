package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardElement;

public class Player extends Hero{
    private int actToShadow;
    private BoardElement currentElement;
    private final int MAX_ACTION_OF_PILL = 60;

    public Player(int xPosition, int yPosition) {
        super(xPosition, yPosition);
        actToShadow = 0;
        currentElement = BoardElement.HERO_DIE;
    }

    public void updatePosition(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
    }

    public void update(BoardElement nextElement) {
        if (--actToShadow < 0) {
            isShadow = false;
            actToShadow = 0;
        }

        switch (nextElement) {
            case GREEN_GOLD:
                Bullion.GREEN.enlargeSeries();
                break;
            case YELLOW_GOLD:
                Bullion.YELLOW.enlargeSeries();
                break;
            case RED_GOLD:
                Bullion.RED.enlargeSeries();
                break;
            case SHADOW_PILL:
                actToShadow = MAX_ACTION_OF_PILL;
                isShadow = true;
                break;
        }

        currentElement = nextElement;
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public BoardElement getCurrentElement() {
        return currentElement;
    }

}
