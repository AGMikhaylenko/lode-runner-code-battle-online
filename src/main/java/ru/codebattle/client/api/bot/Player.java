package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardElement;

public class Player {
    private int xPosition;
    private int yPosition;
    private boolean isShadow;
    private int actToShadow;
    private Bullion[] series = Bullion.values();
    private BoardElement currentElement;

    private final int MAX_ACTION_OF_PILL = 50;

    public Player(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        isShadow = false;
        actToShadow = 0;
        currentElement = BoardElement.NONE;
        for (Bullion b : series)
            b.resetSeries();
    }

    public void updatePosition(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
    }

    public void update(BoardElement newElement) {
        if (--actToShadow < 0) {
            isShadow = false;
            actToShadow = 0;
        }

        switch (newElement) {
            case GREEN_GOLD:
                series[0].enlargeSeries();
                break;
            case YELLOW_GOLD:
                series[1].enlargeSeries();
                break;
            case RED_GOLD:
                series[2].enlargeSeries();
                break;
            case SHADOW_PILL:
                actToShadow = MAX_ACTION_OF_PILL;
                isShadow = true;
                break;
        }

        currentElement = newElement;
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

    public Bullion[] getSeries() {
        return series;
    }
}
