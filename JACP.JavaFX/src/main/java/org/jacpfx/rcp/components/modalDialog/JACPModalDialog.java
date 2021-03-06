/************************************************************************
 *
 * Copyright (C) 2010 - 2014
 *
 * [JACPModalDialog.java]
 * JACPFX Project (https://github.com/JacpFX/JacpFX/)
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either 
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *
 *
 ************************************************************************/
package org.jacpfx.rcp.components.modalDialog;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import static org.jacpfx.rcp.util.CSSUtil.CSSIdConstants.ID_ERROR_DIMMER;


/**
 * The Class JACPModalDialog.
 *
 * @author Patrick Symmangk
 */
public class JACPModalDialog extends StackPane implements IModalMessageNode {

    /**
     * The maximum blur radius.
     */
    private static final double MAX_BLUR = 4.0;
    /**
     * The root.
     */
    private static Node root;
    /**
     * The instance.
     */
    private static JACPModalDialog instance;
    private Timeline hideTimeline;
    private Timeline showTimeline;

    /**
     * Gets the single instance of JACPModalDialog.
     *
     * @return single instance of JACPModalDialog
     */
    public static JACPModalDialog getInstance() {

        if (JACPModalDialog.instance == null) {
            throw new DialogNotInitializedException();
        }
        return JACPModalDialog.instance;
    }

    /**
     * Inits the dialog.
     *
     * @param rootNode the root node
     */
    public static void initDialog(final Node rootNode) {
        synchronized (JACPModalDialog.class) {
            if (JACPModalDialog.instance == null) {
                JACPModalDialog.root = rootNode;
                JACPModalDialog.instance = new JACPModalDialog();
                JACPModalDialog.instance.setId(ID_ERROR_DIMMER);
                final Button close = new Button("close");
                close.setOnAction(arg -> JACPModalDialog.instance.setVisible(false));
            }
        }
    }

    /**
     * Show modal message.
     *
     * @param message the message
     */
    public void showModalDialog(final Node message) {
        synchronized (this) {
            if (getHideTimeline().getStatus() == Status.RUNNING) {
                getHideTimeline().stop();
            }
            this.getChildren().clear();
            this.getChildren().add(message);
            this.setOpacity(0);
            this.setVisible(true);
            this.setCache(true);
            JACPModalDialog.root.setEffect(new GaussianBlur(MAX_BLUR));

            this.getShowTimeline().play();
        }
    }

    /**
     * Hide any modal message that is shown.
     */
    @Override
    public synchronized void hideModalDialog() {
        this.setCache(true);
        this.getHideTimeline().play();
        // "remove" effect.
        JACPModalDialog.root.setEffect(null);
    }

    /**
     * Returns the timeline to hide dialog, should always called from a thread save block
     *
     * @return the hide timeline
     */
    private Timeline getHideTimeline() {
        if (hideTimeline == null) {
            hideTimeline = new Timeline(new KeyFrame(Duration.millis(250), (t) -> {
                JACPModalDialog.this.setCache(false);
                JACPModalDialog.this.setVisible(false);
            }, new KeyValue(this.opacityProperty(), 0, Interpolator.EASE_BOTH)));
        }
        return hideTimeline;
    }

    /**
     * Returns the timeline to show dialog, should always called from a thread save block
     *
     * @return the show timeline
     */
    private Timeline getShowTimeline() {
        if (showTimeline == null) {
            showTimeline = new Timeline(new KeyFrame(Duration.millis(250), (t) ->
                    JACPModalDialog.this.setCache(false),
                    new KeyValue(this.opacityProperty(), 1, Interpolator.EASE_BOTH)));
        }
        return showTimeline;
    }

}
