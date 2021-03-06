package org.jacp.test.missconfig;

import javafx.application.Platform;
import javafx.scene.Node;
import junit.framework.TestCase;
import org.jacp.test.main.ApplicationLauncherMissingComponentDeclarativeViewAnnotation;
import org.jacp.test.main.ApplicationLauncherMissingComponentInitialTargetId;
import org.jacp.test.main.ApplicationLauncherMissingComponentViewAnnotation;
import org.jacpfx.rcp.handler.AErrorDialogHandler;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: ady
 * Date: 09.09.13
 * Time: 20:58
 * To change this template use File | Settings | File Templates.
 */
public class MissconfigFXComponentTest {

    @Test(expected = RuntimeException.class)
    public void failedToStartFXComponent() throws Exception {
        try {
            ApplicationLauncherMissingComponentViewAnnotation.main(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        // Pause briefly to give FX a chance to start
        ApplicationLauncherMissingComponentViewAnnotation.latch.await(5000, TimeUnit.MILLISECONDS);


    }

    @Test(expected = RuntimeException.class)
    public void failedToStartDeclarativeComponents() throws Exception {
        try {
            ApplicationLauncherMissingComponentDeclarativeViewAnnotation.main(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        // Pause briefly to give FX a chance to start
        ApplicationLauncherMissingComponentDeclarativeViewAnnotation.latch.await(1000, TimeUnit.MILLISECONDS);


    }

    @Test
    public void failedToStartMissingTargetId() throws Exception {

        try {
            ApplicationLauncherMissingComponentInitialTargetId.exceptionhandler = new CustomErrorDialogHandler();
            ApplicationLauncherMissingComponentInitialTargetId.main(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        // Pause briefly to give FX a chance to start
        ApplicationLauncherMissingComponentInitialTargetId.latch.await(1000, TimeUnit.MILLISECONDS);


    }

    public class CustomErrorDialogHandler extends AErrorDialogHandler {
        public CountDownLatch latch = new CountDownLatch(1);
        @Override
        public Node createExceptionDialog(Throwable e) {
            System.out.println("ERROR "+e.getMessage());
            //
            TestCase.assertTrue(e.getMessage().contains("no targetLayout for layoutID:"));
            // ApplicationLauncherMissingComponentInitialTargetId.latch.countDown();
            Platform.exit();
            return null;
        }
    }

}
