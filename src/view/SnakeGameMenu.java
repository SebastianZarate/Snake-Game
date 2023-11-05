package view;

import model.Score;
import model.ScoreLoader;
import model.ScoreboardPage;
import presenter.SnakeGameWithThreads;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SnakeGameMenu extends JFrame {

    private SnakeGameWithThreads game;

    public SnakeGameMenu(SnakeGameWithThreads game) {

        this.game = game;
        setTitle("Snake Game Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1));


        JPanel namePanel = new JPanel();
        JLabel nameLabel = new JLabel("Nombre:");
        JTextField nameField = new JTextField(18);
        namePanel.add(nameLabel);
        namePanel.add(nameField);


        JPanel levelButtonPanel = new JPanel();
        ButtonGroup levelGroup = new ButtonGroup();

        JRadioButton easyButton = new JRadioButton("Fácil");
        easyButton.setActionCommand("easy");
        JRadioButton mediumButton = new JRadioButton("Medio");
        mediumButton.setActionCommand("medium");
        JRadioButton hardButton = new JRadioButton("Difícil");
        hardButton.setActionCommand("hard");

        levelGroup.add(easyButton);
        levelGroup.add(mediumButton);
        levelGroup.add(hardButton);

        levelButtonPanel.add(easyButton);
        levelButtonPanel.add(mediumButton);
        levelButtonPanel.add(hardButton);

        mainPanel.add(levelButtonPanel);

        JButton startButton2 = new JButton("Iniciar Juego");
        startButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = nameField.getText();
                if (!playerName.isEmpty()) {
                    String selectedLevel = levelGroup.getSelection().getActionCommand();
                    dispose();
                    startGame(playerName, selectedLevel);
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese su nombre antes de iniciar el juego.", "Nombre requerido", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JPanel startButtonPanel = new JPanel();
        JButton startButton = new JButton("Iniciar Juego");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = nameField.getText();
                if (!playerName.isEmpty()) {
                    String selectedLevel = levelGroup.getSelection().getActionCommand();
                    dispose();
                    startGame(playerName, selectedLevel);
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese su nombre antes de iniciar el juego.", "Nombre requerido", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        startButtonPanel.add(startButton);

        JPanel scoreButtonPanel = new JPanel();
        JButton scoreButton = new JButton("Historial de Puntuaciones");
        scoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Score> scoreEntries = Score.loadScoresFromFile("D://IntelliJ IDEA//SnakeGame//scores.dat");

                game.loadScoreList();
                new ScoreboardPage(game.getScoreList());
                for (Score entry : scoreEntries) {
                    System.out.println("Nombre: " + entry.getPlayerName() + ", Puntuación: " + entry.getScore());
                }
            }
        });
        scoreButtonPanel.add(scoreButton);

        JPanel developerInfoPanel = new JPanel();
        developerInfoPanel.setLayout(new GridLayout(7, 1));

        JPanel mainPanel2 = new JPanel(new BorderLayout());


        String[] developerInfo = {
                " Juan Sebastian Zárate Ortiz",
                " 1002582896",
                " Edgar Meneses",
                " Ingeniería de Sistemas y Computación",
                " 2023",
                " Semestre 5",
        };



        for (String info : developerInfo) {
            JLabel infoLabel = new JLabel(info);
            developerInfoPanel.add(infoLabel);
        }

        ImageIcon developerImage = new ImageIcon("D:/IntelliJ IDEA/SnakeGame/uptc.png");
        developerImage.setImage(developerImage.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT));

        JLabel imageLabel = new JLabel(developerImage);
        imageLabel.setPreferredSize(new Dimension(100, 100));


        mainPanel.add(namePanel);
        mainPanel.add(startButtonPanel);
        mainPanel.add(scoreButtonPanel);
        mainPanel.add(developerInfoPanel);
        mainPanel.add(imageLabel);

        add(mainPanel);
        setVisible(true);
    }

    private void startGame(String playerName, String selectedLevel) {
        JFrame gameFrame = new JFrame("Snake Game");
        SnakeGameWithThreads snakeGame = new SnakeGameWithThreads();

        if ("easy".equals(selectedLevel)) {
            snakeGame.setSnakeSpeed(150);
            snakeGame.setObstacleFrequency(6);
        } else if ("medium".equals(selectedLevel)) {
            snakeGame.setSnakeSpeed(100);
            snakeGame.setObstacleFrequency(4);
        } else if ("hard".equals(selectedLevel)) {
            snakeGame.setSnakeSpeed(75);
            snakeGame.setObstacleFrequency(3);
        }

        snakeGame.setPlayerName(playerName);
        gameFrame.add(snakeGame);
        gameFrame.pack();
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);
    }


}