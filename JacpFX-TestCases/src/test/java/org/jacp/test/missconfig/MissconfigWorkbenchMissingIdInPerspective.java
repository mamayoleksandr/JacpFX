package org.jacp.test.missconfig;

import javafx.application.Platform;
import org.jacp.test.AllTests;
import org.jacp.test.main.ApplicationLauncherMissingIdInPerspective;
import org.jacp.test.workbench.WorkbenchMissingPerspectives;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Andy Moncsek
 * Date: 09.09.13
 * Time: 20:57
 * Tests if id attribute in Perspective is Empty
 */
public class MissconfigWorkbenchMissingIdInPerspective {


    @Test(expected = RuntimeException.class)
    public void failedToStartPerspective() throws Exception {
        try {
            ApplicationLauncherMissingIdInPerspective.main(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        // Pause briefly to give FX a chance to start
        ApplicationLauncherMissingIdInPerspective.latch.await(5000, TimeUnit.MILLISECONDS);


    }

    private String[] getPerspectiveAnnotations() {
        org.jacpfx.api.annotations.workbench.Workbench annotations = WorkbenchMissingPerspectives.class.getAnnotation(org.jacpfx.api.annotations.workbench.Workbench.class);
        return annotations.perspectives();
    }

    @AfterClass
    public static void exitWorkBench() {
        Platform.exit();
        AllTests.resetApplication();
    }
}
