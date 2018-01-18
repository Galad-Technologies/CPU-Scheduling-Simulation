package scheduling;

public enum  State {

    NEW, // P has not arrived yet
    RUNNING, // currently executing
    READY, // waiting for the CPU
    WAITING_FOR_IO,
    TERMINATED

}
