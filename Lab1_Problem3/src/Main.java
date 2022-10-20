import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Main {
    public static ArrayList<Value> primaryValues = new ArrayList<>();

    public static void main(String[] args) {
        createValues();
        modifyInputNodes();
        runChecker();
    }

    private static void createValues(){
        Value primary1 = new Value(2);
        Value primary2 = new Value(3);
        Value primary3 = new Value(5);
        Value primary4 = new Value(4);
        Value primary5 = new Value(1);

        primaryValues.add(primary1);
        primaryValues.add(primary2);
        primaryValues.add(primary3);
        primaryValues.add(primary4);
        primaryValues.add(primary5);

        Value secondary1 = new Value(); // 10
        Value secondary2 = new Value(); // 5

        // 10 = 2+3+5
        primary1.addSecondary(secondary1);
        primary2.addSecondary(secondary1);
        primary3.addSecondary(secondary1);

        // 5 = 4+1
        primary4.addSecondary(secondary2);
        primary5.addSecondary(secondary2);

        Value secondary3 = new Value(); // 19

        // 19 = 4+10+5
        primary4.addSecondary(secondary3);
        secondary1.addSecondary(secondary3);
        secondary2.addSecondary(secondary3);
    }

    private static void modifyInputNodes() {
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        final ScheduledFuture<?> promise = executor.scheduleAtFixedRate(new ModifyPrimaryValuesTask(), 0, 1, TimeUnit.SECONDS);
        final ScheduledFuture<Boolean> canceller = executor.schedule(() -> promise.cancel(false), 30, TimeUnit.SECONDS);
    }

    private static void runChecker() {
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        final ScheduledFuture<?> promise = executor.scheduleAtFixedRate(new ConsistencyCheckTask(), 5, 5, TimeUnit.SECONDS);
        final ScheduledFuture<Boolean> canceller = executor.schedule(() -> promise.cancel(false), 35, TimeUnit.SECONDS);
    }
}
