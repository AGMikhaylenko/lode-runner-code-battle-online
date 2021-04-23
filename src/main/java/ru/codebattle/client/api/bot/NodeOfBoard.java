package ru.codebattle.client.api.bot;

import ru.codebattle.client.api.BoardElement;
import ru.codebattle.client.api.BoardPoint;

import java.util.ArrayList;

public class NodeOfBoard {
    private BoardPoint point;
    private ArrayList<BoardPoint> neighbours;//Список соседей
    private BoardElement element;

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
