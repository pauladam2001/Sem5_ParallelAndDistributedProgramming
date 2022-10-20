import java.util.TimerTask;

public class ConsistencyCheckTask extends TimerTask {
    @Override
    public void run() {
        Checker consistencyChecker = new Checker(Main.primaryValues);

        System.out.println("Running ConsistencyCheck.");
        boolean result = consistencyChecker.run();
        System.out.println("ConsistencyCheck returned the result " + result + ".");
    }
}
