package org.jacp.test;

import com.sun.javafx.application.LauncherImpl;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Andy Moncsek
 * Date: 09.09.13
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */
//@RunWith(Suite.class)
//@Suite.SuiteClasses({ BasicInitialisationTests.class, MissconfigLauncherTest.class,MissconfigWorkbenchTest.class, MissconfigWorkbenchIdTest.class })
public class AllTests {
    // TODO do not use allTests due to problems with main thread.
    public static void resetApplication() {
        Field launchCalledField = null;
        try {
            launchCalledField = LauncherImpl.class.getDeclaredField("launchCalled");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        launchCalledField.setAccessible(true);
        try {
            AtomicBoolean atomic = (AtomicBoolean) launchCalledField.get(null);
            atomic.set(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
