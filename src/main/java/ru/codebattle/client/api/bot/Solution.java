package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardElement;
import ru.codebattle.client.api.BoardPoint;
import ru.codebattle.client.api.GameBoard;
import ru.codebattle.client.api.LoderunnerAction;

public class Solution {
    private Player player;
    private Helper helper;

    private boolean drillRight; //Сверлим справа от стены
    private boolean drillLeft; //Сверлим справа от стены
    private boolean doNothing;

    public Solution(GameBoard board) {
        player = new Player(board.getMyPosition().getX(), board.getMyPosition().getY());
        helper = new Helper(board, player);
        doNothing = drillLeft = drillRight = false;

        for (GoodElements goodElements : GoodElements.values())
            goodElements.resetSeries();
    }

    public LoderunnerAction getNextStepTwo(GameBoard board) {
        if(doNothing){
            doNothing = false;
            return LoderunnerAction.DO_NOTHING;
        }
        if(drillLeft){
            drillLeft = false;
            doNothing = true;
            return LoderunnerAction.DRILL_LEFT;
        }
        if(drillRight){
            drillRight = false;
            doNothing = true;
            return LoderunnerAction.DRILL_RIGHT;
        }

        player.updatePosition(board.getMyPosition().getX(), board.getMyPosition().getY());
        helper.setBoard(board, player);

        Goal goal = choiceGoal();

        player.update(helper.getNextElement(player.getXPosition(), player.getYPosition(), goal.getFirstStepToGoal()));
        if (goal.getDistance() == 0)
            return helper.wherePit(player.getXPosition(), player.getYPosition());
        else
            return checkWall(goal, board);
    }


    /**
     * Выбор целевой точки для дальнейшего движения
     *
     * @return Объект целевой точки
     */
    private Goal choiceGoal() {
        Goal goal = new Goal(new BoardPoint(0, 0), 0, Direction.LEFT,
                new BoardPoint(0, 0));

        Goal gGreen = helper.getNearestElement(player, BoardElement.GREEN_GOLD);
        Goal gYellow = helper.getNearestElement(player, BoardElement.YELLOW_GOLD);
        Goal gRed = helper.getNearestElement(player, BoardElement.RED_GOLD);
        Goal gShadow = helper.getNearestElement(player, BoardElement.SHADOW_PILL);
        Goal gPortal = helper.getNearestElement(player, BoardElement.PORTAL);

        int wGreen = gGreen.getDistance() == 0 || !helper.nextStepIsSafe(player, gGreen.getFirstStepToGoal()) ?
                Integer.MAX_VALUE : gGreen.getDistance() - GoodElements.GREEN.getSeries() - GoodElements.GREEN.getValue();
        int wYellow = gYellow.getDistance() == 0 || !helper.nextStepIsSafe(player, gYellow.getFirstStepToGoal()) ?
                Integer.MAX_VALUE : gYellow.getDistance() - GoodElements.YELLOW.getSeries() - GoodElements.YELLOW.getValue();
        int wRed = gRed.getDistance() == 0 || !helper.nextStepIsSafe(player, gRed.getFirstStepToGoal()) ?
                Integer.MAX_VALUE : gRed.getDistance() - GoodElements.RED.getSeries() - GoodElements.RED.getValue();
        int wShadow = gShadow.getDistance() == 0 || !helper.nextStepIsSafe(player, gShadow.getFirstStepToGoal()) ?
                Integer.MAX_VALUE : gShadow.getDistance() - GoodElements.PILL_SHADOW.getSeries() - GoodElements.PILL_SHADOW.getValue();

        int minW = Integer.MAX_VALUE;
        if (wGreen <= wYellow && wGreen <= wRed && wGreen <= wShadow) {
            goal = gGreen;
            minW = wGreen;
        }
        if (wYellow <= wGreen && wYellow <= wRed && wYellow <= wShadow) {
            goal = gYellow;
            minW = wYellow;
        }
        if (wRed <= wGreen && wRed <= wYellow && wRed <= wShadow) {
            goal = gRed;
            minW = wRed;
        }
        if (wShadow <= wYellow && wShadow <= wRed && wShadow <= wGreen) {
            goal = gShadow;
            minW = wShadow;
        }


        if (gPortal.getDistance() != 0 &&
                (goal.getDistance() == 0 || goal.getDistance() >= gPortal.getDistance() && minW >= 20
                        || helper.getHunters(player.getXPosition(), player.getYPosition(), 5).size() +
                        helper.getOtherHeroes(player.getXPosition(), player.getYPosition(), 5).size() >= 4))
            goal = gPortal;

        printInfo(goal, wGreen, wYellow, wRed, wShadow);
        System.out.println("Distance to portal = " + gPortal.getDistance());
        return goal;
    }

    /**
     * Проверка наличия стены на первом или втором шаге
     *
     * @param goal
     * @param board
     * @return
     */
    private LoderunnerAction checkWall(Goal goal, GameBoard board) {
        if (helper.getNextElement(player.getXPosition(), player.getYPosition(), goal.getFirstStepToGoal()) == BoardElement.BRICK)
            if (board.isAt(player.getXPosition() - 1, player.getYPosition(), Helper.FREE) ||
                    board.isAt(player.getXPosition() - 1, player.getYPosition(), Helper.FREE_WITH_SHADOW) && player.isShadow()) {
                drillRight = true;
                return Direction.LEFT.getAction();
            } else {
                drillLeft = true;
                return Direction.RIGHT.getAction();
            }

        if (!helper.enemyIsNearby(player) && goal.getDistance() > 2 &&
                board.isAt(goal.getSecondPointInWay(), BoardElement.BRICK) &&
                player.getYPosition() - goal.getSecondPointInWay().getY() == -1) {
            doNothing = true;
            if (player.getXPosition() > goal.getSecondPointInWay().getX())
                return LoderunnerAction.DRILL_LEFT;
            else
                return LoderunnerAction.DRILL_RIGHT;
        }

        return goal.getFirstStepToGoal().getAction();

    }

    /**
     * Вывод информации в консоль
     *
     * @param goal    Целевая точка, к которой движется герой
     * @param wGreen  Вес зеленого золота
     * @param wYellow Вес желтого золота
     * @param wRed    Вес красного золота
     */
    private void printInfo(Goal goal, int wGreen, int wYellow, int wRed, int wShadow) {
        System.out.println("Distance = " + goal.getDistance());
        System.out.println("Goal - " + goal.getGoalPoint().getX() + "/" + goal.getGoalPoint().getY());
        System.out.println("=======================");
        System.out.println("GREEN " + GoodElements.GREEN.getSeries() + " Weight = " + wGreen);
        System.out.println("YELLOW " + GoodElements.YELLOW.getSeries() + " Weight = " + wYellow);
        System.out.println("RED " + GoodElements.RED.getSeries() + " Weight = " + wRed);
        System.out.println("SHADOW " + GoodElements.PILL_SHADOW.getSeries() + " Weight = " + wShadow);
    }
}
