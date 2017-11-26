package br.com.pasco.snakes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SnakeMain {
	
	private static int[][] matrix;
	
	private static int matrixSize;
	
	private static int SNAKE_SIZE = 7;
	
	private static Map<String, int[]> directions = new HashMap<String, int[]>(4) {
		
		private static final long serialVersionUID = 1652809528074250100L;

		{
			put("TOP", new int[] {-1,0});
			put("RIGHT", new int[] {0,1});
			put("BOTTOM", new int[] {1,0});
			put("LEFT", new int[] {0,-1});
		}
	};
	
	private static Map<Integer, ArrayList<Snake>> sumSnakes = new HashMap<Integer, ArrayList<Snake>>(0);

	private static BufferedReader stream;

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Missing arguments. Try: java Snake <file.csv>");
			System.exit(1);
		}
		
		matrix = readFile(args[0]);
		
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				searchSnakes(new Snake(i, j));
			}
		}
		System.out.println("FAIL");
		System.exit(1);
	}

	private static int[][] readFile(String filename) {
		String separator = ",";
		int[][] resp = null;
		
		try {
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader isr = new InputStreamReader(fis);
			stream = new BufferedReader(isr);
			
			String[] values;
			matrixSize = 0;
			int lineCounter = 0;
			
			stream.mark(1);
			
			String line = stream.readLine();
			
			// Test if file is empty. If not, initialize matrixSize and response.
			if(line != null) {
				values = line.split(separator);
				matrixSize = values.length;
				resp = new int[matrixSize][matrixSize];
			}
			else {
				throw new RuntimeException("Empty file.");
			}
			
			// Come back to beginning
			stream.reset();
			while ((line = stream.readLine()) != null) {			
				values = line.split(separator);
				
				// Test if the line has the correct column size
				if(values.length != matrixSize) {
					 throw new RuntimeException("Invalid column size");
				}
				
				for (int i = 0; i < values.length; i++) {
					resp[lineCounter][i] = Integer.valueOf(values[i]);
				}
				
				lineCounter++;
			}
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
		return resp;
	}

	private static void searchSnakes(Snake snake) {
		if (snake != null) {
			// Test if the snake has the required size
			// if not, a new function call are made for each direction
			if (snake.getCells().size() < SNAKE_SIZE) {
				searchSnakes(getNext("TOP", snake));
				searchSnakes(getNext("RIGHT", snake));
				searchSnakes(getNext("BOTTOM", snake));
				searchSnakes(getNext("LEFT", snake));
			}
			// When a possible snake was found, find its pair.
			else {
				findSnakePair(snake);
			}
		}
	}

	private static void findSnakePair(Snake snake) {
		int sum = getSnakeSum(snake); // Calculate the sum of snake cells
		
		// Find if a snake with the same sum was already found
		ArrayList<Snake> previousSnakes = sumSnakes.get(sum);
		
		if(previousSnakes != null) {
			// If a snake with the same sum was found, test if there is a pair
			ArrayList<Snake> snakePair = getSnakePair(snake, previousSnakes);
			if(snakePair != null) {
				for(Snake sp: snakePair) {
					for(Cell c : sp.getCells()) {
						System.out.print("{" + (c.getI()+1) + "," + (c.getJ()+1) + "} ");
					}
					System.out.print("\n");					
				}
				System.exit(0);
			}
			else {
				previousSnakes.add(snake);
			}
		}
		else {
			ArrayList<Snake> sumList = new ArrayList<Snake>();
			sumList.add(snake);
			sumSnakes.put(sum, sumList);
		}		
	}
	
	// Find on the list of snakes an allowed pair
	private static ArrayList<Snake> getSnakePair(Snake snake, ArrayList<Snake> previousSnakes) {
		for(Snake previous : previousSnakes) {
			if(!haveCommomCells(snake, previous)) {
				ArrayList<Snake> resp = new ArrayList<Snake>();
				resp.add(previous);
				resp.add(snake);
				return resp;
			}
		}		
		return null;
	}

	// Test if two snakes have common cells
	private static boolean haveCommomCells(Snake snake, Snake previous) {
		for(Cell snakeCell : snake.getCells()) {
			for(Cell previousCell : previous.getCells()) {
				if(snakeCell.getI() == previousCell.getI() && snakeCell.getJ() == previousCell.getJ()) {
					return true;
				}
			}
			
		}
		return false;
	}

	private static int getSnakeSum(Snake snake) {
		int sum = 0; 
		for(Cell cell : snake.getCells()) {
			sum += matrix[cell.getI()][cell.getJ()];
		}
		return sum;
	}

	private static Snake getNext(String direction, Snake snake) {
		Cell cell = snake.getCells().get(snake.getCells().size() - 1);
		int[] dir = directions.get(direction);
		Cell newCell = new Cell(0, 0);
		Snake newSnake = new Snake(0,0);
		
		// Test if next i is negative or greater than matrix size-1
		if(cell.getI() + dir[0] >= 0 && cell.getI() + dir[0] < matrixSize - 1) {
			newCell.setI(cell.getI() + dir[0]);			
		}
		else {
			return null;
		}
		// Test if next j is negative or greater than matrix size-1
		if(cell.getJ() + dir[1] >= 0 && cell.getJ() + dir[1] < matrixSize - 1) {
			newCell.setJ(cell.getJ() + dir[1]);			
		}
		else {
			return null;
		}
		// Test if the newCell can compose the snake
		if(allowed(newCell, snake)) {
			newSnake.getCells().clear();
			newSnake.getCells().addAll(snake.getCells());
			newSnake.getCells().add(newCell);
		}
		else {
			return null;
		}
		 
		return newSnake;
	}

	private static boolean allowed(Cell newCell, Snake snake) {
		for(Cell cell : snake.getCells()) {
			// Test if newCell is already inside array
			if(newCell.getI() == cell.getI() && newCell.getJ() == cell.getJ()) {
				return false;
			}
			// Test if newCell makes a cycle
			if(snake.getCells().size() >= 3 && snake.getCells().indexOf(cell) < snake.getCells().size() - 1) {
				// Test if newCell is on the same row and is adjacent on the left or the right side of the snake. 
				if(newCell.getI() == cell.getI() && (newCell.getJ() == cell.getJ() - 1 || newCell.getJ() == cell.getJ() + 1)) {
					return false;
				}
				// Test if newCell is on the same column and is adjacent on the top or the bottom side of the snake.
				if(newCell.getJ() == cell.getJ() && (newCell.getI() == cell.getI() - 1 || newCell.getI() == cell.getI() + 1)) {
					return false;
				}
			}
		}
		return true;
	}
}
