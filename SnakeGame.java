package Games;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;

public class SnakeGame extends JPanel implements ActionListener {
    private Timer timer;
    private int delay = 100;
    private final int[] x = new int[600];
    private final int[] y = new int[600];
    private int bodyParts = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = true;

    // Modern color scheme
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private final Color GRID_COLOR = new Color(230, 230, 230);
    private final Color SNAKE_HEAD_COLOR = new Color(51, 51, 51);
    private final Color SNAKE_BODY_COLOR = new Color(75, 75, 75);
    private final Color APPLE_COLOR = new Color(235, 87, 87);
    private final Color TEXT_COLOR = new Color(51, 51, 51);

    private int gridSize = 20; // Larger grid size for modern look
    private final int GRID_ROWS = 30;
    private final int GRID_COLS = 30;
    private final int PANEL_SIZE = GRID_ROWS * gridSize;

    public SnakeGame() {
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        initGame();
    }

    private void initGame() {
        timer = new Timer(delay, this);
        timer.start();

        for (int i = 0; i < bodyParts; i++) {
            x[i] = (GRID_COLS / 2 - i) * gridSize;
            y[i] = GRID_ROWS / 2 * gridSize;
        }

        placeApple();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw grid
        drawGrid(g2d);

        if (running) {
            drawSnake(g2d);
            drawApple(g2d);
            drawScore(g2d);
        } else {
            showGameOver(g2d);
        }
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(GRID_COLOR);
        // Draw vertical lines
        for (int x = 0; x <= PANEL_SIZE; x += gridSize) {
            g2d.drawLine(x, 0, x, PANEL_SIZE);
        }
        // Draw horizontal lines
        for (int y = 0; y <= PANEL_SIZE; y += gridSize) {
            g2d.drawLine(0, y, PANEL_SIZE, y);
        }
    }

    private void drawSnake(Graphics2D g2d) {
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g2d.setColor(SNAKE_HEAD_COLOR);
            } else {
                g2d.setColor(SNAKE_BODY_COLOR);
            }
            // Draw rounded rectangles for snake segments
            g2d.fill(new RoundRectangle2D.Float(
                    x[i] + 2, y[i] + 2,
                    gridSize - 4, gridSize - 4,
                    8, 8
            ));
        }
    }

    private void drawApple(Graphics2D g2d) {
        g2d.setColor(APPLE_COLOR);
        g2d.fill(new Ellipse2D.Float(
                appleX + 4, appleY + 4,
                gridSize - 8, gridSize - 8
        ));
    }

    private void drawScore(Graphics2D g2d) {
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        String score = "Score: " + applesEaten;
        g2d.drawString(score, 10, 25);
    }

    private void showGameOver(Graphics2D g2d) {
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 40));
        String gameOver = "Game Over";
        FontMetrics metrics = getFontMetrics(g2d.getFont());
        g2d.drawString(gameOver,
                (PANEL_SIZE - metrics.stringWidth(gameOver)) / 2,
                PANEL_SIZE / 2 - 50
        );

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 20));
        String finalScore = "Final Score: " + applesEaten;
        metrics = getFontMetrics(g2d.getFont());
        g2d.drawString(finalScore,
                (PANEL_SIZE - metrics.stringWidth(finalScore)) / 2,
                PANEL_SIZE / 2 + 20
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            moveSnake();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    private void moveSnake() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'R': x[0] += gridSize; break;
            case 'L': x[0] -= gridSize; break;
            case 'U': y[0] -= gridSize; break;
            case 'D': y[0] += gridSize; break;
        }
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            applesEaten++;
            bodyParts++;
            placeApple();
        }
    }

    private void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }

        if (x[0] < 0 || x[0] >= PANEL_SIZE || y[0] < 0 || y[0] >= PANEL_SIZE) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    private void placeApple() {
        appleX = (int) (Math.random() * GRID_COLS) * gridSize;
        appleY = (int) (Math.random() * GRID_ROWS) * gridSize;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT:
                        if (game.direction != 'L') game.direction = 'R';
                        break;
                    case KeyEvent.VK_LEFT:
                        if (game.direction != 'R') game.direction = 'L';
                        break;
                    case KeyEvent.VK_UP:
                        if (game.direction != 'D') game.direction = 'U';
                        break;
                    case KeyEvent.VK_DOWN:
                        if (game.direction != 'U') game.direction = 'D';
                        break;
                }
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
