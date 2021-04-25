package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardPoint;

/**
 * Класс, описывающий целевой путь, по которому движется бот
 */
public class Goal {
    private BoardPoint goalPoint; //Конечная точка
    private int distance; //Дистанция до конечной точки
    private Direction firstStepToGoal; //Направление первого шага в точку
    private BoardPoint secondPointInWay; //Вторая точка на пути (необходима для "копания" стен)

    public Goal(BoardPoint goalPoint, int distance, Direction firstStepToGoal, BoardPoint secondPointInWay) {
        this.goalPoint = goalPoint;
        this.distance = distance;
        this.firstStepToGoal = firstStepToGoal;
        this.secondPointInWay = secondPointInWay;
    }

    public BoardPoint getGoalPoint() {
        return goalPoint;
    }

    public int getDistance() {
        return distance;
    }

    public Direction getFirstStepToGoal() {
        return firstStepToGoal;
    }

    public BoardPoint getSecondPointInWay() {
        return secondPointInWay;
    }
}
