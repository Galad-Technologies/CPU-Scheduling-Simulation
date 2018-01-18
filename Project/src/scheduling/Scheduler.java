package scheduling;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    private Scheduling scheduling;

    private List<Process> readyQueue = new ArrayList<>();

    /**
     * These four ArrayLists are for Multi Queue Scheduling. Represents Queue0, Queue1, Queue2 and Queue3, respectively.
     */
    private List<Process> emergent = new ArrayList<>();

    private List<Process> important = new ArrayList<>();

    private List<Process> necessary = new ArrayList<>();

    private List<Process> redundant = new ArrayList<>();

    private Process currentProcess = new Process(this);

    public Scheduler(List<Process> readyQueue, Scheduling scheduling) {

        this.readyQueue = readyQueue;
        this.scheduling = scheduling;
        this.currentProcess.setState(State.READY);
        this.currentProcess.setArrivalTime();

    }

    private void setScheduling(Scheduling scheduling) {

        this.scheduling = scheduling;

    }

    public Scheduling getScheduling() {

        return this.scheduling;

    }

    public List<Process> getReadyQueue() {

        return this.readyQueue;

    }

    public Process getCurrent() {

        return this.currentProcess;

    }

    public void addEmergent(Process process) {

        this.emergent.add(process);

    }

    public void addImportant(Process process) {

        this.important.add(process);

    }

    public void addNecessary(Process process) {

        this.necessary.add(process);

    }

    public void addRedundant(Process process) {

        this.redundant.add(process);

    }

    /**
     * Choose and set next candidate state according to different algorithms.
     */
    public void setNext() {

        switch (this.scheduling) {
            case FCFS:

                // Search for the process with the smallest arrival time.
                Process minArrival = this.readyQueue.get(0);
                for (Process process : this.readyQueue) {
                    if (minArrival.getArrivalTime() > process.getArrivalTime()) {
                        minArrival = process;
                    }
                }

                this.currentProcess = minArrival;
                this.currentProcess.setState(State.RUNNING);
                break;
            case SJF:
                Process minTurnaround;
                if (this.currentProcess.getState() == State.READY || this.currentProcess.getState() == State.TERMINATED) {

                    // Search for the process with the smallest turnaround time.
                    minTurnaround = this.readyQueue.get(0);
                    for (Process process : this.readyQueue) {
                        if (minTurnaround.getTurnaroundTime() > process.getTurnaroundTime()) {
                            minTurnaround = process;
                        }
                    }

                    this.currentProcess = minTurnaround;
                    this.currentProcess.setState(State.RUNNING);
                }
                break;
            case SRTF:
                Process minRemaining = this.readyQueue.get(0);

                // Search for the process with the smallest remaining time.
                for (Process process : this.readyQueue) {
                    if (minRemaining.getRemainingTime() > process.getRemainingTime()) {
                        minRemaining = process;
                    }
                }
                this.currentProcess.setState(State.READY);
                this.currentProcess = minRemaining;
                this.currentProcess.setState(State.RUNNING);

                break;
            case NPS:

                // Search for the smallest priority.
                Process priority = this.readyQueue.get(0);
                if (priority.getState() == State.TERMINATED || priority.getState() == State.READY) {
                    for (Process process : this.readyQueue) {
                        if (priority.getPriority() > process.getPriority()) {
                            priority = process;
                        }
                    }
                }

                this.currentProcess.setState(State.READY);
                this.currentProcess = priority;
                this.currentProcess.setState(State.RUNNING);

                break;
            case PPS:

                // Search for the smallest priority.
                priority = this.readyQueue.get(0);
                for (Process process : this.readyQueue) {
                    if (priority.getPriority() > process.getPriority()) {
                        priority = process;
                    }
                }

                this.currentProcess.setState(State.READY);
                this.currentProcess = priority;
                this.currentProcess.setState(State.RUNNING);

                break;
            case RRS:

                this.currentProcess.setState(State.READY);
                if (this.readyQueue.indexOf(this.currentProcess) == this.readyQueue.size() - 1) {
                    this.currentProcess = this.readyQueue.get(0);
                } else {
                    this.currentProcess = this.readyQueue.get(this.readyQueue.indexOf(this.currentProcess) + 1);
                }
                this.currentProcess.setState(State.RUNNING);

                break;
            case MQS:
                if (!this.emergent.isEmpty()) {
                    System.out.println("Emergent processes!");
                    this.readyQueue = this.emergent;

                    // Scheduled by PPS.
                    this.setScheduling(Scheduling.PPS);
                    this.setNext();
                } else if (!this.important.isEmpty()) {
                    System.out.println("Important processes!");
                    this.readyQueue = this.important;

                    // Scheduled by SRTF.
                    this.setScheduling(Scheduling.SRTF);
                    this.setNext();
                } else if (!this.necessary.isEmpty()) {
                    System.out.println("Necessary processes!");
                    this.readyQueue = this.necessary;

                    // Scheduled by NPS.
                    this.setScheduling(Scheduling.NPS);
                    this.setNext();
                } else {
                    System.out.println("Redundant processes!");
                    this.readyQueue = this.redundant;

                    // Scheduled by RRS.
                    this.setScheduling(Scheduling.RRS);
                    this.setNext();
                }
                this.setScheduling(Scheduling.MQS);
                break;
            case MFQS:

                Process worst;
                // Find the process with the largest turnaround time, remove it from the emergent and add it to the redundant.
                if (!this.emergent.isEmpty()) {
                    worst = this.emergent.get(0);
                    for (Process process : this.emergent) {
                        if (worst.getTurnaroundTime() < process.getTurnaroundTime()) {
                            worst = process;
                        }
                    }
                    this.emergent.remove(worst);
                    this.redundant.add(worst);
                }

                // Find the process with the largest priority, remove it from the emergent and add it to the redundant.
                if (!this.important.isEmpty()) {
                    worst = this.important.get(0);
                    for (Process process : this.important) {
                        if (worst.getPriority() < process.getPriority()) {
                            worst = process;
                        }
                    }
                    this.important.remove(worst);
                    this.redundant.add(worst);
                }

                // Find the process with the largest remaining time, remove it from the necessary and add it to the redundant.
                if (!this.necessary.isEmpty()) {
                    worst = this.necessary.get(0);
                    for (Process process : this.necessary) {
                        if (worst == null || worst.getRemainingTime() < process.getRemainingTime()) {
                            worst = process;
                        }
                    }
                    this.necessary.remove(worst);
                    this.redundant.add(worst);
                }

                if (!this.redundant.isEmpty()) {
                    Process best = this.redundant.get(0);
                    for (Process process : this.redundant) {
                        if (best.getIOTime() > process.getIOTime()) {
                            best = process;
                        }
                    }
                    this.redundant.remove(best);
                    this.important.add(best);
                }

                this.setScheduling(Scheduling.MQS);
                this.setNext();

                break;
        }

    }

}
