import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class Index extends JFrame {
    private int birdX = 100, birdY = 200; // Bird's position
    private int birdVelocity = 0, gravity = 1; // Bird physics
    private int obstacleX1 = 800, obstacleX2 = 1100; // Obstacles
    private int obstacleGap = 200, obstacleWidth = 50, obstacleSpeed = 3;
    private int score = 0, lives = 3; // Score and lives
    private boolean isGameOver = false, isNight = false;
    private Timer gameTimer;
    private Random random = new Random();
    private int birdFlapFrame = 0; // Animation frame for flapping
    private ArrayList<int[]> clouds; // Stores cloud positions

    public Index() {
        setTitle("Flappy Bird Animation");
        setSize(800, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Initialize clouds
        clouds = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            clouds.add(new int[]{random.nextInt(800), random.nextInt(150)});
        }

        // Key bindings
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && !isGameOver) {
                    birdVelocity = -12; // Bird jumps
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER && isGameOver) {
                    restartGame(); // Restart game
                }
            }
        });

        startGame();
    }

    private void startGame() {
        gameTimer = new Timer(30, e -> {
            updateGame();
            repaint();
        });
        gameTimer.start();
    }

    private void updateGame() {
        if (isGameOver) return;

        // Bird animation
        birdFlapFrame = (birdFlapFrame + 1) % 3; // Cycle through frames (0, 1, 2)

        // Update bird's position
        birdVelocity += gravity;
        birdY += birdVelocity;

        // Update obstacles
        obstacleX1 -= obstacleSpeed;
        obstacleX2 -= obstacleSpeed;

        // Reset obstacles
        if (obstacleX1 + obstacleWidth < 0) {
            obstacleX1 = getWidth();
            score++;
        }
        if (obstacleX2 + obstacleWidth < 0) {
            obstacleX2 = getWidth();
            score++;
        }

        // Update clouds
        for (int[] cloud : clouds) {
            cloud[0] -= 1; // Move clouds left
            if (cloud[0] < -100) {
                cloud[0] = getWidth();
                cloud[1] = random.nextInt(150);
            }
        }

        // Check for collisions
        if (birdY <= 0 || birdY + 50 >= getHeight() - 50 || checkCollision()) {
            lives--;
            if (lives <= 0) {
                gameOver();
            } else {
                birdY = 200; // Reset bird position
                birdVelocity = 0;
            }
        }

        // Toggle day/night mode
        if (score > 0 && score % 10 == 0) {
            isNight = !isNight;
        }
    }

    private boolean checkCollision() {
        int pipeTop1 = getHeight() / 2 - obstacleGap / 2;
        int pipeBottom1 = getHeight() / 2 + obstacleGap / 2;

        // Check collision for first obstacle
        if (birdX + 50 > obstacleX1 && birdX < obstacleX1 + obstacleWidth) {
            if (birdY < pipeTop1 || birdY + 50 > pipeBottom1) {
                return true;
            }
        }

        // Check collision for second obstacle
        if (birdX + 50 > obstacleX2 && birdX < obstacleX2 + obstacleWidth) {
            if (birdY < pipeTop1 || birdY + 50 > pipeBottom1) {
                return true;
            }
        }

        return false;
    }

    private void gameOver() {
        gameTimer.stop();
        isGameOver = true;
        repaint();
        JOptionPane.showMessageDialog(this, "Game Over! Score: " + score + "\nPress ENTER to Restart.");
    }

    private void restartGame() {
        birdY = 200;
        birdVelocity = 0;
        obstacleX1 = getWidth();
        obstacleX2 = getWidth() + 300;
        obstacleSpeed = 3;
        score = 0;
        lives = 3;
        isGameOver = false;
        gameTimer.start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Background
        g.setColor(isNight ? Color.BLACK : Color.CYAN);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Clouds
        g.setColor(isNight ? Color.DARK_GRAY : Color.WHITE);
        for (int[] cloud : clouds) {
            g.fillOval(cloud[0], cloud[1], 100, 50);
        }

        // Ground
        g.setColor(Color.ORANGE);
        g.fillRect(0, getHeight() - 50, getWidth(), 50);

        // Obstacles
        g.setColor(Color.RED);
        int pipeTop1 = getHeight() / 2 - obstacleGap / 2;
        int pipeBottom1 = getHeight() / 2 + obstacleGap / 2;
        g.fillRect(obstacleX1, 0, obstacleWidth, pipeTop1);
        g.fillRect(obstacleX1, pipeBottom1, obstacleWidth, getHeight() - pipeBottom1);

        g.fillRect(obstacleX2, 0, obstacleWidth, pipeTop1);
        g.fillRect(obstacleX2, pipeBottom1, obstacleWidth, getHeight() - pipeBottom1);

        // Bird
        g.setColor(Color.GREEN);
        g.fillOval(birdX, birdY, 50, 50);
        g.setColor(Color.WHITE); // Flapping wings
        g.fillOval(birdX + 10, birdY + (birdFlapFrame == 1 ? 10 : 5), 30, 20);

        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 20, 50);
        g.drawString("Lives: " + lives, 20, 80);

        // Game Over
        if (isGameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("GAME OVER", getWidth() / 2 - 120, getHeight() / 2 - 20);
        }
    }

    public static void main(String[] args) {
        new Index();
    }
}
