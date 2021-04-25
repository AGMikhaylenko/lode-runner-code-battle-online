package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardPoint;

public class Goal {
    private BoardPoint goalPoint;
    private int distance;
    private Direction firstStepToGoal;
    private BoardPoint secondPointInWay;

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
