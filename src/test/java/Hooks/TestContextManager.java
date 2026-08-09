package Hooks;

public final class TestContextManager {

    private TestContextManager() {
    }

    private static final ThreadLocal<TestContext> CONTEXT =
            ThreadLocal.withInitial(TestContext::new);

    public static TestContext getContext() {
        return CONTEXT.get();
    }

    public static void unload() {
        CONTEXT.remove();
    }

}
