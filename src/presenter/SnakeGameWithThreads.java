package presenter;

import model.Score;
import view.SnakeGameMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;


public class SnakeGameWithThreads extends JPanel implements ActionListener {
    private int width = 500;
    private int height = 500;
    private int gridSize = 10;
    private int snakeLength = 3;
    private int score = 0;

    private int[] snakeX = new int[width * height];
    private int[] snakeY = new int[width * height];

    private int foodX;
    private int foodY;

    private boolean isPlaying = true;
    private boolean isMovingRight = true;
    private boolean isMovingLeft = false;
    private boolean isMovingUp = false;
    private boolean isMovingDown = false;


    private Random random;
    private ScoreThread scoreThread;
    private FoodThread foodThread;
    private ObstacleThread obstacleThread;
    private boolean isObstacleActive = false;
    private int obstacleX;
    private int obstacleY;
    private int obstacleWidth = gridSize;
    private int obstacleHeight = gridSize;
    private boolean isMoving = false;
    private MoveThread moveThread;
    private int snakeSpeed;
    private int foodCountSinceLastSpeedIncrease = 0;
    private int obstacleFrequency;
    private String playerName;
    JButton restartButton = new JButton("Reiniciar");
    private List<Score> scoreList;

    public void setSnakeSpeed(int snakeSpeed) {
        this.snakeSpeed = snakeSpeed;
    }

    public void setObstacleFrequency(int obstacleFrequency) {
        this.obstacleFrequency = obstacleFrequency;
    }

    public SnakeGameWithThreads() {
        obstacleFrequency = 4;
        scoreList = new ArrayList<>();
        this.playerName = playerName;
        random = new Random();
        obstacleThread = new ObstacleThread();
        foodThread = new FoodThread();
        snakeSpeed = 100;
        foodCountSinceLastSpeedIncrease = 0;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.black);
        setFocusable(true);

        restartButton.setVisible(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        add(restartButton);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (!isMoving) {
                    isMoving = true;
                    moveThread = new MoveThread();
                    moveThread.start();
                }
                if ((key == KeyEvent.VK_RIGHT) && (!isMovingLeft)) {
                    isMovingRight = true;
                    isMovingUp = false;
                    isMovingDown = false;
                }
                if ((key == KeyEvent.VK_LEFT) && (!isMovingRight)) {
                    isMovingLeft = true;
                    isMovingUp = false;
                    isMovingDown = false;
                }
                if ((key == KeyEvent.VK_UP) && (!isMovingDown)) {
                    isMovingUp = true;
                    isMovingRight = false;
                    isMovingLeft = false;
                }
                if ((key == KeyEvent.VK_DOWN) && (!isMovingUp)) {
                    isMovingDown = true;
                    isMovingRight = false;
                    isMovingLeft = false;
                }
            }
        });

        isMovingRight = true;
        isMovingLeft = false;
        isMovingUp = false;
        isMovingDown = false;

        isMoving = true;
        moveThread = new MoveThread();
        moveThread.start();

        initGame();

        foodThread.start();
        obstacleThread.start();

    }

    public List<Score> getScoreList() {
        return scoreList;
    }

    public void addScore(String playerName, int score) {
        scoreList.add(new Score(playerName, score));
        saveScoreList();
    }

    private void saveScoreList() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D://IntelliJ IDEA//SnakeGame//scores.dat"))) {
            oos.writeObject(scoreList);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadScoreList() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("D://IntelliJ IDEA//SnakeGame//scores.dat"))) {
            scoreList = (List<Score>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void restartGame() {
        isPlaying = false;
        scoreThread.interrupt();
        foodThread.interrupt();
        obstacleThread.interrupt();
        moveThread.interrupt();

        initGame();
        isMoving = false;
        repaint();

        restartButton.setVisible(false);

        JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        gameFrame.dispose();
        SnakeGameWithThreads game = new SnakeGameWithThreads();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SnakeGameMenu(game);
            }
        });
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    private void increaseSpeed() {
        snakeSpeed -= 10;
    }

    private class MoveThread extends Thread {
        @Override
        public void run() {
            while (isPlaying) {
                if (isMoving) {
                    move();
                    checkCollision();
                }
                repaint();
                try {
                    Thread.sleep(snakeSpeed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ObstacleThread extends Thread {
        @Override
        public void run() {
            while (isPlaying) {
                try {
                    Thread.sleep((random.nextInt(4) + 3) * 1000);
                    if (isPlaying) {
                        isObstacleActive = true;
                        obstacleX = random.nextInt(width / gridSize) * gridSize;
                        obstacleY = random.nextInt(height / gridSize) * gridSize;
                        repaint();

                        Thread.sleep((random.nextInt(4) + 3) * 1000);
                        if (isPlaying) {
                            isObstacleActive = false;
                            repaint();
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private class FoodThread extends Thread {
        @Override
        public void run() {
            while (isPlaying) {
                try {
                    Thread.sleep(obstacleFrequency * 1000);
                    if (isPlaying) {
                        spawnFood();
                        repaint();
                    }
                } catch (InterruptedException e) {
                    break;

                }
            }
        }
    }

    public void initGame() {
        for (int i = 0; i < snakeLength; i++) {
            snakeX[i] = 50 - i * gridSize;
            snakeY[i] = 50;
        }

        spawnFood();
        score = 0;
        isPlaying = true;

        scoreThread = new ScoreThread();
        scoreThread.start();
    }

    public void spawnFood() {
        foodX = random.nextInt(width / gridSize) * gridSize;
        foodY = random.nextInt(height / gridSize) * gridSize;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isPlaying) {
            move();
            checkCollision();
        }
        repaint();
    }

    public void move() {
        for (int i = snakeLength; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        if (isMovingRight) {
            snakeX[0] += gridSize;
        }
        if (isMovingLeft) {
            snakeX[0] -= gridSize;
        }
        if (isMovingUp) {
            snakeY[0] -= gridSize;
        }
        if (isMovingDown) {
            snakeY[0] += gridSize;
        }

        if (snakeX[0] == foodX && snakeY[0] == foodY) {
            snakeLength++;
            spawnFood();
            score++;

            foodCountSinceLastSpeedIncrease++;

            if (foodCountSinceLastSpeedIncrease == 4) {
                increaseSpeed();
                foodCountSinceLastSpeedIncrease = 0;
            }
        }
    }


    public void checkCollision() {

        if (snakeX[0] >= width) {
            snakeX[0] = 0;
        } else if (snakeX[0] < 0) {
            snakeX[0] = width - gridSize;
        }
        if (snakeY[0] >= height) {
            snakeY[0] = 0;
        } else if (snakeY[0] < 0) {
            snakeY[0] = height - gridSize;

        }
        if (isObstacleActive && snakeX[0] == obstacleX && snakeY[0] == obstacleY) {
            isPlaying = false;

            restartButton.setVisible(true);
        }
        for (int i = 1; i < snakeLength; i++) {
            if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                isPlaying = false;

            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isPlaying) {
            for (int i = 0; i < snakeLength; i++) {
                g.setColor(Color.green);
                g.fillRect(snakeX[i], snakeY[i], gridSize, gridSize);
            }

            g.setColor(Color.red);
            g.fillOval(foodX, foodY, gridSize, gridSize);

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("Score: " + score, 10, 15);
        } else {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Game Over", width / 2 - 60, height / 2 - 10);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("Score: " + score, width / 2 - 20, height / 2 + 10);
        }
        if (isObstacleActive) {
            g.setColor(Color.blue);  // Cambiar el color del obstáculo
            g.fillRect(obstacleX, obstacleY, obstacleWidth, obstacleHeight);
        }
    }

    private class ScoreThread extends Thread {
        @Override
        public void run() {
            while (isPlaying) {
                try {
                    Thread.sleep(1000);
                    if (isPlaying && snakeX[0] == foodX && snakeY[0] == foodY) {
                        score++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {
        SnakeGameWithThreads snakeGame = new SnakeGameWithThreads();

        JFrame frame = new JFrame("Snake Game");
        frame.add(snakeGame);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SnakeGameMenu(snakeGame);
            }
        });
    }
}