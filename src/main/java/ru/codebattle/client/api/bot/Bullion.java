package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardElement;

public enum Bullion {
    GREEN(0, 1, BoardElement.GREEN_GOLD),
    YELLOW(0, 3, BoardElement.YELLOW_GOLD),
    RED(0, 5, BoardElement.RED_GOLD);

    private int series;
    private int value;
    private BoardElement boardElement;

    Bullion(int series, int value, BoardElement element) {
        this.series = series;
        this.value = value;
        this.boardElement = element;
    }

    public int getSeries() {
        return series;
    }

    public void enlargeSeries() {
        this.series++;
    }

    public int getValue() {
        return value;
    }

    public void resetSeries() {
        this.series = 0;
    }

    public BoardElement getBoardElement() {
        return boardElement;
    }
}
