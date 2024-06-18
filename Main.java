// Nicholas Tiow Kai Bo 1211102398
// Low Kai Yan 1211101699
// Wong Jui Hong 1211101452
// Wong Wei Ping 1211102080


import javax.swing.*;
import javax.swing.table.*;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Scheduler selection setup
        String[] schedulers = { "Round Robin", "Non Preemptive SJF", "Preemptive SJF", "Non Preemptive Priority"};
        JComboBox<String> comboBox = new JComboBox<>(schedulers);
        int schedulerChoice = JOptionPane.showConfirmDialog(null, comboBox, "Select a scheduler", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (schedulerChoice != JOptionPane.OK_OPTION) {
            return; // Exit if user did not confirm their choice
        }
        String selectedScheduler = (String) comboBox.getSelectedItem();

        // Initialize the simulator
        CPUSchedulingSimulator simulator = new CPUSchedulingSimulator(3);

        // Adding processes based on user input and make sure input is from 3 - 10
        
        int processCount = Integer.parseInt(JOptionPane.showInputDialog("Enter number of processes: "));
        while (processCount < 3 || processCount > 10) {
            processCount = Integer.parseInt(JOptionPane.showInputDialog("Enter number of processes (3 - 10): "));
        }
        
        
        
        JPanel panel = new JPanel(new GridLayout(processCount + 1, 4));
        
        
        panel.add(new JLabel("Process ID"));
        panel.add(new JLabel("Arrival Time"));
        panel.add(new JLabel("Burst Time"));
        panel.add(new JLabel("Priority"));

        List<JTextField> arrivalTimeFields = new ArrayList<>();
        List<JTextField> burstTimeFields = new ArrayList<>();
        List<JTextField> priorityFields = new ArrayList<>();

        for (int i = 0; i < processCount; i++) {
            panel.add(new JLabel("Process " + i));
            JTextField arrivalTimeField = new JTextField(5);
            JTextField burstTimeField = new JTextField(5);
            JTextField priorityField = new JTextField(5);

            panel.add(arrivalTimeField);
            panel.add(burstTimeField);
            panel.add(priorityField);

            arrivalTimeFields.add(arrivalTimeField);
            burstTimeFields.add(burstTimeField);
            priorityFields.add(priorityField);
        }

        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Process Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; i < processCount; i++) {
                int arrivalTime = Integer.parseInt(arrivalTimeFields.get(i).getText());
                int burstTime = Integer.parseInt(burstTimeFields.get(i).getText());
                int priority = Integer.parseInt(priorityFields.get(i).getText());
                simulator.addProcess(new Process(i, arrivalTime, burstTime, priority));
            }
        }

        // Check the selected scheduler and perform scheduling
        if ("Round Robin".equals(selectedScheduler)) {
            System.out.println("Round Robin");
            simulator.roundRobin();
        } else if ("Non Preemptive SJF".equals(selectedScheduler)) {
            System.out.println("Non Preemptive SJF");
            simulator.nonSJF();
        } else if ("Preemptive SJF".equals(selectedScheduler)) {
            System.out.println("Preemptive SJF");
            simulator.preemptiveSJF();
        } else if ("Non Preemptive Priority".equals(selectedScheduler)) {
            System.out.println("Non Preemptive Priority");
            simulator.nonPreemptivePriority();
        }


        // Create table data
        String[] columnNames = { "Process ID", "Arrival Time", "Finish Time", "Turnaround Time", "Burst Time",
                "Waiting Time", "Priority"};
        Object[][] data = new Object[simulator.processes.size()][7];
        for (int i = 0; i < simulator.processes.size(); i++) {
            Process process = simulator.processes.get(i);
            data[i][0] = process.getPid();
            data[i][1] = process.getArrivalTime();
            data[i][2] = process.getFinishTime();
            data[i][3] = process.getFinishTime() - process.getArrivalTime();
            data[i][4] = process.getBurstTime();
            data[i][5] = (process.getFinishTime() - process.getArrivalTime()) - process.getBurstTime();
            data[i][6] = process.getPriority();
        }

        // Create table
        JTable table = new JTable(data, columnNames);

        // Set uneditable
        table.setDefaultEditor(Object.class, null);

        

        // Create frame
        JFrame frame = new JFrame("CPU Scheduling Simulator");
        // Set Title Based on Selected Scheduler
        frame.setTitle("CPU Scheduling Simulator - " + selectedScheduler);
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        // Create the Gantt chart panel with a preferred size
            GanttChartPanel ganttChartPanel = new GanttChartPanel(simulator.processTimeline);
            ganttChartPanel.setPreferredSize(new Dimension(frame.getWidth(), 100)); // Set preferred size

        frame.add(ganttChartPanel, BorderLayout.NORTH);


        // Create panel for displaying statistics
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(4, 2));
        statsPanel.add(new JLabel("Total Turnaround Time:"));
        statsPanel.add(new JLabel(String.format("%.2f", (double)simulator.totalTurnaroundTime)));
        statsPanel.add(new JLabel("Average Turnaround Time:"));
        statsPanel.add(new JLabel(String.format("%.2f", (double)simulator.totalTurnaroundTime / simulator.processCount)));
        statsPanel.add(new JLabel("Total Waiting Time:"));
        statsPanel.add(new JLabel(Double.toString(simulator.totalWaitingTime)));
        statsPanel.add(new JLabel("Average Waiting Time:"));;
        statsPanel.add(new JLabel(String.format("%.2f", (double)simulator.totalWaitingTime / simulator.processCount)));


        frame.add(statsPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
