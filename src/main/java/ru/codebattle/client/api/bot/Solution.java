package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardElement;
import ru.codebattle.client.api.GameBoard;
import ru.codebattle.client.api.LoderunnerAction;

public class Solution {
    private Player player;
    private Helper helper;

    private int pitLeftStep; //Сверлим слева от стены
    private int pitRightStep; //Сверлим справа от стены

    private LoderunnerAction[] pitLeftActions = {LoderunnerAction.GO_LEFT, LoderunnerAction.DRILL_RIGHT, LoderunnerAction.GO_RIGHT};
    private LoderunnerAction[] pitRightActions = {LoderunnerAction.GO_RIGHT, LoderunnerAction.DRILL_LEFT, LoderunnerAction.GO_LEFT};

    public Solution(GameBoard board) {
        player = new Player(board.getMyPosition().getX(), board.getMyPosition().getY());
        helper = new Helper(board, player);
        pitLeftStep = pitRightStep = 0;

        for (Bullion bullion : Bullion.values())
            bullion.resetSeries();
    }

    public LoderunnerAction getNextStepTwo(GameBoard board) {
        player.updatePosition(board.getMyPosition().getX(), board.getMyPosition().getY());
        helper.setBoard(board, player);

        GoalPoint goal = choiceGoal();

        player.update(helper.getNearestElement(player.getXPosition(), player.getYPosition(), goal.getFirstStepToGoal()));
        if (goal.getDistance() == 0)
            return helper.wherePit(player.getXPosition(), player.getYPosition());
        else
            return goal.getFirstStepToGoal().getAction();
    }


    /**
     * Выбор целевой точки для дальнейшего движения
     *
     * @return Объект целевой точки
     */
    private GoalPoint choiceGoal() {
        GoalPoint goal;

        GoalPoint gGreen = helper.getNearestElement(player.getXPosition(), player.getYPosition(), false, BoardElement.GREEN_GOLD);
        GoalPoint gYellow = helper.getNearestElement(player.getXPosition(), player.getYPosition(), false, BoardElement.YELLOW_GOLD);
        GoalPoint gRed = helper.getNearestElement(player.getXPosition(), player.getYPosition(), false, BoardElement.RED_GOLD);

        GoalPoint gShadow = helper.getNearestElement(player.getXPosition(), player.getYPosition(), false, BoardElement.SHADOW_PILL);//<----------------
        GoalPoint gPortal = helper.getNearestElement(player.getXPosition(), player.getYPosition(), false, BoardElement.PORTAL);//<----------------

        int wGreen = gGreen.getDistance() == 0 || helper.isDeadEnd(gGreen.getGoalPoint()) || !helper.nextStepIsSafe(player, gGreen.getFirstStepToGoal()) ?
                Integer.MAX_VALUE : gGreen.getDistance() - Bullion.GREEN.getSeries() - Bullion.GREEN.getValue();
        int wYellow = gYellow.getDistance() == 0 || helper.isDeadEnd(gYellow.getGoalPoint())|| !helper.nextStepIsSafe(player, gYellow.getFirstStepToGoal()) ?
                Integer.MAX_VALUE : gYellow.getDistance() - Bullion.YELLOW.getSeries() - Bullion.YELLOW.getValue();
        int wRed = gRed.getDistance() == 0 || helper.isDeadEnd(gRed.getGoalPoint())|| !helper.nextStepIsSafe(player, gRed.getFirstStepToGoal()) ?
                Integer.MAX_VALUE : gRed.getDistance() - Bullion.RED.getSeries() - Bullion.RED.getValue();
        int wShadow = gShadow.getDistance() == 0 || helper.isDeadEnd(gShadow.getGoalPoint())|| !helper.nextStepIsSafe(player, gShadow.getFirstStepToGoal()) ?
                Integer.MAX_VALUE : gShadow.getDistance() - Bullion.PILL_SHADOW.getSeries() - Bullion.PILL_SHADOW.getValue();

        int minW = 0;
        if (wGreen <= wYellow && wGreen <= wRed) {
            minW = wGreen;
            goal = gGreen;
        } else if (wYellow <= wGreen && wYellow <= wRed) {
            minW = wGreen;
            goal = gYellow;
        } else {
            minW = wGreen;
            goal = gRed;
        }

        if (minW >= wShadow)
            goal = gShadow;


        printInfo(goal, wGreen, wYellow, wRed, wShadow);

        return goal;
    }

    /**
     * Вывод информации в консоль
     *
     * @param goal    Целевая точка, к которой движется герой
     * @param wGreen  Вес зеленого золота
     * @param wYellow Вес желтого золота
     * @param wRed    Вес красного золота
     */
    private void printInfo(GoalPoint goal, int wGreen, int wYellow, int wRed, int wShadow) {
        System.out.println("Distance = " + goal.getDistance());
        System.out.println("Goal - " + goal.getGoalPoint().getX() + "/" + goal.getGoalPoint().getY());
        System.out.println("=====================");
        System.out.println("Weight Green = " + wGreen);
        System.out.println("Weight Yellow = " + wYellow);
        System.out.println("Weight Red = " + wRed);
        System.out.println("Weight Shadow = " + wShadow);
    }
}
