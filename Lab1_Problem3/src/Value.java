import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Value {
    public ReentrantLock lock = new ReentrantLock();
    private final ArrayList<Value> primary = new ArrayList<>();
    private final ArrayList<Value> secondary = new ArrayList<>();
    private int value = 0;

    public Value() {}

    public Value(int value) {
        this.value = value;
    }

    public ArrayList<Value> getPrimary() {
        return primary;
    }

    public ArrayList<Value> getSecondary() {
        return secondary;
    }

    public int getValue() {
        return value;
    }

    public void addPrimary(Value input){
        this.primary.add(input);
    }

    public void addValue(int value){
        lock.lock();

        this.value += value;
        this.getSecondary().forEach(secondary ->
                secondary.addValue(value)
        );

        lock.unlock();
    }

    public void addSecondary(Value secondary){
        this.secondary.add(secondary);
        secondary.addPrimary(this);
        secondary.addValue(value);
    }
}
