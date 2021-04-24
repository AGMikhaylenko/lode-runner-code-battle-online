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
    public static final BoardElement[] OTHER_HERO = {BoardElement.OTHER_HERO_DRILL_LEFT, BoardElement.OTHER_HERO_DRILL_RIGHT,
            BoardElement.OTHER_HERO_LADDER, BoardElement.OTHER_HERO_LEFT, BoardElement.OTHER_HERO_RIGHT,
            BoardElement.OTHER_HERO_FALL_LEFT, BoardElement.OTHER_HERO_FALL_RIGHT, BoardElement.OTHER_HERO_PIPE_LEFT,
            BoardElement.OTHER_HERO_PIPE_RIGHT};
    public static final BoardElement[] OTHER_HERO_SHADOW = {BoardElement.OTHER_HERO_SHADOW_DRILL_LEFT,
            BoardElement.OTHER_HERO_SHADOW_DRILL_RIGHT, BoardElement.OTHER_HERO_SHADOW_LEFT,
            BoardElement.OTHER_HERO_SHADOW_RIGHT, BoardElement.OTHER_HERO_SHADOW_LADDER, BoardElement.OTHER_HERO_SHADOW_FALL_LEFT,
            BoardElement.OTHER_HERO_SHADOW_FALL_RIGHT, BoardElement.OTHER_HERO_SHADOW_PIPE_LEFT,
            BoardElement.OTHER_HERO_SHADOW_PIPE_RIGHT};


    private GameBoard board;
    private int size;
    private NodeOfBoard[][] nodes;

    private final int[] dx = {1, -1, 0, 0};
    private final int[] dy = {0, 0, 1, -1};

    public Helper(GameBoard board, Player player) {
        this.board = board;
        board.replaceElementInMyPosition(player.getCurrentElement());
        size = board.size();
        getMatrixOfNodes(player);
    }

    /**
     * Обновление значения игровой доски
     *
     * @param board Игровая доска
     */
    public void setBoard(GameBoard board, Player player) {
        this.board = board;
        board.replaceElementInMyPosition(player.getCurrentElement());
        size = board.size();
        getMatrixOfNodes(player);
    }


    public GoalPoint getNearestElement(int xStart, int yStart, boolean isCheckDeadEnd, BoardElement... elements) {
        int[][] cells = new int[board.size()][board.size()];
        for (int[] row : cells)
            Arrays.fill(row, -1);
        cells[xStart][yStart] = 0;

        ArrayList<NodeOfBoard> queue = new ArrayList<>();
        queue.add(nodes[xStart][yStart]);

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
                if (board.isAt(bp.getX(), bp.getY(), elements) &&
                        (isCheckDeadEnd || cells[bp.getX()][bp.getY()] < smallestDistanceOtherToPoint(bp.getX(), bp.getY(),
                                getOtherHeroes(bp.getX(), bp.getY(), 15), elements))) {
                    distance = cells[bp.getX()][bp.getY()];
                    endNode = nodes[bp.getX()][bp.getY()];
                    break;
                }
            }
        }

        if (distance != 0) {
            BoardPoint temp = endNode.getPoint();
            while (temp.getX() != xStart || temp.getY() != yStart) {
                for (int i = 0; i < dx.length; i++) {
                    int xTemp = temp.getX() + dx[i];
                    int yTemp = temp.getY() + dy[i];

                    if (cells[xTemp][yTemp] - cells[temp.getX()][temp.getY()] == -1 && nodes[xTemp][yTemp] != null &&
                            nodes[xTemp][yTemp].getNeighbours().contains(nodes[temp.getX()][temp.getY()].getPoint())) {
                        cells[temp.getX()][temp.getY()] = Integer.MAX_VALUE;
                        temp = nodes[xTemp][yTemp].getPoint();
                        break;
                    }
                }
            }
            for (int i = 0; i < dx.length; i++) {
                if (cells[xStart + dx[i]][yStart + dy[i]] == Integer.MAX_VALUE) {
                    direction = Direction.values()[i];
                    break;
                }
            }
        }
        return new GoalPoint(endNode.getPoint(), distance, direction);

    }

    public LoderunnerAction wherePit(int x, int y) {
        if (board.isAt(x + 1, y + 1, BoardElement.BRICK) && board.isAt(x + 1, y, BoardElement.NONE))
            return LoderunnerAction.DRILL_RIGHT;
        if (board.isAt(x - 1, y + 1, BoardElement.BRICK) && board.isAt(x - 1, y, BoardElement.NONE))
            return LoderunnerAction.DRILL_LEFT;
        return LoderunnerAction.DO_NOTHING;
    }

    /**
     * Получение следующего элемента по направлению движения
     *
     * @param x         Х координата точки
     * @param y         Y координата точки
     * @param direction Направление движения
     * @return Следующий элемент
     */
    public BoardElement getNearestElement(int x, int y, Direction direction) {
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

    public void getMatrixOfNodes(Player player) {
        nodes = new NodeOfBoard[size][size];

        for (int x = 0; x < nodes.length; x++)
            for (int y = 0; y < nodes.length; y++) {
                if (board.isAt(x, y, FREE) || board.isAt(x, y, FREE_WITH_SHADOW) && player.isShadow() || board.isAt(x, y, GOLDS) || isGoodWall(x, y)
                        || x == player.getXPosition() && y == player.getYPosition()) {
                    BoardPoint bp = new BoardPoint(x, y);
//                    BoardElement el = board.getElementAt(bp);
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
                                (board.isAt(x + dx[i], y + dy[i], FREE) || board.isAt(x + dx[i], y + dy[i], FREE) && player.isShadow()
                                        || board.isAt(x + dx[i], y + dy[i], GOLDS) ||
                                        isGoodWall(x + dx[i], y + dy[i]))) {
                            neighbours.add(new BoardPoint(x + dx[i], y + dy[i]));
                        }
                    }
                    nodes[x][y] = new NodeOfBoard(bp, neighbours, board.getElementAt(bp));
                } else
                    nodes[x][y] = null;
            }
    }

    private boolean isGoodWall(int x, int y) {
//        if (board.isAt(x, y, BoardElement.BRICK) && board.isAt(x, y + 1, FREE) && //Если клетка - стена и под ней есть пространство
//                (board.isAt(x, y - 1, BoardElement.NONE) || board.isAt(x, y - 1, BoardElement.PIPE)) &&
//                (!board.isAt(x - 1, y, NO_FUNDAMENT) && board.isAt(x - 1, y - 1, FREE)
//                        || !board.isAt(x + 1, y, NO_FUNDAMENT) && board.isAt(x + 1, y - 1, FREE)))
//            return true;
//        else
        return false;
    }

    /**
     * Получение списка соперников на карте
     *
     * @return Список соперников
     */
    public ArrayList<Hero> getOtherHeroes() {
        ArrayList<Hero> enemies = new ArrayList<>();

        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                if (board.isAt(x, y, OTHER_HERO_SHADOW) || (board.isAt(x, y, OTHER_HERO)))
                    enemies.add(new Hero(x, y, board.getElementAt(x, y)));

        return enemies;
    }

    /**
     * Получение списка соперников на карте в пределах заданного радиуса от точки
     *
     * @param xPoint Х координата точки
     * @param yPoint Y координата точки
     * @param radius Радиус поиска
     * @return Список соперников
     */
    public ArrayList<Hero> getOtherHeroes(int xPoint, int yPoint, int radius) {
        int minX = xPoint - radius < 0 ? 0 : xPoint - radius;
        int maxX = xPoint + radius >= size ? size - 1 : xPoint + radius;
        int minY = yPoint - radius < 0 ? 0 : yPoint - radius;
        int maxY = yPoint + radius >= size ? size - 1 : yPoint + radius;

        ArrayList<Hero> enemies = new ArrayList<>();

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                if (board.isAt(x, y, OTHER_HERO_SHADOW) || (board.isAt(x, y, OTHER_HERO)))
                    enemies.add(new Hero(x, y, board.getElementAt(x, y)));

        return enemies;
    }

    /**
     * Получение списка координат охотников
     *
     * @return Список координат охотников
     */
    public ArrayList<BoardPoint> getHunters() {
        ArrayList<BoardPoint> hunters = new ArrayList<>();

        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                if (board.isAt(x, y, HUNTERS))
                    hunters.add(new BoardPoint(x, y));

        return hunters;
    }

    /**
     * Получение списка координат охотников на карте в пределах заданного радиуса от точки
     *
     * @param xPoint Х координата точки
     * @param yPoint Y координата точки
     * @param radius Радиус поиска
     * @return Список координат охотников
     */
    public ArrayList<BoardPoint> getHunters(int xPoint, int yPoint, int radius) {
        int minX = xPoint - radius < 0 ? 0 : xPoint - radius;
        int maxX = xPoint + radius >= size ? size - 1 : xPoint + radius;
        int minY = yPoint - radius < 0 ? 0 : yPoint - radius;
        int maxY = yPoint + radius >= size ? size - 1 : yPoint + radius;

        ArrayList<BoardPoint> hunters = new ArrayList<>();

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                if (board.isAt(x, y, HUNTERS))
                    hunters.add(new BoardPoint(x, y));

        return hunters;
    }

    /**
     * Проверка, является ли точка тупиком
     * Тупик - точка, из которой нельзя добраться до любого полезного объекта
     *
     * @param pointStart Точка, для которой происходит проверка
     * @return Результат проверки: true - точка является тупиком, false - точка не является тупиком
     */
    public boolean isDeadEnd(BoardPoint pointStart) {
        for (Bullion bullion : Bullion.values()) {
            if (getNearestElement(pointStart.getX(), pointStart.getY(), true, bullion.getBoardElement()).getDistance() != 0)
                return false;
        }
        return true;
    }

    /**
     * Расчет координат следующей точки по направлению движения
     *
     * @param x         Х координата точки
     * @param y         Y координата точки
     * @param direction
     * @return
     */
    private BoardPoint getNextPoint(int x, int y, Direction direction) {
        switch (direction) {
            case LEFT:
                return new BoardPoint(x - 1, y);
            case RIGHT:
                return new BoardPoint(x + 1, y);
            case UP:
                return new BoardPoint(x, y - 1);
            case DOWN:
                return new BoardPoint(x, y + 1);
            default:
                return new BoardPoint(x, y);
        }
    }

    private int smallestDistanceOtherToPoint(int xGoal, int yGoal, ArrayList<Hero> enemies, BoardElement... elements) {
        int minDistance = Integer.MAX_VALUE;

        for (Hero hero : enemies) {
            int[][] cells = new int[board.size()][board.size()];
            for (int[] row : cells)
                Arrays.fill(row, -1);
            cells[hero.getXPosition()][hero.getYPosition()] = 0;

            ArrayList<NodeOfBoard> queue = new ArrayList<>();
//            Предположим, что противник первым шагом может сделать шаг в любую сторону
            ArrayList<BoardPoint> virtualNeighbours = new ArrayList<BoardPoint>();
            virtualNeighbours.add(new BoardPoint(hero.getXPosition() - 1, hero.getYPosition()));
            virtualNeighbours.add(new BoardPoint(hero.getXPosition() + 1, hero.getYPosition()));
            virtualNeighbours.add(new BoardPoint(hero.getXPosition(), hero.getYPosition() - 1));
            virtualNeighbours.add(new BoardPoint(hero.getXPosition(), hero.getYPosition() + 1));

            queue.add(new NodeOfBoard(new BoardPoint(hero.getXPosition(), hero.getYPosition()), virtualNeighbours, BoardElement.NONE));

            int distance = 0;

            while (!queue.isEmpty() && distance == 0) {
                NodeOfBoard temp = queue.remove(0);
                try {
                    for (BoardPoint bp : temp.getNeighbours()) {
                        if (cells[bp.getX()][bp.getY()] == -1 && nodes[bp.getX()][bp.getY()] != null) {
                            cells[bp.getX()][bp.getY()] = cells[temp.getPoint().getX()][temp.getPoint().getY()] + 1;
                            queue.add(nodes[bp.getX()][bp.getY()]);
                        }
                        if (board.isAt(bp.getX(), bp.getY(), elements)) {
                            if (bp.getX() == xGoal && bp.getY() == yGoal)
                                distance = cells[bp.getX()][bp.getY()];
                            else
                                distance = Integer.MAX_VALUE;
                        }
                    }
                } catch (NullPointerException npe) {
                    System.out.println(npe.getMessage());
                }

            }
            if (minDistance > distance)
                minDistance = distance;
        }
        return minDistance;
    }

    /**
     * Проверка безопасности следующего шага
     *
     * @param player    Игрок
     * @param direction Направление, в котором будет сделан шаг
     * @return Результат проверки: true - шаг безопасен, false - опасен
     */
    public boolean nextStepIsSafe(Player player, Direction direction) {
        if (player.isShadow())
            return true;

        BoardPoint nextPoint = getNextPoint(player.getXPosition(), player.getYPosition(), direction);
        for (int i = 0; i < dx.length; i++) {
            if (board.isAt(player.getXPosition() + dx[i], player.getYPosition() + dy[i], HUNTERS) ||
                    board.isAt(player.getXPosition() + dx[i], player.getYPosition() + dy[i], OTHER_HERO_SHADOW))
                return false;
        }

        return true;
    }
}
