package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardElement;
import ru.codebattle.client.api.BoardPoint;

import java.util.ArrayList;

/**
 * Класс, описывающий узел на игровом поле
 */
public class NodeOfBoard {
    private BoardPoint point;//Точка на игровом поле
    private ArrayList<BoardPoint> neighbours;//Список соседних точек, в которые можно попасть
    private BoardElement element;//Элемент в данной точке

    public NodeOfBoard(BoardPoint point, ArrayList<BoardPoint> neighbours, BoardElement element) {
        this.point = point;
        this.neighbours = neighbours;
        this.element = element;
    }

    public BoardPoint getPoint() {
        return point;
    }

    public ArrayList<BoardPoint> getNeighbours() {
        return neighbours;
    }

    public BoardElement getElement() {
        return element;
    }
}
