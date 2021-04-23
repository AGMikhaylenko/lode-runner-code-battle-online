package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardElement;
import ru.codebattle.client.api.BoardPoint;
import ru.codebattle.client.api.GameBoard;
import ru.codebattle.client.api.LoderunnerAction;

import java.util.ArrayList;
import java.util.Arrays;

public class Helper {
    public static final BoardElement[] GOLDS = {BoardElement.GREEN_GOLD, BoardElement.YELLOW_GOLD, BoardElement.RED_GOLD};
    public static final BoardElement[] NO_FUNDAMENT = {BoardElement.NONE, BoardElement.PIPE, BoardElement.RED_GOLD,
            BoardElement.YELLOW_GOLD, BoardElement.GREEN_GOLD, BoardElement.SHADOW_PILL, BoardElement.PORTAL};
    public static final BoardElement[] FREE = {BoardElement.NONE, BoardElement.PIPE, BoardElement.LADDER,
            BoardElement.SHADOW_PILL, BoardElement.PORTAL};
    public static final BoardElement[] FREE_WITH_SHADOW = {BoardElement.NONE, BoardElement.PIPE, BoardElement.LADDER,
            BoardElement.SHADOW_PILL, BoardElement.PORTAL, BoardElement.OTHER_HERO_DIE, BoardElement.OTHER_HERO_DRILL_LEFT,
            BoardElement.OTHER_HERO_DRILL_RIGHT, BoardElement.OTHER_HERO_LADDER, BoardElement.OTHER_HERO_LEFT,
            BoardElement.OTHER_HERO_RIGHT, BoardElement.OTHER_HERO_FALL_LEFT, BoardElement.OTHER_HERO_FALL_RIGHT,
            BoardElement.OTHER_HERO_PIPE_LEFT, BoardElement.OTHER_HERO_PIPE_RIGHT, BoardElement.OTHER_HERO_SHADOW_DIE,
            BoardElement.OTHER_HERO_SHADOW_DRILL_LEFT, BoardElement.OTHER_HERO_SHADOW_DRILL_RIGHT, BoardElement.OTHER_HERO_SHADOW_LEFT,
            BoardElement.OTHER_HERO_SHADOW_RIGHT, BoardElement.OTHER_HERO_SHADOW_LADDER, BoardElement.OTHER_HERO_SHADOW_FALL_LEFT,
            BoardElement.OTHER_HERO_SHADOW_FALL_RIGHT, BoardElement.OTHER_HERO_SHADOW_PIPE_LEFT,
            BoardElement.OTHER_HERO_SHADOW_PIPE_RIGHT, BoardElement.ENEMY_LADDER, BoardElement.ENEMY_LEFT,
            BoardElement.ENEMY_RIGHT, BoardElement.ENEMY_PIPE_LEFT, BoardElement.ENEMY_PIPE_RIGHT, BoardElement.ENEMY_PIT};
    public static final BoardElement[] HUNTERS = {BoardElement.ENEMY_LADDER, BoardElement.ENEMY_LEFT,
            BoardElement.ENEMY_RIGHT, BoardElement.ENEMY_PIPE_LEFT, BoardElement.ENEMY_PIPE_RIGHT, BoardElement.ENEMY_PIT};


    private GameBoard board;
    private int size;

    private final int[] dx = {1, -1, 0, 0};
    private final int[] dy = {0, 0, 1, -1};

    public Helper(GameBoard board) {
        this.board = board;
        size = board.size();
    }


    public GoalPoint getNearestElement(Player player, BoardElement... elements) {
        int[][] cells = new int[board.size()][board.size()];
        for (int[] row : cells)
            Arrays.fill(row, -1);
        cells[player.getXPosition()][player.getYPosition()] = 0;

        NodeOfBoard[][] nodes = getMatrixOfNodes(player);

        ArrayList<NodeOfBoard> queue = new ArrayList<>();
        queue.add(nodes[player.getXPosition()][player.getYPosition()]);

        int distance = 0;
        NodeOfBoard endNode = new NodeOfBoard(new BoardPoint(0, 0), new ArrayList<BoardPoint>(), BoardElement.NONE);
        Direction direction = Direction.RIGHT;

        while (!queue.isEmpty() && distance == 0) {
            NodeOfBoard temp = queue.remove(0);

            for (BoardPoint bp : temp.getNeighbours()) {
                if (cells[bp.getX()][bp.getY()] == -1) {
                    cells[bp.getX()][bp.getY()] = cells[temp.getPoint().getX()][temp.getPoint().getY()] + 1;
                    queue.add(nodes[bp.getX()][bp.getY()]);
                }
                for (BoardElement be : elements)
                    if (be == nodes[bp.getX()][bp.getY()].getElement()) {
                        distance = cells[bp.getX()][bp.getY()];
                        endNode = nodes[bp.getX()][bp.getY()];
                        break;
                    }
            }
        }

        if (distance != 0) {
            BoardPoint temp = endNode.getPoint();
            while (temp.getX() != player.getXPosition() || temp.getY() != player.getYPosition()) {
                for (int i = 0; i < dx.length; i++) {
                    int xTemp = temp.getX() + dx[i];
                    int yTemp = temp.getY() + dy[i];
                    if (cells[xTemp][yTemp] - cells[temp.getX()][temp.getY()] == -1
                            && nodes[xTemp][yTemp].getNeighbours().contains(nodes[temp.getX()][temp.getY()].getPoint())) {
                        cells[temp.getX()][temp.getY()] = Integer.MAX_VALUE;
                        temp = nodes[xTemp][yTemp].getPoint();
                        break;
                    }
                }
            }
            for (int i = 0; i < dx.length; i++) {
                if (cells[player.getXPosition() + dx[i]][player.getYPosition() + dy[i]] == Integer.MAX_VALUE) {
                    direction = Direction.values()[i];
                    break;
                }
            }
        }
        return new GoalPoint(endNode.getPoint(), distance, direction);

    }

    public void setBoard(GameBoard board) {
        this.board = board;
        size = board.size();
    }

    public LoderunnerAction wherePit(int x, int y) {
        if (board.isAt(x + 1, y + 1, BoardElement.BRICK) && board.isAt(x + 1, y, BoardElement.NONE))
            return LoderunnerAction.DRILL_RIGHT;
        if (board.isAt(x - 1, y + 1, BoardElement.BRICK) && board.isAt(x - 1, y, BoardElement.NONE))
            return LoderunnerAction.DRILL_LEFT;
        return LoderunnerAction.DO_NOTHING;
    }

    public BoardElement getNextElement(int x, int y, Direction direction) {
        switch (direction) {
            case RIGHT:
                return board.getElementAt(new BoardPoint(x + 1, y));
            case LEFT:
                return board.getElementAt(new BoardPoint(x - 1, y));
            case DOWN:
                return board.getElementAt(new BoardPoint(x, y + 1));
            case UP:
                return board.getElementAt(new BoardPoint(x, y - 1));
            default:
                return BoardElement.NONE;
        }
    }

    public NodeOfBoard[][] getMatrixOfNodes(Player player) {
        NodeOfBoard[][] nodes = new NodeOfBoard[size][size];

        for (int x = 0; x < nodes.length; x++)
            for (int y = 0; y < nodes.length; y++) {


                if (board.isAt(x, y, FREE) || board.isAt(x, y, GOLDS) || isGoodWall(x, y) ||
                        x == player.getXPosition() && y == player.getYPosition()) {
                    BoardPoint bp = new BoardPoint(x, y);
                    BoardElement el = x == player.getXPosition() && y == player.getYPosition() ? player.getCurrentElement() :
                            board.getElementAt(bp);
                    boolean[] doMove;
                    switch (el) {
                        //Right, left, down, up
                        case LADDER:
                            doMove = new boolean[]{true, true, true, true};
                            break;
                        case PIPE:
                            doMove = new boolean[]{true, true, true, false};
                            break;
                        case BRICK:
                            doMove = new boolean[]{false, false, true, false};
                            break;
                        default:
                            boolean downFree = false;
                            if (x == player.getXPosition() && y + 1 == player.getYPosition())
                                for (BoardElement be : NO_FUNDAMENT) {
                                    if (player.getCurrentElement() == be) {
                                        downFree = true;
                                        break;
                                    }
                                }
                            else
                                downFree = board.isAt(x, y + 1, NO_FUNDAMENT);
                            if (downFree)
                                doMove = new boolean[]{false, false, true, false};
                            else
                                doMove = new boolean[]{true, true, true, false};
                    }

                    ArrayList<BoardPoint> neighbours = new ArrayList<>();
                    for (int i = 0; i < dx.length; i++) {
                        if (doMove[i] &&
                                (board.isAt(x + dx[i], y + dy[i], FREE) || board.isAt(x + dx[i], y + dy[i], GOLDS) ||
                                        isGoodWall(x + dx[i], y + dy[i]))) {
                            neighbours.add(new BoardPoint(x + dx[i], y + dy[i]));
                        }
                    }
                    nodes[x][y] = new NodeOfBoard(bp, neighbours, board.getElementAt(bp));
                } else
                    nodes[x][y] = null;
            }

        return nodes;

    }

    private boolean isGoodWall(int x, int y) {
        if (board.isAt(x, y, BoardElement.BRICK) && board.isAt(x, y + 1, FREE) && //Если клетка - стена и под ней есть пространство
                (!board.isAt(x - 1, y, NO_FUNDAMENT) && board.isAt(x - 1, y - 1, FREE)
                        || !board.isAt(x + 1, y, NO_FUNDAMENT) && board.isAt(x + 1, y - 1, FREE)))
            return true;
        else
            return false;
    }

}
