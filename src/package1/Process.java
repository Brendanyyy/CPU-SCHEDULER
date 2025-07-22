package package1;

public class Process {
    String pid;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    boolean completed = false;
    boolean started = false;

    int completionTime = -1;
    int turnaroundTime = -1;
    int waitingTime = -1;
    int responseTime = -1;
    int startTime = -1;
    boolean responseRecorded = false;

    public Process(String pid, int at, int bt) {
        this.pid = pid;
        this.arrivalTime = at;
        this.burstTime = bt;
        this.remainingTime = bt;
    }

    public void reset() {
        this.remainingTime = burstTime;
        this.completed = false;
        this.completionTime = -1;
        this.turnaroundTime = -1;
        this.waitingTime = 0;
        this.responseTime = -1;
        this.startTime = -1;
        this.started = false;
        this.responseRecorded = false;
    }
}
