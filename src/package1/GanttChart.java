package package1;

import javax.swing.*;
import java.awt.*;

public class GanttChart {
    private JPanel ganttChartPanel;

    public GanttChart(JPanel ganttChartPanel) {
        this.ganttChartPanel = ganttChartPanel;
    }

    public void addGanttLabel(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(text.equals("Idle") ? Color.GRAY : Color.CYAN);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setPreferredSize(new Dimension(50, 30));
        ganttChartPanel.add(label);
        ganttChartPanel.revalidate();
    }

    public void resetGanttChart() {
        ganttChartPanel.removeAll();
        ganttChartPanel.revalidate();
        ganttChartPanel.repaint();
    }

    public void updateGanttChart(String text) {
        addGanttLabel(text);
    }
}
