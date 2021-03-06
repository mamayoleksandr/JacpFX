/************************************************************************
 * 
 * Copyright (C) 2010 - 2014
 *
 * [IModalMessageNode.java]
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
package org.jacpfx.controls.optionPane;

import org.jacpfx.controls.util.ResourceBundleUtil;

/**
 * The Enum JACPDialogButton.
 * 
 * @author Patrick Symmangk (pete.jacp@gmail.com)
 */
public enum JACPDialogButton {

    /** The OK-Button. */
    OK(1, "btn.dialog.ok"),
    /** The CANCEL-Button. */
    CANCEL(2, "btn.dialog.cancel"),
    /** The YES-Button. */
    YES(3, "btn.dialog.yes"),
    /** The NO-Button. */
    NO(4, "btn.dialog.no");

    /** The id. */
    private int id;

    /** The label. */
    private String label;

    /**
     * Instantiates a new jACP dialog button.
     * 
     * @param id
     *            the id
     * @param label
     *            the label
     */
    JACPDialogButton(final int id, final String label) {
        this.id = id;
        this.label = label;
    }

    /**
     * From id.
     * 
     * @param id
     *            the id
     * @return the jACP dialog button
     */
    public static JACPDialogButton fromId(final int id) {
        JACPDialogButton currentButton;
        switch (id) {
        case 1:
            currentButton = JACPDialogButton.OK;
            break;
        case 2:
            currentButton = JACPDialogButton.CANCEL;
            break;
        case 3:
            currentButton = JACPDialogButton.YES;
            break;
        case 4:
            currentButton = JACPDialogButton.NO;
            break;
        default:
            currentButton = JACPDialogButton.OK;
            break;
        }
        return currentButton;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *            the new id
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * Gets the label.
     * 
     * @return the label
     */
    public String getLabel() {
        return ResourceBundleUtil.getBundle().getString(this.label);
    }

    /**
     * Sets the label.
     * 
     * @param label
     *            the new label
     */
    public void setLabel(final String label) {
        this.label = label;
    }

}
