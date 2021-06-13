import org.junit.jupiter.api.Test;
import spoon.Launcher;

class TestClassTest {

    Launcher launcher;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        launcher = new Launcher();
        launcher.addInputResource("src/main/java/TestClass.java");
        launcher.setSourceOutputDirectory("target/spoon-generated/");

//        launcher.setArgs(args);
    }


    @Test
    void test1() {
        launcher.addProcessor(new ReleaseResourceProcessor());
        launcher.run();
    }
}