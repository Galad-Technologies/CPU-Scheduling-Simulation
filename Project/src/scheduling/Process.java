package scheduling;

import java.util.Random;

public class Process {

    /**
     * This is to count how many processes have been created, so as it to assign the value of id of each process.
     */
    private static int count = 0;

    /**
     * This is to count how many processes have been finished, in order to calculate the average turnaround time.
     */
    private static int done = 1;

    private int id;

    /**
     * the time when process P first makes a request to the CPU Scheduler to execute oon the CPU.
     */
    private int arrivalTime;

    /**
     * the units of time for P to execute on the CPU
     */
    private int CPUTime;

    /**
     * the units of time for P to do I/0 on some device
     */
    private int IOTime;

    /**
     * will only be used when Priority Scheduling is applied. Lower Process Priority Numbers correspond to higher process
     * priorities
     */
    private int priority;

    private double turnaroundTime;

    private static double totalTurnaroundTime = 0;

    private static double averageTurnaroundTime = 0;

    private static double totalWaitingTime = 0;

    private static double averageWaitingTime = 0;

    private double remainingTime;

    private State state;

    /**
     * This is mainly for Multi Queue Scheduling where each process is required for a type to denote its importance, or
     * priority.
     */
    private Type type;

    private Scheduler scheduler;

    public Process(Scheduler scheduler) {

        Random random = new Random();
        this.id = count;
        count++;

        /**
         * For the sake of guaranteeing the positive of CPUTime and IOTime.
         */
        int t;
        do {
            this.setCPUTime(t = random.nextInt(5));
        }
        while (t == 0);
        do {
            this.setIOTime(t = random.nextInt(5));
        } while (t == 0);

        this.turnaroundTime = this.getCPUTime() + this.getIOTime();
        this.remainingTime = this.turnaroundTime;
        this.scheduler = scheduler;

    }

    public void setArrivalTime() {

        this.arrivalTime += count;

    }

    public void setCPUTime(int CPUTime) {

        this.CPUTime = CPUTime;

    }

    public void setIOTime(int IOTime) {

        this.IOTime = IOTime;

    }

    public void setPriority(int priority) {

        this.priority = priority;

    }

    public void setState(State state) {

        this.state = state;

    }

    public void setType() {

        Random random = new Random();
        int t = random.nextInt(4);
        switch (t) {
            case 0:
                this.type = Type.EMERGENT;
                break;
            case 1:
                this.type = Type.IMPORTANT;
                break;
            case 2:
                this.type = Type.NECESSARY;
                break;
            case 3:
                this.type = Type.REDUNDANT;
                break;
        }

    }

    public Type getType() {

        return this.type;

    }

    public State getState() {

        return this.state;

    }

    public int getId() {

        return this.id;

    }

    public double getTurnaroundTime() {

        return this.turnaroundTime;

    }

    public int getArrivalTime() {

        return this.arrivalTime;

    }

    public int getCPUTime() {

        return this.CPUTime;

    }

    public int getIOTime() {

        return this.IOTime;

    }

    public int getPriority() {

        return this.priority;

    }

    public double getRemainingTime() {

        return this.remainingTime;

    }

    /**
     * Every one second (each unit time) this method will be called to update system's state.
     */
    public void resetState() {

        System.out.println();
        switch (this.getState()) {
            case RUNNING:

                if (this.CPUTime == 0) {
                    if (this.IOTime == 0) {
                        System.out.println("This process is done!");
                        this.setState(State.TERMINATED);
                        System.out.println(this.toString());
                    } else {
                        System.out.println("This process is waiting for IO!");
                        this.setState(State.WAITING_FOR_IO);
                        System.out.println(this.toString());
                        this.remainingTime--;
                        this.IOTime--;
                    }
                } else {
                    System.out.println("This process is running!");
                    System.out.println(this.toString());
                    this.remainingTime--;
                    this.CPUTime--;
                }

                /**
                 * Compute the total and average turnaround time.
                 */
                totalTurnaroundTime++;
                averageTurnaroundTime = totalTurnaroundTime / done;

                /**
                 * Compute the total and average waiting time.
                 */
                for (int i = 1; i < this.scheduler.getReadyQueue().size() - 1; i++) {
                    totalWaitingTime += this.scheduler.getReadyQueue().get(i).getTurnaroundTime();
                }
                averageWaitingTime = totalWaitingTime / this.scheduler.getReadyQueue().size();

                if (this.scheduler.getScheduling() == Scheduling.RRS) {
                    this.scheduler.setNext();
                }

                break;
            case WAITING_FOR_IO:

                if (this.IOTime == 0) {
                    if (this.CPUTime >= 1) {
                        System.out.println("This process is running!");
                        this.setState(State.RUNNING);
                        System.out.println(this.toString());
                        this.remainingTime--;
                        this.CPUTime--;
                    } else {
                        System.out.println("This process is done!");
                        this.setState(State.TERMINATED);
                        System.out.println(this.toString());
                    }
                } else {
                    System.out.println("This process is waiting for IO!");
                    System.out.println(this.toString());
                    this.remainingTime--;
                    this.IOTime--;
                }

                /**
                 * Compute the total and average turnaround time.
                 */
                totalTurnaroundTime++;
                averageTurnaroundTime = totalTurnaroundTime / done;

                /**
                 * Compute the total and average waiting time.
                 */
                for (int i = 1; i < this.scheduler.getReadyQueue().size() - 1; i++) {
                    totalWaitingTime += this.scheduler.getReadyQueue().get(i).getTurnaroundTime();
                }
                averageWaitingTime = totalWaitingTime / done + this.scheduler.getReadyQueue().size();

                if (this.scheduler.getScheduling() == Scheduling.RRS) {
                    this.scheduler.setNext();
                }

                break;
            case TERMINATED:
                this.scheduler.getReadyQueue().remove(this);
                done++;
                if (this.scheduler.getReadyQueue().isEmpty()) {
                    if (this.scheduler.getScheduling() != Scheduling.MQS) {
                        System.out.println("No process remaining.");
                        System.out.println("But wait! More is coming!");
                    } else {
                        this.scheduler.setNext();
                    }
                } else {
                    this.scheduler.setNext();
                }
                break;
        }

    }

    public String toString() {

        return "Process " + this.getId() + "    " + "   priority: " + this.getPriority() + "    state: " +
                this.getState() + "   " + "CPU time remained: " + this.getCPUTime() + "   " + "IO time remained: "
                + this.getIOTime() + "    total time remained: " + this.remainingTime + "  total turnaround time: "
                + totalTurnaroundTime + "    average turnaround time: " + averageTurnaroundTime + "   "
                + "total waiting time: " + totalWaitingTime + "    " + "average waiting time: " + averageWaitingTime;

    }

}
