package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Game {

	private JFrame gameFrame = new JFrame();
	final private int height = 620;
	final private int width = 524;

	private JPanel startPanel;
	private int rectWidth = 200;
	private int rectHeight = 100;
	private int mainButtonX = (width - rectWidth) / 2;
	private int mainButtonY = 25;

	private MouseAdapter menuMouseAdapter;

	private KeyAdapter menuKeyAdapter;

	private SnakeGamePanel snakeGamePanel;

	private SnakeGameLogic gameLogic;

	public void start() {

		initStartPanel();
		gameFrame.setContentPane(startPanel);
		gameFrame.pack();

		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setResizable(false);
		gameFrame.setLocationRelativeTo(null);

		gameFrame.setVisible(true);
	}

	private void initStartPanel() {

		JLabel startButton = new JLabel("Start Game");
		startButton.setBackground(Color.black);
		startButton.setForeground(Color.white);
		startButton.setOpaque(true);
		startButton.setHorizontalAlignment(SwingConstants.CENTER);
		startButton.setVerticalTextPosition(SwingConstants.CENTER);
		startButton.setFont(new Font("Arial", Font.BOLD, 30));
		startButton.setBorder(BorderFactory.createLineBorder(Color.white, 2, true));
		startButton.setBounds(mainButtonX, mainButtonY, rectWidth, rectHeight);

		JLabel bestMessage = new JLabel("BEST SCORE: " + readBest());
		bestMessage.setBackground(Color.black);
		bestMessage.setForeground(new Color(68, 156, 42));
		bestMessage.setOpaque(true);
		bestMessage.setHorizontalAlignment(SwingConstants.CENTER);
		bestMessage.setVerticalTextPosition(SwingConstants.CENTER);
		bestMessage.setFont(new Font("Arial", Font.BOLD, 30));
		bestMessage.setBounds(mainButtonX - 50, mainButtonY * 10, 300, 100);

		startPanel = new JPanel(null);
		startPanel.setBackground(Color.black);
		startPanel.setPreferredSize(new Dimension(width, height));
		startPanel.add(bestMessage);
		startPanel.add(startButton);

		initMenuMouseAdapter();
		initMenuKeyAdapter();
		startPanel.setFocusable(true);
	}

	public void initMenuMouseAdapter() {
		menuMouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int mouseX = e.getX();
				int mouseY = e.getY();
				if(mouseX >= mainButtonX && mouseX <= mainButtonX + rectWidth
						&& mouseY >= mainButtonY && mouseY <= mainButtonY + rectHeight) {
					gameLogic = new SnakeGameLogic();
					initGamePanel();
					Thread t = new Thread(snakeGamePanel);
					t.start();
				}
			}
		};

		startPanel.addMouseListener(menuMouseAdapter);
	}

	public void initMenuKeyAdapter() {
		menuKeyAdapter = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					gameLogic = new SnakeGameLogic();
					initGamePanel();
					Thread t = new Thread(snakeGamePanel);
					t.start();
				}
			}
		};
		startPanel.addKeyListener(menuKeyAdapter);
	}

	protected void initGamePanel() {

		startPanel.setFocusable(false);
		if(snakeGamePanel == null) {
			snakeGamePanel = new SnakeGamePanel();
		}

		snakeGamePanel.setPreferredSize(new Dimension(width, height));
		snakeGamePanel.setFocusable(true);

		gameFrame.setContentPane(snakeGamePanel);
		gameFrame.pack();
		gameFrame.setVisible(true);

		snakeGamePanel.requestFocusInWindow();

		snakeGamePanel.setGameLogic(gameLogic);
	}

	private int readBest() {
		File bestFile = new File("C:\\Users\\maaya\\eclipse-workspace\\snake\\res\\best.txt");
		int best = 0;
		if(bestFile.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(bestFile))) {
				String line = br.readLine();
				best = Integer.parseInt(line);
			}
				catch (IOException e) {
					System.out.println(e.getClass().toString() + ": " + e.getMessage());
				}
			}
		return best;
	}
}

