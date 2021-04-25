package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardElement;

/**
 * Класс, описывающий игрока на игровом поле
 */
public class Player extends Hero{
    private int actToShadow; //Остаток действия таблетки тени
    private BoardElement currentElement; //Элемент, находящийся "под игроком"

    private final int MAX_ACTION_OF_PILL = 30; //Максимальное значение действия таблетки тени

    public Player(int xPosition, int yPosition) {
        super(xPosition, yPosition);
        actToShadow = 0;
        currentElement = BoardElement.HERO_DIE;
    }

    public void updatePosition(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
    }

    /**
     * Обновление значений полей
     * @param nextElement Элемент, находящийся первым по направлению движения
     */
    public void update(BoardElement nextElement) {
        if (--actToShadow <= 0) {
            isShadow = false;
            actToShadow = 0;
        }

        switch (nextElement) {
            case GREEN_GOLD:
                GoodElements.GREEN.enlargeSeries();
                break;
            case YELLOW_GOLD:
                GoodElements.YELLOW.enlargeSeries();
                break;
            case RED_GOLD:
                GoodElements.RED.enlargeSeries();
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

    public int getActToShadow() {
        return actToShadow;
    }
}
