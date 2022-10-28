import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Checker {
    private final ArrayList<Value> primaryValues;

    public Checker(ArrayList<Value> primaryValues) {
        this.primaryValues = primaryValues;
    }

    public boolean run() {
        lockValues();
        boolean isValid = checkValues();
        unlockValues();

        return isValid;
    }

    public void lockValues() {
        primaryValues.forEach(value -> {
            value.lock.lock();
            lockSecondary(value);
        });
    }

    public void lockSecondary(Value value) {
        value.getSecondary().forEach(secondary -> {
            secondary.lock.lock();
            lockSecondary(secondary);
        });
    }

    public void unlockValues() {
        primaryValues.forEach(value -> {
            value.lock.unlock();
            unlockSecondary(value);
        });
    }

    public void unlockSecondary(Value value) {
        value.getSecondary().forEach(secondary -> {
            secondary.lock.unlock();
            unlockSecondary(secondary);
        });
    }

    public boolean checkValues() {
        AtomicBoolean isValid = new AtomicBoolean(true);

        primaryValues.forEach(value -> {
            if (!check(value)) isValid.set(false);
        });

        return isValid.get();
    }

    public boolean check(Value value) {
        AtomicBoolean isValid = new AtomicBoolean(true);    // a boolean value that may be updated atomically

        if (value.getSecondary().size() != 0) {
            if (value.getPrimary().size() != 0) {
                int sumValue = value.getPrimary().stream().map(Value::getValue).reduce(0, Integer::sum);
                isValid.set(sumValue == value.getValue());
            }
            if (isValid.get()) {
                value.getSecondary().forEach(secondary -> {
                    if (!check(secondary)) isValid.set(false);
                });
            }
        }

        //System.out.println(node.getValue() + " " + isValid.get());
        return isValid.get();
    }
}
