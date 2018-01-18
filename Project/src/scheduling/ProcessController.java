package scheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is intended to serve as process generator, to set the default states and to associate with Scheduler.
 */
public class ProcessController {

    private Scheduler scheduler;

    public ProcessController() {}

    public void setScheduler(Scheduler scheduler) {

        this.scheduler = scheduler;

    }

    /**
     * Every 30 time units, a new sequence of processes will be generated, of which the state will be set to NEW.
     * It then, after another five time units, adds these new processes to the ready queue, which is the scheduler.
     * Then the state of these processes will be set to READY.
     */
    public void generate() {

        int seriesSize;
        List<Process> processes = new ArrayList<>();
        if ((new Random()).nextBoolean()) {
            seriesSize = 3;
        } else {
            seriesSize = 5;
        }
        for (int i = 0; i < seriesSize; i++) {
            Process process = new Process(this.scheduler);
            process.setState(State.NEW);
            process.setPriority(i);
            System.out.println("New process is born! " + process.toString());
            processes.add(process);
        }

        // Five time units later.

        for (Process process : processes) {
            process.setState(State.READY);
            process.setArrivalTime();
            process.setType();
            System.out.println("New processes arrive! " + process.toString());
        }

        for (Process process : processes) {
            switch (process.getType()) {
                case EMERGENT:
                    this.scheduler.addEmergent(process);
                    break;
                case IMPORTANT:
                    this.scheduler.addImportant(process);
                    break;
                case NECESSARY:
                    this.scheduler.addNecessary(process);
                    break;
                case REDUNDANT:
                    this.scheduler.addRedundant(process);
                    break;
            }
        }

        this.scheduler.getReadyQueue().addAll(processes);
        this.scheduler.setNext();

    }

}
