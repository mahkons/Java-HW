package task.testClasses;

public class InitializingOnceOnlyAnother {
    public static boolean flag = false;
    public InitializingOnceOnlyAnother() {
        if (flag) {
            throw new AssertionError("Singleton!");
        }
        flag = true;
    }
}
