package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardElement;

public class Hero {
    protected int xPosition;
    protected int yPosition;
    protected boolean isShadow;

    public Hero(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        isShadow = false;
    }

    public Hero(int xPosition, int yPosition, BoardElement element) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        isShadow = false;
        for (BoardElement be : Helper.OTHER_HERO_SHADOW)
            if (be == element) {
                isShadow = true;
                break;
            }
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public boolean isShadow() {
        return isShadow;
    }
}
