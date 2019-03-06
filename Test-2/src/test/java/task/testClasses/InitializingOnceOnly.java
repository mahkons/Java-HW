package task.testClasses;

public class InitializingOnceOnly {
    public static boolean flag = false;
    public InitializingOnceOnly() {
        if (flag) {
            throw new AssertionError("Singleton!");
        }
        flag = true;
    }
}
