import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.util.List;

public class GanttChartPanel extends JPanel {
    private List<ProcessEntry> processTimeline;

    public GanttChartPanel(List<ProcessEntry> processTimeline) {
        this.processTimeline = processTimeline;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int startX = 10; // Starting x-coordinate for the first process block
        int startY = 30; // Starting y-coordinate for drawing
        int processHeight = 50; // Height of each process bar
        int processWidth = 50; // Width of each process bar

        int currentX = startX; // Current x-coordinate, will be updated as we draw each process

        // Draw a text "Gannt Chart"
        g.setColor(Color.BLACK);
        g.drawString(" Gantt chart (visual form)", startX + 10, startY - 10);


        // Draw the Gantt chart
        for (int i = 0; i < processTimeline.size(); i++) {
            ProcessEntry entry = processTimeline.get(i);


            // Set color and draw the process block
            g.setColor(Color.GREEN);
            g.fillRect(currentX, startY, processWidth, processHeight);

            // Draw the process ID
            g.setColor(Color.BLACK);
            g.drawString("P" + entry.getProcessId(), currentX + processWidth / 2 - 8, startY + processHeight / 2 + 5);


            // Update currentX for the next process block
            currentX += processWidth;
        }

        currentX = startX; // Reset currentX to startX
        // Draw division lines between processes
        for (int i = 0; i <= processTimeline.size(); i++) {
            g.setColor(Color.BLACK);
            g.drawLine(currentX, startY, currentX, startY + processHeight);
            
            // Only advance currentX after drawing the division line if it's not the last line
            if (i < processTimeline.size()) {
                currentX += processWidth;
            }
        }

        // Draw the arrival time for the first process
        g.setColor(Color.BLACK);
        g.drawString("0", startX - 5, startY + processHeight + 15);

        // Draw the finish time below each division line
        int finishTimeX = startX + processWidth;
        for (ProcessEntry entry : processTimeline) {
            g.drawString(String.valueOf(entry.getFinishTime()), finishTimeX - 5, startY + processHeight + 15);
            finishTimeX += processWidth;
        }
    }
}
