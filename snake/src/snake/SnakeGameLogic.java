package snake;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGameLogic {

	private LinkedList<Integer[]> snake;
	private int width = 35;
	private int length = 35;
	private BoardState[][] board;
	private int[] applePos;
	private Direction curDir;
	private boolean isGameFinished;
	private int[][] emptyCells;
	private int emptyCount;
	private Random random = new Random();
	private int applesEaten;
	
	
	private int best;
	private int prevBest;
	private File bestFile;

	public SnakeGameLogic() {
		board = new BoardState[width][length];
		emptyCells = new int[width * length][2];
		initSnake();
		setBoard();
		setEmptyCells();
		applePos = putAppleNew();
		markApple();
		curDir = Direction.right;
		isGameFinished = false;
		
		bestFile = new File("C:\\Users\\maaya\\eclipse-workspace\\snake\\res\\best.txt");
		if(!bestFile.exists()) {
			try {
				bestFile.createNewFile();
			} catch (IOException e) {
				System.out.println(e.getClass().toString() + ": " + e.getMessage());
			}
			best = 0;
		}
		else {
			readBest();
		}
	}

	private void readBest() {
		try (BufferedReader br = new BufferedReader(new FileReader(bestFile))) {
			String line = br.readLine();
			if (line != null && !line.isEmpty()) {
	            best = Integer.parseInt(line);
	        } else {
	            best = 0;
	        }
			prevBest = best;
		}
		catch (IOException e) {
		System.out.println(e.getClass().toString() + ": " + e.getMessage());
		}
	}

	private void initSnake() {
		snake = new LinkedList<Integer[]>();
		int startingRow = (2 + (board.length - 3)) / 2;
		Integer[][] startingPosition = {{startingRow, 4}, {startingRow, 3}, {startingRow, 2}, {startingRow, 1}};
		for(int i = 0; i < startingPosition.length; i++) {
			snake.add(i, startingPosition[i]);
		}
	}
	
	private int[] putAppleNew() {
		int[] applePosition = new int[2];
		int chosenIndex = random.nextInt(emptyCount);
		applePosition = emptyCells[chosenIndex];
		return applePosition;
	}

	private void setBoard() {
		
		//snake cells are marked
		Integer[] snakePos = new Integer[2];
		for(int s = 0; s < snake.size(); s++) {
			snakePos = snake.get(s);
			board[snakePos[0]][snakePos[1]] = BoardState.SNAKE;
		}
		
		//all the cells are marked as "empty" 
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				if(board[i][j] == null) {
					board[i][j] = BoardState.EMPTY;
				}
			}
		}
		
		//wall cells are marked
		for (int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[0].length; j++) {
				if (i == 0 || i == board.length - 1 || j == 0 || j == board[0].length - 1) {
					board[i][j] = BoardState.WALL;
				}
			}
		}
	
	}
	
	private void markApple() {
		board[applePos[0]][applePos[1]] = BoardState.APPLE;
	}

	public LinkedList<Integer[]> getSnake() {
		return snake;
	}



	public void printBoard() {
		String[][] stringBoard = new String[board.length][board[0].length];

		for (int i = 0; i < stringBoard.length; i++) {
			for (int j = 0; j < stringBoard[0].length; j++) {
				switch(board[i][j]) {
				case BoardState.EMPTY:
					stringBoard[i][j] = " .";
					break;
				case BoardState.WALL:
					stringBoard[i][j] = " W";
					break;
				case BoardState.SNAKE:
					stringBoard[i][j] = " S";
					break;
				case BoardState.APPLE:
					stringBoard[i][j] = " A";
					break;
				}
			}
		}

		for (int i = 0; i < stringBoard.length; i++) {
			for (int j = 0; j < stringBoard[0].length; j++) {
				System.out.print(stringBoard[i][j]);
			}
			System.out.println();
		}
	}
	
	public void move(Direction dir) {
		Integer[] head = snake.getFirst();
		if((dir == Direction.down && curDir == Direction.up) || (dir == Direction.up && curDir == Direction.down) ||
				(dir == Direction.left && curDir == Direction.right) || (dir == Direction.right && curDir == Direction.left)) {
			dir = curDir;
		}
		switch(dir) {
		case up:
			snake.addFirst(new Integer[] {head[0] - 1, (head[1])});
			break;
		case down:
			snake.addFirst(new Integer[] {head[0] + 1, (head[1])});
			break;
		case right:
			snake.addFirst(new Integer[] {head[0], head[1] + 1});
			break;
		case left:
			snake.addFirst(new Integer[] {head[0], head[1] - 1});
			break;
		}
		
		
		boolean ateApple = isApple();
		if(!ateApple) {
			snake.removeLast();
		}
		if(checkCollision() || emptyCount == 0) {
			isGameFinished = true;
			updateBest();
			return;
		}
		setBoard();
		curDir = dir;
		if(ateApple) {
			applesEaten++;
			setEmptyCells();
			applePos = putAppleNew();
			markApple();
			if (applesEaten > best) {
				best++;
			}
		}
	}
	
	boolean isApple() {
		return snake.getFirst()[0] == applePos[0] && snake.getFirst()[1] == applePos[1];
	}
	
	private boolean checkCollision() {
		int snakeX = snake.getFirst()[0];
		int snakeY = snake.getFirst()[1];
		
		if(snakeX == 0 || snakeX == board.length - 1 || snakeY == 0 || snakeY == board[0].length - 1) {
			return true;
		}
		
		for(int i = 1; i < snake.size(); i++) {
			Integer[] part = snake.get(i);
			if(part[0] == snakeX && part[1] == snakeY) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isGameFinished() {
		return isGameFinished;
	}
	
	public void setEmptyCells() {
		emptyCount = 0;
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[0].length; j++) {
				if(board[i][j].equals(BoardState.EMPTY)) {
					emptyCells[emptyCount][0] = i;
					emptyCells[emptyCount][1] = j;
					emptyCount++;
				}
			}
		}
	}
	
	public int[] getApplePos() {
		return applePos;
	}
	
	public BoardState[][] getBoard() {
		return board;
	}

	public int getApplesEaten() {
		return applesEaten;
	}
	
	public boolean isGameLost() {
		return checkCollision();
	}
	
	public int getBest() {
		return best;
	}
	
	private void updateBest() {
		if(best > prevBest) {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(bestFile))) {
				bw.write(String.valueOf(best));
			}
			catch (IOException e) {
			System.out.println(e.getClass().toString() + ": " + e.getMessage());
			}
		}
	}
	
	public boolean hasBestUpdated() {
		return best > prevBest;
	}
	
}
