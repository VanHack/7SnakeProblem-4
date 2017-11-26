import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SnakeMain {
	
	private static int[][] matrix = {{227, 191, 234, 67, 43, 13, 48, 211, 253, 243},
									{36, 95, 229, 209, 49, 230, 46, 16, 190, 49},
									{206, 130, 85, 67, 104, 93, 128, 243, 38, 173},
									{234, 82, 191, 153, 170, 99, 124, 60, 12, 31},
									{192, 9, 24, 127, 183, 241, 139, 21, 244, 66},
									{93, 200, 66, 16, 189, 42, 209, 113, 215, 4},
									{182, 141, 153, 64, 229, 55, 115, 139, 12, 187},
									{133, 241, 35, 255, 126, 39, 110, 147, 24, 241},
									{2, 202, 191, 159, 223, 128, 154, 109, 6, 200},
									{173, 44, 163, 196, 159, 232, 135, 159, 117, 175}};
	
	private static Map<String, int[]> directions = new HashMap<String, int[]>(4) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1652809528074250100L;

		{
			put("TOP", new int[] {-1,0});
			put("RIGHT", new int[] {0,1});
			put("BOTTOM", new int[] {1,0});
			put("LEFT", new int[] {0,-1});
		}
	};
	
	private static Map<Integer, ArrayList<Snake>> sumSnakes = new HashMap<Integer, ArrayList<Snake>>(0);

	public static void main(String[] args) {		
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				searchSnakes(new Snake(i, j));
			}
		}
	}

	private static void searchSnakes(Snake snake) {
		if (snake != null) {
			if (snake.getCells().size() < 7) {
				searchSnakes(getNext("TOP", snake));
				searchSnakes(getNext("RIGHT", snake));
				searchSnakes(getNext("BOTTOM", snake));
				searchSnakes(getNext("LEFT", snake));
			}
			else {
				findSnakePair(snake);
			}
		}
	}

	private static void findSnakePair(Snake snake) {
		int sum = getSnakeSum(snake);
		if(sum==286) {
			System.out.println(sum);
		}
		ArrayList<Snake> previousSnakes = sumSnakes.get(sum);
		if(previousSnakes != null) {
			ArrayList<Snake> snakePair = getSnakePair(snake, previousSnakes);
			if(snakePair != null) {
				System.out.print(sum+"\n");
				for(Snake sp: snakePair) {
					for(Cell c : sp.getCells()) {
						System.out.print("{" + c.getI() + "," + c.getJ() + "} ");
					}
					System.out.print("\n");					
				}
				System.out.print("\n\n");
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
		
		int MAX_I = 9;
		int MAX_J = 9;
		// Test if next i is negative or greater than matrix size-1
		if(cell.getI() + dir[0] >= 0 && cell.getI() + dir[0] < MAX_I) {
			newCell.setI(cell.getI() + dir[0]);			
		}
		else {
			return null;
		}
		// Test if next j is negative or greater than matrix size-1
		if(cell.getJ() + dir[1] >= 0 && cell.getJ() + dir[1] < MAX_J) {
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
			// Test if newCell is inside array already
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
