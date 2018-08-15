package com.ibasco.glcdemu;

import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Grid extends Pane {

    private static final Logger log = LoggerFactory.getLogger(Grid.class);

    private int rows;
    private int columns;

    private double width;
    private double height;

    private Cell[][] cells;

    public Grid( int columns, int rows, double width, double height) {
        this.columns = columns;
        this.rows = rows;
        this.width = width;
        this.height = height;
        cells = new Cell[rows][columns];
    }

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        log.info("Resize");
    }

    void add(Cell cell, int column, int row) {
        cells[row][column] = cell;

        double w = 10;//width / columns;
        double h = 10;//height / rows;
        double x = w * column;
        double y = h * row;

        cell.setLayoutX(x);
        cell.setLayoutY(y);
        cell.setPrefWidth(w);
        cell.setPrefHeight(h);

        getChildren().add(cell);
    }

    public Cell getCell(int column, int row) {
        return cells[row][column];
    }

    public void unhighlight() {
        for( int row=0; row < rows; row++) {
            for( int col=0; col < columns; col++) {
                cells[row][col].unhighlight();
            }
        }
    }
}