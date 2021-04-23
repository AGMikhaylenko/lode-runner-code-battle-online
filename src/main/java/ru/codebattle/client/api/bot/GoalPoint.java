package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardPoint;

import java.awt.*;

public class GoalPoint {
    private BoardPoint GoalPoint;
    private int distance;
    private Direction firstStepToGoal;

    public GoalPoint(BoardPoint goalPoint, int distance, Direction firstStepToGoal) {
        GoalPoint = goalPoint;
        this.distance = distance;
        this.firstStepToGoal = firstStepToGoal;
    }

    public BoardPoint getGoalPoint() {
        return GoalPoint;
    }

    public int getDistance() {
        return distance;
    }

    public Direction getFirstStepToGoal() {
        return firstStepToGoal;
    }
}
