package model;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ScoreboardPage extends JFrame {
    public ScoreboardPage(List<Score> scoreList) {
        setTitle("Historial de Puntuaciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columnNames = {"Nombre", "Puntuación", "Fecha y Hora"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        for (Score score : scoreList) {
            model.addRow(new Object[]{score.getPlayerName(), score.getScore(), score.getDateTime()});
        }

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
