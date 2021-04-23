package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardElement;
import ru.codebattle.client.api.GameBoard;
import ru.codebattle.client.api.LoderunnerAction;

public class Solution {
    private int ticks;
    private Player player;
    private GameBoard board;
    private Helper helper;

    private int pitLeftStep; //Сверлим слева от стены
    private int pitRightStep; //Сверлим справа от стены

    private LoderunnerAction[] pitLeftActions = {LoderunnerAction.GO_LEFT, LoderunnerAction.DRILL_RIGHT, LoderunnerAction.GO_RIGHT};
    private LoderunnerAction[] pitRightActions = {LoderunnerAction.GO_RIGHT, LoderunnerAction.DRILL_LEFT, LoderunnerAction.GO_LEFT};

    public Solution(GameBoard board) {
        this.board = board;
        player = new Player(board.getMyPosition().getX(), board.getMyPosition().getY());
        helper = new Helper(board);
        pitLeftStep = pitRightStep = 0;
    }

    public LoderunnerAction getNextStep(GameBoard board) {
        this.board = board;
        helper.setBoard(board);
        player.updatePosition(board.getMyPosition().getX(), board.getMyPosition().getY());

        if (pitRightStep != 0) {
            LoderunnerAction next = pitRightActions[pitRightStep - 1];
            pitRightStep = ++pitRightStep % 4;
            return next;
        }
        if (pitLeftStep != 0) {
            LoderunnerAction next = pitLeftActions[pitLeftStep - 1];
            pitLeftStep = ++pitLeftStep % 4;
            return next;
        }


        GoalPoint goal = helper.getNearestElement(player, Helper.GOLDS);
        BoardElement nextElement = helper.getNextElement(player.getXPosition(), player.getYPosition(), goal.getFirstStepToGoal());
        if (nextElement == BoardElement.BRICK) {
            if (board.isAt(player.getXPosition() - 1, player.getYPosition(), Helper.FREE) &&
                    !board.isAt(player.getXPosition() - 1, player.getYPosition() + 1, Helper.NO_FUNDAMENT)) {
                pitLeftStep = 2;
                return pitLeftActions[0];
            } else {
                pitRightStep = 2;
                return pitRightActions[0];
            }
        }


        printInfo(goal);

        player.update(helper.getNextElement(player.getXPosition(), player.getYPosition(), goal.getFirstStepToGoal()));
        if (goal.getDistance() == 0)
            return helper.wherePit(player.getXPosition(), player.getYPosition());
        else
            return goal.getFirstStepToGoal().getAction();
    }

    private void printInfo(GoalPoint goal) {
        System.out.println("Distance = " + goal.getDistance());
        System.out.println("Goal - " + goal.getGoalPoint().getX() + "/" + goal.getGoalPoint().getY());
        System.out.println("Green " + player.getSeries()[0].getSeries());
        System.out.println("Yellow " + player.getSeries()[1].getSeries());
        System.out.println("Red " + player.getSeries()[2].getSeries());
    }
}
