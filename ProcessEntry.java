public class ProcessEntry {
    private final int processId;
    private final int finishTime;

    public ProcessEntry(int processId, int finishTime) {
        this.processId = processId;
        this.finishTime = finishTime;
    }

    public int getProcessId() {
        return processId;
    }

    public int getFinishTime() {
        return finishTime;
    }
}
