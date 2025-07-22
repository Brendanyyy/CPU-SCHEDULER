package package1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;

public class Simulation {
    private List<Process> processList;
    private int currentTime = 0;

    public Simulation(List<Process> processList) {
        this.processList = processList;
    }

    public void run(String algorithm, JPanel ganttChartPanel, DefaultTableModel statusModel) {
        switch (algorithm) {
            case "FCFS":
                runFCFS(ganttChartPanel, statusModel);
                break;
            case "SJF":
                runSJF(ganttChartPanel, statusModel);
                break;
            case "SRTF":
                runSRTF(ganttChartPanel, statusModel);
                break;
            case "Round Robin":
                runRoundRobin(ganttChartPanel, statusModel);
                break;
            case "MLFQ":
                runMLFQ(ganttChartPanel, statusModel);
                break;
            default:
                throw new IllegalArgumentException("Unknown scheduling algorithm: " + algorithm);
        }
    }

    private void runFCFS(JPanel ganttChartPanel, DefaultTableModel statusModel) {
        // Implement FCFS logic
    }

    private void runSJF(JPanel ganttChartPanel, DefaultTableModel statusModel) {
        // Implement SJF logic
    }

    private void runSRTF(JPanel ganttChartPanel, DefaultTableModel statusModel) {
        // Implement SRTF logic
    }

    private void runRoundRobin(JPanel ganttChartPanel, DefaultTableModel statusModel) {
        // Implement Round Robin logic
    }

    private void runMLFQ(JPanel ganttChartPanel, DefaultTableModel statusModel) {
        // Implement MLFQ logic
    }
}
