package br.com.pasco.snakes;

import java.util.ArrayList;

public class Snake {
	ArrayList<Cell> cells;
	
	public Snake(int i, int j) {
		Cell cell = new Cell(i, j);
		this.cells = new ArrayList<Cell>();
		this.cells.add(cell);
	}
	
	public ArrayList<Cell> getCells() {
		return cells;
	}

	public void setCells(ArrayList<Cell> cells) {
		this.cells = cells;
	}		
	
	public void addCell(int i, int j) {
		Cell cell = new Cell(i, j);
		this.cells.add(cell);
	}
}
