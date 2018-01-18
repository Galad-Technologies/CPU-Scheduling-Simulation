package scheduling;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The test class.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("Please enter the scheduling you wish to apply.\n" +
                "Choices:\n" +
                "1 for First-Come, First-Serve Scheduling\n" +
                "2 for Shortest-Job-First Scheduling\n" +
                "3 for Shortest-Remaining-Time-First Scheduling\n" +
                "4 for Non-preemptive Priority Scheduling\n" +
                "5 for Preemptive Priority Scheduling\n" +
                "6 for Preemptive Round-Robin Scheduling\n" +
                "7 for Multilevel Queue Scheduling\n" +
                "8 for Multilevel Queue Scheduling\n");
        int input = (new Scanner(System.in)).nextInt();

        ProcessController controller = new ProcessController();
        Scheduler scheduler = null;
        switch (input) {
            case 1:
                scheduler = new Scheduler(new ArrayList<>(), Scheduling.FCFS);
                break;
            case 2:
                scheduler = new Scheduler(new ArrayList<>(), Scheduling.SJF);
                break;
            case 3:
                scheduler = new Scheduler(new ArrayList<>(), Scheduling.SRTF);
                break;
            case 4:
                scheduler = new Scheduler(new ArrayList<>(), Scheduling.NPS);
                break;
            case 5:
                scheduler = new Scheduler(new ArrayList<>(), Scheduling.PPS);
                break;
            case 6:
                scheduler = new Scheduler(new ArrayList<>(), Scheduling.RRS);
                break;
            case 7:
                scheduler = new Scheduler(new ArrayList<>(), Scheduling.MQS);
                break;
            case 8:
                scheduler = new Scheduler(new ArrayList<>(), Scheduling.MFQS);
                break;
        }

        controller.setScheduler(scheduler);
        Scheduler temp = scheduler;
        System.out.println("Hold on, the system is waiting for new process to be born!");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                controller.generate();

            }
        }, 5000, 20000);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                temp.getCurrent().resetState();

            }
        }, 0, 1000);

    }

}
