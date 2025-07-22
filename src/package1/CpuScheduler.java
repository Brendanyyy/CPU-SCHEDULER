package package1;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class CpuScheduler extends JFrame {
    private JTable inputTable, statusTable;
    private DefaultTableModel inputModel, statusModel;
    private JPanel ganttChartPanel;
    private JComboBox<String> algorithmCombo;
    private JTextField pidField, atField, btField, quantumField;
    private JButton addButton, resetButton, runButton;
    private JSlider speedSlider;

    private int currentTime = 0;
    private List<Process> processList = new ArrayList<>();
    private Queue<Process> readyQueue = new LinkedList<>();

    public CpuScheduler() {
        setTitle("CPU Scheduling Simulator");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set the background color for the whole frame
        getContentPane().setBackground(new Color(50, 50, 50));  // Dark grey background

        setupInputPanel();
        setupStatusPanel();
        setupControls();
        setupGanttChartPanel();

        setVisible(true);
    }

    private void setupInputPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.RED), "Process Input Queue", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.YELLOW));
    panel.setBackground(new Color(60, 60, 60));  // Darker grey for input panel
    panel.setPreferredSize(new Dimension(500, 600)); // Set width and height for input panel

    inputModel = new DefaultTableModel(new String[]{"#", "Process", "Arrival Time", "Burst Time"}, 0);
    inputTable = new JTable(inputModel);
    inputTable.setBackground(new Color(100, 100, 100));  // Lighter grey for table
    panel.add(new JScrollPane(inputTable), BorderLayout.CENTER);
    add(panel, BorderLayout.WEST);
}

    private void setupStatusPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.RED), "Real-Time Process Status", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.YELLOW));
    panel.setBackground(new Color(60, 60, 60));  // Darker grey for status panel
    panel.setPreferredSize(new Dimension(600, 600)); // Set width and height for status panel

    statusModel = new DefaultTableModel(new String[]{
        "Process", "Status", "Progress", "Remaining Time", "Wait Time",
        "Completion Time", "Turnaround Time", "Response Time"
    }, 0);

    statusTable = new JTable(statusModel);
    statusTable.setBackground(new Color(100, 100, 100));  // Lighter grey for table
    panel.add(new JScrollPane(statusTable), BorderLayout.CENTER);
    add(panel, BorderLayout.CENTER);
}

    private void setupControls() {
        JPanel controlPanel = new JPanel(new GridLayout(3, 1));
        controlPanel.setBackground(new Color(60, 60, 60));  // Dark grey for control panel

        JPanel inputForm = new JPanel();
        inputForm.setBackground(new Color(60, 60, 60));  // Same background for the input form
        pidField = new JTextField(3);
        atField = new JTextField(3);
        btField = new JTextField(3);
        addButton = new JButton("Add Process");
        resetButton = new JButton("Reset All");

        inputForm.add(new JLabel("PID:")); inputForm.add(pidField);
        inputForm.add(new JLabel("AT:")); inputForm.add(atField);
        inputForm.add(new JLabel("BT:")); inputForm.add(btField);
        inputForm.add(addButton);
        inputForm.add(resetButton);

        JButton randomButton = new JButton("Randomize");
        randomButton.setBackground(Color.RED); // Red button background
        randomButton.setForeground(Color.BLACK); // White text
        randomButton.addActionListener(e -> addRandomProcesses());
        inputForm.add(randomButton);

        addButton.addActionListener(e -> addProcess());
        resetButton.addActionListener(e -> resetAll());

        JPanel algorithmPanel = new JPanel();
        algorithmCombo = new JComboBox<>(new String[]{"FCFS", "SJF", "SRTF", "Round Robin", "MLFQ"});
        quantumField = new JTextField(3);
        runButton = new JButton("Run Simulation");
        runButton.setBackground(Color.YELLOW);
        runButton.setForeground(Color.RED);

        algorithmPanel.add(new JLabel("Algorithm:"));
        algorithmPanel.add(algorithmCombo);
        algorithmPanel.add(new JLabel("Quantum:"));
        algorithmPanel.add(quantumField);
        algorithmPanel.add(runButton);

        JPanel sliderPanel = new JPanel();
        speedSlider = new JSlider(10, 200, 100);
        sliderPanel.add(new JLabel("Speed:"));
        sliderPanel.add(speedSlider);

        runButton.addActionListener(e -> runSimulation());

        controlPanel.add(inputForm);
        controlPanel.add(algorithmPanel);
        controlPanel.add(sliderPanel);

        add(controlPanel, BorderLayout.NORTH);
    }

    private void setupGanttChartPanel() {
    ganttChartPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    ganttChartPanel.setPreferredSize(new Dimension(1000, 200)); // Increased height for Gantt chart
    ganttChartPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.RED), "Gantt Chart", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.YELLOW));
    ganttChartPanel.setBackground(Color.BLUE);  // Set background to blue for Gantt chart
    add(new JScrollPane(ganttChartPanel), BorderLayout.SOUTH);
}

    private void addRandomProcesses() {
        String input = JOptionPane.showInputDialog(this, "Enter number of processes to generate:", "Random Process Generator", JOptionPane.PLAIN_MESSAGE);

        if (input == null || input.isEmpty()) return;

        int numberOfProcesses;
        try {
            numberOfProcesses = Integer.parseInt(input);
            if (numberOfProcesses <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Random rand = new Random();
        inputModel.setRowCount(0);
        statusModel.setRowCount(0);
        processList.clear();

        for (int i = 1; i <= numberOfProcesses; i++) {
            String pid = "P" + i;
            int arrival = rand.nextInt(6);
            int burst = 1 + rand.nextInt(9);

            Process p = new Process(pid, arrival, burst);
            processList.add(p);

            inputModel.addRow(new Object[]{i, pid, arrival, burst});
            statusModel.addRow(new Object[]{pid, "Waiting", "0%", burst, 0});
        }
    }

    private void addProcess() {
        String pid = pidField.getText();
        int at = Integer.parseInt(atField.getText());
        int bt = Integer.parseInt(btField.getText());
        Process p = new Process(pid, at, bt);
        processList.add(p);
        inputModel.addRow(new Object[]{inputModel.getRowCount() + 1, pid, at, bt});
        statusModel.addRow(new Object[]{pid, "Waiting", "0%", bt, 0});
    }

    private void resetAll() {
        currentTime = 0;
        processList.clear();
        readyQueue.clear();
        inputModel.setRowCount(0);
        statusModel.setRowCount(0);
        ganttChartPanel.removeAll();
        ganttChartPanel.repaint();
    }

    private void runSimulation() {
        runButton.setEnabled(false);
        String algorithm = algorithmCombo.getSelectedItem().toString();
        ganttChartPanel.removeAll();
        currentTime = 0;
        readyQueue.clear();

        for (Process p : processList) {
            p.reset();
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                switch (algorithm) {
                    case "FCFS":
                        runFCFS();
                        break;
                    case "SJF":
                        runSJF();
                        break;
                    case "SRTF":
                        runSRTF();
                        break;
                    case "Round Robin":
                        runRoundRobin();
                        break;
                    case "MLFQ":
                        runMLFQ();
                        break;
                }
                return null;
            }

            @Override
            protected void done() {
                runButton.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void runFCFS() {
        List<Process> queue = new ArrayList<>(processList);
        queue.sort(Comparator.comparingInt(p -> p.arrivalTime));

        for (Process p : queue) {
            if (currentTime < p.arrivalTime) {
                while (currentTime < p.arrivalTime) {
                    addGanttLabel("Idle");
                    currentTime++;
                }
            }

            p.responseTime = currentTime - p.arrivalTime;
            p.responseRecorded = true;

            for (int i = 0; i < p.burstTime; i++) {
                p.remainingTime--;
                updateStatus(p);
                addGanttLabel(p.pid);
                currentTime++;

                try {
                    int delay = Math.max(5, 210 - speedSlider.getValue());
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            p.completed = true;
            p.completionTime = currentTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            int waitTime = p.turnaroundTime - p.burstTime;

            for (int i = 0; i < statusModel.getRowCount(); i++) {
                if (statusModel.getValueAt(i, 0).equals(p.pid)) {
                    statusModel.setValueAt("Completed", i, 1);
                    statusModel.setValueAt("100%", i, 2);
                    statusModel.setValueAt(0, i, 3);
                    statusModel.setValueAt(waitTime, i, 4);
                    statusModel.setValueAt(p.completionTime, i, 5);
                    statusModel.setValueAt(p.turnaroundTime, i, 6);
                    statusModel.setValueAt(p.responseTime, i, 7);
                    break;
                }
            }
        }

        double totalTAT = 0;
        double totalRT = 0;
        int count = processList.size();

        for (Process p : processList) {
            totalTAT += p.turnaroundTime;
            totalRT += p.responseTime;
        }

        double avgTAT = totalTAT / count;
        double avgRT = totalRT / count;

        JOptionPane.showMessageDialog(this,
                String.format("Average Turnaround Time: %.2f\nAverage Response Time: %.2f", avgTAT, avgRT));

        runButton.setEnabled(true);
    }

    private void runSJF() {
        List<Process> queue = new ArrayList<>(processList);
        int completed = 0;

        while (completed < queue.size()) {
            List<Process> available = queue.stream()
                    .filter(proc -> proc.arrivalTime <= currentTime && !proc.completed)
                    .collect(Collectors.toList());

            if (available.isEmpty()) {
                addGanttLabel("Idle");
                currentTime++;
                continue;
            }

            Process p = Collections.min(available, Comparator.comparingInt(proc -> proc.burstTime));

            if (p.startTime == -1) {
                p.startTime = currentTime;
                p.responseTime = currentTime - p.arrivalTime;
            }

            for (int i = 0; i < p.burstTime; i++) {
                p.remainingTime--;
                updateStatus(p);
                addGanttLabel(p.pid);
                currentTime++;

                try {
                    int delay = Math.max(5, 210 - speedSlider.getValue());
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            p.completed = true;
            p.completionTime = currentTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            completed++;

            for (int i = 0; i < statusModel.getRowCount(); i++) {
                if (statusModel.getValueAt(i, 0).equals(p.pid)) {
                    statusModel.setValueAt("Completed", i, 1);
                    statusModel.setValueAt("100%", i, 2);
                    statusModel.setValueAt(0, i, 3);
                    statusModel.setValueAt(p.waitingTime, i, 4);
                    statusModel.setValueAt(p.completionTime, i, 5);
                    statusModel.setValueAt(p.turnaroundTime, i, 6);
                    statusModel.setValueAt(p.responseTime, i, 7);
                    break;
                }
            }
        }

        double totalTAT = 0;
        double totalRT = 0;
        int count = processList.size();

        for (Process p : processList) {
            totalTAT += p.turnaroundTime;
            totalRT += p.responseTime;
        }

        double avgTAT = totalTAT / count;
        double avgRT = totalRT / count;

        JOptionPane.showMessageDialog(this,
                String.format("Average Turnaround Time: %.2f\nAverage Response Time: %.2f", avgTAT, avgRT));

        runButton.setEnabled(true);
    }

    private void runSRTF() {
        List<Process> queue = new ArrayList<>(processList);
        currentTime = 0;

        for (Process p : queue) {
            p.remainingTime = p.burstTime;
            p.completed = false;
            p.startTime = -1;
        }

        while (true) {
            Process p = queue.stream()
                    .filter(proc -> proc.arrivalTime <= currentTime && !proc.completed && proc.remainingTime > 0)
                    .min(Comparator.comparingInt(proc -> proc.remainingTime))
                    .orElse(null);

            if (p == null) {
                if (queue.stream().allMatch(proc -> proc.completed)) break;

                addGanttLabel("Idle");
                currentTime++;
                try {
                    Thread.sleep(Math.max(5, 210 - speedSlider.getValue()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            if (p.startTime == -1) {
                p.startTime = currentTime;
                p.responseTime = currentTime - p.arrivalTime;
            }

            p.remainingTime--;
            updateStatus(p);
            addGanttLabel(p.pid);
            currentTime++;

            if (p.remainingTime == 0) {
                p.completed = true;
                p.completionTime = currentTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;

                for (int i = 0; i < statusModel.getRowCount(); i++) {
                    if (statusModel.getValueAt(i, 0).equals(p.pid)) {
                        statusModel.setValueAt("Completed", i, 1);
                        statusModel.setValueAt("100%", i, 2);
                        statusModel.setValueAt(0, i, 3);
                        statusModel.setValueAt(p.waitingTime, i, 4);
                        statusModel.setValueAt(p.completionTime, i, 5);
                        statusModel.setValueAt(p.turnaroundTime, i, 6);
                        statusModel.setValueAt(p.responseTime, i, 7);
                        break;
                    }
                }
            }

            try {
                Thread.sleep(Math.max(5, 210 - speedSlider.getValue()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        double totalTAT = 0;
        double totalRT = 0;
        int count = processList.size();

        for (Process p : processList) {
            totalTAT += p.turnaroundTime;
            totalRT += p.responseTime;
        }

        double avgTAT = totalTAT / count;
        double avgRT = totalRT / count;

        JOptionPane.showMessageDialog(this,
                String.format("Average Turnaround Time: %.2f\nAverage Response Time: %.2f", avgTAT, avgRT));

        runButton.setEnabled(true);
    }

    private void runRoundRobin() {
    int quantum = Integer.parseInt(quantumField.getText());
    List<Process> queue = new ArrayList<>(processList); // Unarrived processes
    Queue<Process> rrQueue = new LinkedList<>();
    int time = 0;

    // Ensure all remaining times are initialized
    for (Process p : queue) {
        p.remainingTime = p.burstTime;
        p.completed = false;
        p.responseRecorded = false;
    }

    while (!queue.isEmpty() || !rrQueue.isEmpty()) {
        // Move processes that have arrived to the ready queue
        for (Iterator<Process> it = queue.iterator(); it.hasNext(); ) {
            Process p = it.next();
            if (p.arrivalTime <= time) {
                rrQueue.add(p);
                it.remove();
            }
        }

        if (rrQueue.isEmpty()) {
            addGanttLabel("Idle");
            time++;
            currentTime = time;
            continue;
        }

        Process p = rrQueue.poll();
        int execTime = Math.min(quantum, p.remainingTime);

        // Record response time at first execution
        if (!p.responseRecorded) {
            p.responseTime = time - p.arrivalTime;
            p.responseRecorded = true;
        }

        for (int i = 0; i < execTime; i++) {
            p.remainingTime--;
            time++;
            currentTime = time;

            updateStatus(p);
            addGanttLabel(p.pid);

            // Check for newly arrived processes during execution
            for (Iterator<Process> it = queue.iterator(); it.hasNext(); ) {
                Process arriving = it.next();
                if (arriving.arrivalTime <= time) {
                    rrQueue.add(arriving);
                    it.remove();
                }
            }

            try {
                Thread.sleep(Math.max(5, 210 - speedSlider.getValue()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (p.remainingTime == 0) break; // finished
        }

        if (p.remainingTime > 0) {
            rrQueue.add(p); // Still needs CPU time
        } else {
            p.completionTime = time;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            p.completed = true;
        }
    }

    // Calculate average Turnaround Time and Response Time
    double totalTAT = 0;
    double totalRT = 0;
    int count = 0;

    // Use the process list with completed processes
    for (Process p : processList) {
        if (p.completed) { // Only consider completed processes
            totalTAT += p.turnaroundTime;
            totalRT += p.responseTime;
            count++;
        }
    }

    if (count > 0) {
        double avgTAT = totalTAT / count;
        double avgRT = totalRT / count;

        JOptionPane.showMessageDialog(this,
                String.format("Average Turnaround Time: %.2f\nAverage Response Time: %.2f", avgTAT, avgRT));
    }

    runButton.setEnabled(true);
}



    private void runMLFQ() {
    int levels;
    try {
        String input = JOptionPane.showInputDialog(this, "Enter number of MLFQ levels (e.g., 4):");
        if (input == null) return;
        levels = Integer.parseInt(input.trim());
        if (levels <= 0) throw new NumberFormatException();
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid number of levels.");
        return;
    }

    int[] quantums = new int[levels];
    int[] allotments = new int[levels];

    // Calculate the total burst time of all processes
    int totalBurstTime = 0;
    for (Process p : processList) {
        totalBurstTime += p.burstTime;
    }

    // Get user input for quantum and allotment
    int totalQuantumAndAllotment = 0;
    for (int i = 0; i < levels; i++) {
        try {
            String q = JOptionPane.showInputDialog(this, "Quantum for Q" + i + ":");
            String a = JOptionPane.showInputDialog(this, "Allotment for Q" + i + ":");
            if (q == null || a == null) return;
            quantums[i] = Integer.parseInt(q.trim());
            allotments[i] = Integer.parseInt(a.trim());
            totalQuantumAndAllotment += quantums[i] * allotments[i]; // Total quantum and allotment
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantum/allotment for level " + i);
            return;
        }
    }

    // Check if total burst time is greater than total quantum and allotment
    if (totalBurstTime > totalQuantumAndAllotment) {
        JOptionPane.showMessageDialog(this, "Total burst time exceeds the total quantum and allotment. Please input greater values.");
        return;
    }

    List<Queue<Process>> queues = new ArrayList<>();
    for (int i = 0; i < levels; i++) queues.add(new LinkedList<>());

    List<Process> queue = new ArrayList<>();
    for (Process p : processList) {
        p.reset();
        queue.add(p);
    }

    Map<String, Integer> usedAllotment = new HashMap<>();
    int time = 0;
    currentTime = 0;

    while (!queue.isEmpty() || queues.stream().anyMatch(q -> !q.isEmpty())) {
        // Add newly arrived processes to top queue (Q0)
        Iterator<Process> it = queue.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (p.arrivalTime <= time) {
                queues.get(0).offer(p);
                usedAllotment.put(p.pid, 0);
                it.remove();
            }
        }

        Process current = null;
        int level = -1;
        for (int i = 0; i < levels; i++) {
            if (!queues.get(i).isEmpty()) {
                current = queues.get(i).poll();
                level = i;
                break;
            }
        }

        if (current == null) {
            addGanttLabel("Idle");
            try {
                Thread.sleep(Math.max(5, 210 - speedSlider.getValue()));
            } catch (InterruptedException e) {}
            time++;
            currentTime = time;
            continue;
        }

        int quantum = quantums[level];
        int allotLeft = allotments[level] - usedAllotment.get(current.pid);
        int execTime = Math.min(current.remainingTime, Math.min(quantum, allotLeft));

        for (int i = 0; i < execTime; i++) {
            if (!current.started) {
                current.startTime = time;
                current.responseTime = current.startTime - current.arrivalTime; 
                current.started = true;
            }

            addGanttLabel(current.pid);
            current.remainingTime--;
            usedAllotment.put(current.pid, usedAllotment.get(current.pid) + 1);
            time++;
            currentTime = time;

            updateStatus(current);

            // Check for new arrivals
            Iterator<Process> it2 = queue.iterator();
            while (it2.hasNext()) {
                Process p = it2.next();
                if (p.arrivalTime <= time) {
                    queues.get(0).offer(p);
                    usedAllotment.put(p.pid, 0);
                    it2.remove();
                }
            }

            try {
                Thread.sleep(Math.max(5, 210 - speedSlider.getValue()));
            } catch (InterruptedException e) {}
        }

        if (current.remainingTime == 0) {
            current.completionTime = time;
            current.turnaroundTime = current.completionTime - current.arrivalTime;
            current.waitingTime = current.turnaroundTime - current.burstTime;
            current.responseTime = current.startTime - current.arrivalTime;
            current.completed = true;

            // Update final status row
            for (int i = 0; i < statusModel.getRowCount(); i++) {
                if (statusModel.getValueAt(i, 0).equals(current.pid)) {
                    statusModel.setValueAt("Completed", i, 1);
                    statusModel.setValueAt("100%", i, 2);
                    statusModel.setValueAt(0, i, 3);
                    statusModel.setValueAt(current.waitingTime, i, 4);
                    statusModel.setValueAt(current.completionTime, i, 5);
                    statusModel.setValueAt(current.turnaroundTime, i, 6);
                    statusModel.setValueAt(current.responseTime, i, 7);
                    break;
                }
            }
        } else {
            if (usedAllotment.get(current.pid) >= allotments[level] && level < levels - 1) {
                queues.get(level + 1).offer(current);
                usedAllotment.put(current.pid, 0);
            } else {
                queues.get(level).offer(current);
            }
        }
    }

    // Calculate and display average turnaround and response times
    double totalTAT = 0;
    double totalRT = 0;
    int count = processList.size();

    for (Process p : processList) {
        totalTAT += p.turnaroundTime;
        totalRT += p.responseTime;
    }

    double avgTAT = totalTAT / count;
    double avgRT = totalRT / count;

    JOptionPane.showMessageDialog(this,
        String.format("Average Turnaround Time: %.2f\nAverage Response Time: %.2f", avgTAT, avgRT));

    runButton.setEnabled(true);
}

    private void updateStatus(Process p) {
        for (int i = 0; i < statusModel.getRowCount(); i++) {
            String pid = statusModel.getValueAt(i, 0).toString();
            if (pid.equals(p.pid)) {
                int completed = p.burstTime - p.remainingTime;
                int percent = (int) (((double) completed / p.burstTime) * 100);

                if (!p.responseRecorded && completed > 0) {
                    p.responseTime = currentTime - p.arrivalTime;
                    p.responseRecorded = true;
                }

                if (p.remainingTime == 0 && p.completionTime == -1) {
                    p.completionTime = currentTime;
                    p.turnaroundTime = p.completionTime - p.arrivalTime;
                }

                statusModel.setValueAt(p.remainingTime == 0 ? "Completed" : "Running", i, 1);
                statusModel.setValueAt(percent + "%", i, 2);
                statusModel.setValueAt(p.remainingTime, i, 3);
                statusModel.setValueAt(currentTime - p.arrivalTime - completed, i, 4);
                statusModel.setValueAt(p.completionTime == -1 ? "" : p.completionTime, i, 5);
                statusModel.setValueAt(p.turnaroundTime == -1 ? "" : p.turnaroundTime, i, 6);
                statusModel.setValueAt(p.responseTime == -1 ? "" : p.responseTime, i, 7);
            }
        }
    }

    private void addGanttLabel(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(text.equals("Idle") ? Color.GRAY : Color.CYAN);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setPreferredSize(new Dimension(40, 30));
        ganttChartPanel.add(label);
        ganttChartPanel.revalidate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CpuScheduler::new);
    }
}
