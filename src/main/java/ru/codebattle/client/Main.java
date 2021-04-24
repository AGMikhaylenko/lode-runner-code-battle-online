package ru.codebattle.client;

import ru.codebattle.client.api.BoardElement;
import ru.codebattle.client.api.GameBoard;
import ru.codebattle.client.api.LoderunnerAction;
import ru.codebattle.client.api.bot.Solution;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;

public class Main {

    private static final String SERVER_ADDRESS = "https://dojorena.io/codenjoy-contest/board/player/dojorena260?code=6592512883740390488";
    private static Solution mySolution;
    private static String lastBoard;

    public static void main(String[] args) throws IOException {
        ReconnectableLodeRunnerClientWrapper client = new ReconnectableLodeRunnerClientWrapper(SERVER_ADDRESS, Main::doAction);

        client.run();

        System.in.read();

        client.initiateExit();
    }

    private static LoderunnerAction doAction(GameBoard gameBoard) {
        if (lastBoard != null && lastBoard.equals(gameBoard.getBoardString()))
            mySolution = null;

        if (mySolution == null) {
            mySolution = new Solution(gameBoard);
            System.out.println("I create new Solution!");
        }

        lastBoard = gameBoard.getBoardString();

        long millis = System.currentTimeMillis();
        LoderunnerAction answer = mySolution.getNextStepTwo(gameBoard);
        System.out.println("Time = " + (System.currentTimeMillis() - millis));

        return answer;
    }
}
