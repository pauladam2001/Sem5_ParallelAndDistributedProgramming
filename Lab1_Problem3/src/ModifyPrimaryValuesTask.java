import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class ModifyPrimaryValuesTask extends TimerTask {
    @Override
    public void run() {
        int index = ThreadLocalRandom.current().nextInt(0, Main.primaryValues.size());  // a random number generator isolated to the current thread. Like the global Random generator used by the Math class, a ThreadLocalRandom is initialized with an internally generated seed that may not otherwise be modified
        Value primary = Main.primaryValues.get(index);
        int value = ThreadLocalRandom.current().nextInt(-10, 11);

        primary.addValue(value);

        System.out.println("Modified primary value " + index + " by adding value " + value + ".");
    }
}
