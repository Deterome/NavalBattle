package StateMachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class StateMachine<S extends Enum<S>, E extends Enum<E>> {

    public StateMachine(S initialState) {
        this.currentState = initialState;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        initTransitionTable();
    }

    public void processEvent(E event) {
        if (transitionTable.containsKey(currentState) &&
                transitionTable.get(currentState).containsKey(event)) {
            currentState = transitionTable.get(currentState).get(event);
            onStateChange(currentState);
        } else {
            // Обработка некорректного события
            handleInvalidEvent(event);
        }
    }
    // Метод для планирования события таймаута
    protected void scheduleTimeoutEvent(E event, long delay, TimeUnit unit) {
        scheduler.schedule(() -> {
            processEvent(event);
        }, delay, unit);
    }
    // Метод для остановки таймера
    public void stopScheduler() {
        scheduler.shutdown();
    }

    public S getCurrentState() {
        return this.currentState;
    }

    // Метод, вызываемый после изменения состояния
    protected abstract void onStateChange(S newState);
    // Метод для обработки некорректного события
    protected abstract void handleInvalidEvent(E event);
    // Метод для инициализации талицы переходов
    protected abstract void initTransitionTable();

    protected void addNewTransitionToTable(S state, E event, S nextState) {
        if (!transitionTable.containsKey(state)) {
            transitionTable.put(state, new HashMap<>());
        }
        transitionTable.get(state).put(event, nextState);
    }

    protected ArrayList<S> states;
    protected ArrayList<E> events;
    protected HashMap<S, HashMap<E, S>> transitionTable = new HashMap<>();

    protected S currentState;
    private ScheduledExecutorService scheduler;
}
