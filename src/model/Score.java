package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Score implements Serializable {
    private Date dateTime;
    private String playerName;
    private int score;

    public Score(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public static List<Score> loadScoresFromFile(String fileName) {
        List<Score> scores = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            while (true) {
                Score entry = (Score) ois.readObject();
                scores.add(entry);
            }
        } catch (EOFException e) {
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }

        return scores;
    }


}


