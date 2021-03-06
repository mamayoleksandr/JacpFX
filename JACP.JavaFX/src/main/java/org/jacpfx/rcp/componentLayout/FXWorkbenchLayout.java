/*
 * **********************************************************************
 * Copyright (C) 2010 - 2013
 *
 * [FX2ComponentLayout.java]
 * JACPFX Project (https://github.com/JacpFX/JacpFX/)
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 * ***********************************************************************
 */
package org.jacpfx.rcp.componentLayout;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import org.jacpfx.api.componentLayout.WorkbenchLayout;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.api.util.Tupel;
import org.jacpfx.rcp.components.menuBar.JACPMenuBar;
import org.jacpfx.rcp.components.toolBar.JACPToolBar;
import org.jacpfx.rcp.workbench.GlobalMediator;

import java.util.Map;

/**
 * defines basic layout of workbench; define if menus are enabled; declare tool
 * bars; set workbench size
 *
 * @author Andy Moncsek
 */
public class FXWorkbenchLayout implements WorkbenchLayout<Node> {

    private final Tupel<Integer, Integer> size = new Tupel<>();
    private boolean menueEnabled;
    private JACPMenuBar menu;
    private Pane glassPane;
    private StageStyle style = StageStyle.DECORATED;
    private String id;

    @Override
    public boolean isMenuEnabled() {
        return this.menueEnabled;
    }

    @Override
    public void setMenuEnabled(final boolean enabled) {
        this.menueEnabled = enabled;
        if (enabled && this.menu == null) {
            this.menu = new JACPMenuBar();
            this.menu.setId("main-menu");
            this.checkWindowButtons();
        }
    }

    @Override
    public void setWorkbenchXYSize(final int x, final int y) {
        this.size.setX(x);
        this.size.setY(y);
    }

    @Override
    public Tupel<Integer, Integer> getWorkbenchSize() {
        return this.size;
    }

    private JACPToolBar initToolBar(final ToolbarPosition position) {
        final JACPToolBar bar = new JACPToolBar();
        bar.setId(position.getName() + "-bar");
        return bar;
    }

    @Override
    public void registerToolBars(final ToolbarPosition... positions) {
        for (ToolbarPosition pos : positions) {
            this.registerToolBar(pos);
        }
    }

    @Override
    public void registerToolBar(final ToolbarPosition position) {
        JACPToolBar toolBar = this.initToolBar(position);
        GlobalMediator.getInstance().registerToolbar(position, toolBar);
    }

    private void checkWindowButtons() {
        if (this.menu != null && StageStyle.DECORATED.equals(style))
            this.menu.deregisterWindowButtons();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <S extends Enum> S getStyle() {
        return (S) this.style;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <S extends Enum> void setStyle(final S style) {
        this.style = (StageStyle) style;
        checkWindowButtons();
    }

    @Override
    public JACPMenuBar getMenu() {
        return this.menu;
    }

    @Override
    public JACPToolBar getRegisteredToolBar(final ToolbarPosition position) {
        return GlobalMediator.getInstance().getRegisteredToolbar(position, null, this.getId());
    }

    @Override
    public Map<ToolbarPosition, JACPToolBar> getRegisteredToolBars() {
        return GlobalMediator.getInstance().getRegisteredToolBars(null, this.getId());
    }

    private String getId() {
        return GlobalMediator.getInstance().getWorkbenchId();

    }

    @Override
    public Pane getGlassPane() {
        if (this.glassPane == null) {
            this.glassPane = new Pane();
        }
        return this.glassPane;
    }

}
