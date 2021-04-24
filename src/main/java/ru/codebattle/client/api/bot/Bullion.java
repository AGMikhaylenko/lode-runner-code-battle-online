package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardElement;

public enum Bullion {
    GREEN(0, 1, BoardElement.GREEN_GOLD),
    YELLOW(0, 2, BoardElement.YELLOW_GOLD),
    RED(0, 5, BoardElement.RED_GOLD),
    PILL_SHADOW(0, 20, BoardElement.SHADOW_PILL) {
        @Override
        public void enlargeSeries() {
            //Nothing
        }
    },
    PORTAL(0, 0, BoardElement.PORTAL) {
        @Override
        public void enlargeSeries() {
            //Nothing
        }
    };

    private int series;
    private int value;
    private BoardElement boardElement;

    Bullion(int series, int value, BoardElement boardElement) {
        this.series = series;
        this.value = value;
        this.boardElement = boardElement;
    }

    public int getSeries() {
        return series;
    }

    public int getValue() {
        return value;
    }

    public BoardElement getBoardElement() {
        return boardElement;
    }

    /**
     * Инкрементирование значения серии
     */
    public void enlargeSeries() {
        this.series++;
    }

    /**
     * Сброс значения серии до нуля
     */
    public void resetSeries() {
        this.series = 0;
    }
}
