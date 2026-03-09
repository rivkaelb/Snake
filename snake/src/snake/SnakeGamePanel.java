package snake;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class SnakeGamePanel extends JPanel implements Runnable {

	private KeyAdapter gameKeyAdapter;
	private int tileSize = 15;
	private int padding;
	private int[][] snakeTiles;
	private int prevApplesEaten = 0;
	private double speed;
	private Direction dir;
	private SnakeGameLogic gameLogic;
	private Color snakeColor = Color.white;
	private Color appleColor = Color.red;
	private final Font scoreFont = new Font("Agency FB", Font.BOLD, 32);

	public SnakeGamePanel() {
		initKeyAdapter();
		addKeyListener(gameKeyAdapter);
		setBackground(Color.black);
		dir = Direction.right;
		speed = 6;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		padding  = getHeight() - 35 * tileSize;

		drawWall(g2d);
		drawSnake(g2d);
		drawApple(g2d);
		drawScoreBoard(g2d);

		if(gameLogic.isGameFinished()) {
			drawFinishedScreen(g2d);
		}

	}


	private void drawWall(Graphics2D g2d) {
		BoardState[][] board = gameLogic.getBoard();
		g2d.setColor(Color.LIGHT_GRAY);
		for(int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if(board[i][j].equals(BoardState.WALL)) {
					g2d.fillRect(j * tileSize, i * tileSize + padding, tileSize, tileSize);
				}
			}
		}
	}
	
	private void drawSnake(Graphics2D g2d) {
		getSnakeTiles();
		for(int i = 0; i < snakeTiles.length; i++) {
			int tileX = snakeTiles[i][1] * tileSize;
			int tileY = snakeTiles[i][0] * tileSize + padding;
			g2d.setColor(snakeColor);
			g2d.fillRect(tileX, tileY, tileSize, tileSize);

		}
	}
	
	private void drawApple(Graphics2D g2d) {
		int[] applePos = gameLogic.getApplePos();
		g2d.setColor(appleColor);
		g2d.fillOval(applePos[1] * tileSize, applePos[0] * tileSize + padding, tileSize, tileSize);
	}
	
	private void drawScoreBoard(Graphics2D g2d) {
		g2d.setFont(scoreFont);
		g2d.setColor(snakeColor);
		g2d.drawString("EATEN APPLES: " + gameLogic.getApplesEaten(), 10, padding - 10);
		FontMetrics fm = g2d.getFontMetrics(scoreFont);
		int bestX = fm.stringWidth("Best: " + gameLogic.getBest());
		g2d.drawString("Best: " + gameLogic.getBest(), getWidth() - bestX - 10, padding - 10);
	}

	private void drawFinishedScreen(Graphics2D g2d) {
		int rectY = padding / 2 + padding;
		int rectHeight = (getHeight() - padding) / 2 + padding;
		
		g2d.setColor(new Color(200, 200, 200, 200));
		g2d.fillRect(0, rectY, getWidth(), rectHeight);
		
		String stateMessage;
		String pressEnterMessage = "Press enter for new game";
		String pressEscMessage = "press Esc to exit";
		Font stateFont = new Font("Agency FB", Font.BOLD, 45);
		Font pressFont = new Font("Agency FB", Font.BOLD, 35);		
		FontMetrics stateMetrics = g2d.getFontMetrics(stateFont);
		FontMetrics pressMetrics = g2d.getFontMetrics(pressFont);
		
		if(gameLogic.isGameLost()) {
			g2d.setColor(new Color(201, 44, 31));
			stateMessage = "GAME OVER!";
		}
		else {
			g2d.setColor(new Color(68, 156, 42));
			stateMessage = "YOU WON!";
		}
		
		int fontY = rectY + (rectHeight - (stateMetrics.getAscent() + stateMetrics.getDescent())) / 2 + stateMetrics.getAscent();
		int stateWidth = stateMetrics.stringWidth(stateMessage);
		int enterWidth = pressMetrics.stringWidth(pressEnterMessage);
		int escWidth = pressMetrics.stringWidth(pressEscMessage);
		
		g2d.setFont(stateFont);
		g2d.drawString(stateMessage, (getWidth() - stateWidth) / 2, fontY - 60);
		g2d.setFont(pressFont);
		g2d.drawString(pressEnterMessage, (getWidth() - enterWidth) / 2, fontY);
		g2d.drawString(pressEscMessage, (getWidth() - escWidth) / 2, fontY + 60);
		
		if(gameLogic.hasBestUpdated()) {
			String best = "NEW BEST! " + gameLogic.getBest();
			int bestWidth = stateMetrics.stringWidth(best);
			g2d.setFont(stateFont);
			g2d.setColor(new Color(55, 125, 34));
			g2d.drawString(best, (getWidth() - bestWidth) / 2, fontY - 120);
		}
	}
	
	private void initKeyAdapter() {
		gameKeyAdapter = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(!gameLogic.isGameFinished()) {
					switch (e.getKeyCode()) {
					case KeyEvent.VK_DOWN:
						dir = Direction.down;
						break;
					case KeyEvent.VK_UP:
						dir = Direction.up;
						break;
					case KeyEvent.VK_RIGHT:
						dir = Direction.right;
						break;
					case KeyEvent.VK_LEFT:
						dir = Direction.left;
						break;
					}
				}
				else {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						restartGame();
					}
					else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						Component component = (Component) e.getSource();
						Window window = SwingUtilities.getWindowAncestor(component);
						window.dispose();
					}
				}
			}

		};
	}

	@SuppressWarnings("static-access")
	public void run() {
		int frameTimeNano = 1000000000 / 60;
		long nextFrame = System.nanoTime();

		int i = 0;

		while(!gameLogic.isGameFinished()) {
			nextFrame += frameTimeNano;
			i++;
			double fps = 60 / speed;
			if(i >= fps) {
				gameLogic.move(dir);
				i -= fps;
			}
			repaint();

			long curTime = System.nanoTime();

			if(curTime <= nextFrame) {
				try {
					Thread.currentThread().sleep((nextFrame - curTime) / 1000000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			setSpeed();			
		}
	}

	private void setSpeed() {
		if(prevApplesEaten != gameLogic.getApplesEaten()) {
			if(gameLogic.getApplesEaten() % 8 == 0) {
				speed += 0.3;
			}
			prevApplesEaten = gameLogic.getApplesEaten();
		}
	}

	//	public void setSnake(LinkedList<Integer[]> snake) {
	//		this.snake = snake;
	//	}

	private void getSnakeTiles() {
		LinkedList<Integer[]> snake = gameLogic.getSnake();
		snakeTiles = new int[snake.size()][2];
		for(int i = 0; i < snakeTiles.length; i++) {
			snakeTiles[i][0] = snake.get(i)[0];
			snakeTiles[i][1] = snake.get(i)[1];
		}

	}

	public void setGameLogic(SnakeGameLogic gameLogic) {
		this.gameLogic = gameLogic;
	}

	public void restartGame() {
	    gameLogic = new SnakeGameLogic();
	    dir = Direction.right;
	    speed = 6;
	    prevApplesEaten = 0;

	    Thread t = new Thread(this);
	    t.start();
	}


}
