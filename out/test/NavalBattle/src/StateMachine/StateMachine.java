package StateMachine;

import java.util.ArrayDeque;
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

    // Обязательно к вызову перед завершением работы машины состояний
    public abstract void stopStateMachine();

    public void invokeEvent(E event) {
        invokedEventsQueue.addLast(event);
        processEvents();
    }

    private void processEvents() {
        if (!processingEvents) {
            processingEvents = true;
            while (!invokedEventsQueue.isEmpty()) {
                E event = invokedEventsQueue.pollFirst();
                if (transitionTable.containsKey(currentState) && transitionTable.get(currentState).containsKey(event)) {
                    var newState = transitionTable.get(currentState).get(event);
                    onStateChange(newState);
                    currentState = newState;
                } else {
                    // Обработка некорректного события
                    handleInvalidEvent(event);
                }
            }
            processingEvents = false;
        }

    }

    // Метод для планирования события таймаута
    protected void scheduleTimeoutEvent(E event, long delay, TimeUnit unit) {
        scheduler.schedule(() -> {
            invokeEvent(event);
        }, delay, unit);
    }
    // Метод для остановки таймера. Надо вызывать при закрытии машины состояний
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

    private ArrayDeque<E> invokedEventsQueue = new ArrayDeque<>();
    private boolean processingEvents = false;

    protected S currentState;
    private ScheduledExecutorService scheduler;
}
