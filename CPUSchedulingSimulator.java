import java.util.*;

public class CPUSchedulingSimulator {
    public List<Process> processes;
    public int timeQuantum;
    public int totalTurnaroundTime = 0;
    public int totalWaitingTime = 0;
    public int processCount = 0;

    // Create a map
    public List<ProcessEntry> processTimeline = new ArrayList<>();

    public CPUSchedulingSimulator(int timeQuantum) {
        processes = new ArrayList<>();
        this.timeQuantum = timeQuantum;
    }

    public void addProcess(Process process) {
        processes.add(process);
    }

    public void roundRobin() {
        // Sort the processes based on arrival time
        Collections.sort(processes, Comparator.comparingInt(Process::getArrivalTime));
    
        Queue<Integer> processQueue = new LinkedList<>();
        int currentTime = 0;
        int i = 0; // Use 'i' to track new arrivals more effectively within the loop
    
        // Pre-load the queue with processes that arrive at time 0
        while (i < processes.size() && processes.get(i).getArrivalTime() == 0) {
            processQueue.offer(i++);
        }

        List<ProcessEntry> ganttChart = new ArrayList<>();
    
        // Execute processes in round robin fashion
        while (!processQueue.isEmpty()) {
            int index = processQueue.poll();
            Process currentProcess = processes.get(index);
    
            int timeSlice = Math.min(timeQuantum, currentProcess.getRemainingTime());
            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - timeSlice);
            currentTime += timeSlice;
    
            ganttChart.add(new ProcessEntry(currentProcess.getPid(), currentTime));
    
            // Check for new arrivals between the last check and the current time
            while (i < processes.size() && processes.get(i).getArrivalTime() <= currentTime) {
                processQueue.offer(i++);
            }
    
            // Re-queue the process if it still has remaining time
            if (currentProcess.getRemainingTime() > 0) {
                processQueue.offer(index);
            } else {
                currentProcess.setFinishTime(currentTime);
            }
        }
    
        // Calculate statistics after all processes are scheduled
        for (Process process : processes) {
            int turnaroundTime = process.getFinishTime() - process.getArrivalTime();
            totalTurnaroundTime += turnaroundTime;
            int waitingTime = turnaroundTime - process.getBurstTime();
            totalWaitingTime += waitingTime;
            processCount++;
        }
    
        // Print the Gantt chart
        for (ProcessEntry entry : ganttChart) {
            processTimeline.add(entry); // Assuming you want to store the final timeline
            System.out.println("P" + entry.getProcessId() + " " + entry.getFinishTime());
        }
    }
    
    
    
    public void nonSJF() {
        // Copy the original list of processes for scheduling
        List<Process> processCopy = new ArrayList<>(processes);
    
        // Initially sort the copied processes based on arrival time
        Collections.sort(processCopy, Comparator.comparingInt(Process::getArrivalTime));
    
        int currentTime = 0;
        List<Process> availableProcesses = new ArrayList<>();
    
        while (!processCopy.isEmpty()) {
            // Filter processes that have arrived but not started
            for (Process process : processCopy) {
                if (process.getArrivalTime() <= currentTime && !availableProcesses.contains(process)) {
                    availableProcesses.add(process);
                }
            }
    
            if (availableProcesses.isEmpty()) {
                currentTime++;
                continue;
            }
    
            // Sort the available processes based on burst time
            Collections.sort(availableProcesses, Comparator.comparingInt(Process::getBurstTime));
    
            // Select the process with the shortest burst time
            Process currentProcess = availableProcesses.get(0);
    
            // Execute the selected process
            currentTime += currentProcess.getBurstTime();
            currentProcess.setFinishTime(currentTime);
            System.out.println("Process " + currentProcess.getPid() + " finished at time " + currentTime);
            processTimeline.add(new ProcessEntry(currentProcess.getPid(), currentTime));
            // Calculate and set turnaround and waiting times for statistics
            int turnaroundTime = currentProcess.getFinishTime() - currentProcess.getArrivalTime();
            int waitingTime = turnaroundTime - currentProcess.getBurstTime();
            totalTurnaroundTime += turnaroundTime;
            totalWaitingTime += waitingTime;
            processCount++;
    
            // Remove the completed process from the copied list and available list
            processCopy.remove(currentProcess);
            availableProcesses.remove(currentProcess);
        }
    
        // Now processes list is intact and can be used for displaying in the table
    }

    public void preemptiveSJF() {
        Collections.sort(processes, Comparator.comparingInt(Process::getArrivalTime));
        
        int currentTime = 0;
        Process currentProcess = null;
        boolean isProcessRunning = false;
    
        // Continue looping until all processes are finished
        while (true) {
            // Find the process with the shortest remaining time at the current time
            Process nextProcess = null;
            for (Process process : processes) {
                if (process.getArrivalTime() <= currentTime && process.getRemainingTime() > 0) {
                    if (nextProcess == null || process.getRemainingTime() < nextProcess.getRemainingTime()) {
                        nextProcess = process;
                    }
                }
            }
    
            // If no process is found and no process is running, increment time
            if (nextProcess == null && !isProcessRunning) {
                currentTime++;
                continue;
            }
    
            // If the current process is null or the next process has a shorter remaining time
            if (currentProcess == null || nextProcess.getRemainingTime() < currentProcess.getRemainingTime()) {
                if (currentProcess != null && currentProcess.getRemainingTime() > 0) {
                    // Preempt current process
                    processTimeline.add(new ProcessEntry(currentProcess.getPid(), currentTime));
                }
                currentProcess = nextProcess;
                isProcessRunning = true;
            }
    
            // Execute the current process for one time unit
            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
            currentTime++;
    
            // If the current process is finished, record its finish time
            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setFinishTime(currentTime);
                processTimeline.add(new ProcessEntry(currentProcess.getPid(), currentTime));
                isProcessRunning = false;
                currentProcess = null; // Reset current process
            }
    
            // Check if all processes are finished
            boolean allProcessesFinished = processes.stream().allMatch(p -> p.getRemainingTime() == 0);
            if (allProcessesFinished) {
                break;
            }
        }
    
        // Calculate and set turnaround and waiting times for statistics after all processes are finished
        totalTurnaroundTime = 0;
        totalWaitingTime = 0;
        for (Process process : processes) {
            int turnaroundTime = process.getFinishTime() - process.getArrivalTime();
            totalTurnaroundTime += turnaroundTime;

            int waitingTime = turnaroundTime - process.getBurstTime();
            totalWaitingTime += waitingTime;
            processCount++;
        }
    }
    
    
    

    public void nonPreemptivePriority() {
        // Copy the original list of processes for scheduling
        List<Process> processCopy = new ArrayList<>(processes);
    
        // Initially sort the copied processes based on arrival time
        Collections.sort(processCopy, Comparator.comparingInt(Process::getArrivalTime));
    
        int currentTime = 0;
        List<Process> availableProcesses = new ArrayList<>();
    
        while (!processCopy.isEmpty()) {
            // Filter processes that have arrived but not started
            for (Process process : processCopy) {
                if (process.getArrivalTime() <= currentTime && !availableProcesses.contains(process)) {
                    availableProcesses.add(process);
                }
            }
    
            if (availableProcesses.isEmpty()) {
                currentTime++;
                continue;
            }
    
            // Sort the available processes based on priority and then burst time
            Collections.sort(availableProcesses, Comparator.comparingInt(Process::getPriority)
                                                        .thenComparingInt(Process::getBurstTime));
    
            // Select the process with the highest priority (and shortest burst time in case of tie)
            Process currentProcess = availableProcesses.get(0);
    
            // Execute the selected process
            currentTime += currentProcess.getBurstTime();
            currentProcess.setFinishTime(currentTime);
            System.out.println("Process " + currentProcess.getPid() + " finished at time " + currentTime);
            processTimeline.add(new ProcessEntry(currentProcess.getPid(), currentTime));
            // Calculate and set turnaround and waiting times for statistics
            int turnaroundTime = currentProcess.getFinishTime() - currentProcess.getArrivalTime();
            int waitingTime = turnaroundTime - currentProcess.getBurstTime();
            totalTurnaroundTime += turnaroundTime;
            totalWaitingTime += waitingTime;
            processCount++;
    
            // Remove the completed process from the copied list and available list
            processCopy.remove(currentProcess);
            availableProcesses.remove(currentProcess);
        }
    
        // Now processes list is intact and can be used for displaying in the table
    }
    
    

}
