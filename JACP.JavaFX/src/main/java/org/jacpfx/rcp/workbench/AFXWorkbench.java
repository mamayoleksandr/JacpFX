/************************************************************************
 *
 * Copyright (C) 2010 - 2014
 *
 * [AFX2Workbench.java]
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
package org.jacpfx.rcp.workbench;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.jacpfx.api.annotations.workbench.Workbench;
import org.jacpfx.api.component.Injectable;
import org.jacpfx.api.component.Perspective;
import org.jacpfx.api.component.RootComponent;
import org.jacpfx.api.componentLayout.WorkbenchLayout;
import org.jacpfx.api.context.JacpContext;
import org.jacpfx.api.coordinator.Coordinator;
import org.jacpfx.api.delegator.ComponentDelegator;
import org.jacpfx.api.delegator.MessageDelegator;
import org.jacpfx.api.handler.ComponentHandler;
import org.jacpfx.api.launcher.Launcher;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.workbench.Base;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.componentLayout.FXWorkbenchLayout;
import org.jacpfx.rcp.components.managedFragment.ManagedFragment;
import org.jacpfx.rcp.components.workbench.WorkbenchDecorator;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.context.JacpContextImpl;
import org.jacpfx.rcp.coordinator.MessageCoordinator;
import org.jacpfx.rcp.delegator.ComponentDelegatorImpl;
import org.jacpfx.rcp.delegator.MessageDelegatorImpl;
import org.jacpfx.rcp.handler.PerspectiveHandlerImpl;
import org.jacpfx.rcp.message.MessageImpl;
import org.jacpfx.rcp.perspective.AFXPerspective;
import org.jacpfx.rcp.registry.PerspectiveRegistry;
import org.jacpfx.rcp.util.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * represents the basic JavaFX workbench instance; handles perspective and
 * component;
 *
 * @author Andy Moncsek, Patrick Symmangk
 */
public abstract class AFXWorkbench
        implements
        Base<Node,EventHandler<Event>, Event, Object>,
        RootComponent<Perspective<Node, EventHandler<Event>, Event, Object>, Message<Event, Object>> {

    private final ComponentDelegator<EventHandler<Event>, Event, Object> componentDelegator = new ComponentDelegatorImpl();
    private final MessageDelegator<EventHandler<Event>, Event, Object> messageDelegator = new MessageDelegatorImpl();
    private final WorkbenchLayout<Node> workbenchLayout = new FXWorkbenchLayout();
    private final Logger logger = Logger.getLogger(getClass().getName());
    private List<Perspective<Node, EventHandler<Event>, Event, Object>> perspectives;
    private ComponentHandler<Perspective<Node, EventHandler<Event>, Event, Object>, Message<Event, Object>> componentHandler;
    private Coordinator<EventHandler<Event>, Event, Object> messageCoordinator;



    private WorkbenchDecorator workbenchDecorator;
    private Launcher<?> launcher;
    private Stage stage;
    private Context context;
    private FXWorkbench handle;

    /**
     * JavaFX specific start sequence
     *
     * @param stage, The JavaFX stage
     */
    private void start(final Stage stage) {
        this.stage = stage;
        DimensionUtil.init(stage);
        registerTeardownActions();
        log("1: init workbench");

        initWorkbenchHandle(stage);

        log("3: handle initialisation sequence");
        perspectives = WorkbenchUtil.getInstance(launcher).createPerspectiveInstances(getWorkbenchAnnotation());
        if (perspectives == null) return;

        initSubsystem();
        handleInitialisationSequence();
    }

    private void initWorkbenchHandle(final Stage stage) {
        // init user defined workspace
        handle.handleInitialLayout(new MessageImpl(context.getId(), "init"),
                getWorkbenchLayout(), stage);
        initWorkbenchDecorator(stage);
        handle.postHandle(new FXComponentLayout(getWorkbenchLayout()
                .getMenu(), workbenchDecorator.getGlassPane(), null, getContext().getId()));
    }

    private void initWorkbenchDecorator(Stage stage) {
        workbenchDecorator.setWorkbenchLayout(getWorkbenchLayout());
        workbenchDecorator.initBasicLayout(stage);
        SceneUtil.setScene(stage.getScene());
    }

    private void registerTeardownActions() {
        TearDownHandler.registerBase(this);
        stage.setOnCloseRequest(arg0 -> {
            ShutdownThreadsHandler.shutdowAll();
            TearDownHandler.handleGlobalTearDown();
            Platform.exit();
        });
    }

    private void initSubsystem() {
        componentHandler = new PerspectiveHandlerImpl(launcher,
                workbenchLayout, workbenchDecorator.getRoot());
        messageCoordinator.setPerspectiveHandler(componentHandler);
        componentDelegator.setPerspectiveHandler(componentHandler);
        messageDelegator.setPerspectiveHandler(componentHandler);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void init(final Launcher<?> launcher, Object root) {
        this.launcher = launcher;
        ManagedFragment.initManagedFragment(launcher);
        final Workbench annotation = getWorkbenchAnnotation();
        messageCoordinator = new MessageCoordinator(annotation.id(), launcher);
        messageCoordinator.setDelegateQueue(messageDelegator.getMessageDelegateQueue());
        context = new JacpContextImpl(annotation.id(), annotation.name(), messageCoordinator.getMessageQueue());
        FXUtil.performResourceInjection(handle, context);
        start(Stage.class.cast(root));
        GlobalMediator.getInstance().handleWorkbenchToolBarButtons(annotation.id(), true);
        logger.info("INIT");
    }

    private Workbench getWorkbenchAnnotation() {
        return handle.getClass().getAnnotation(Workbench.class);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public final void initComponents(final Message<Event, Object> action) {
        perspectives.forEach(this::initPerspective);
        final List<Perspective<Node, EventHandler<Event>, Event, Object>> activeSequentialPerspectiveList = perspectives
                .stream()
                .sequential()
                .filter(p -> p.getContext() != null && p.getContext().isActive())
                .collect(Collectors.toList());
        if (!activeSequentialPerspectiveList.isEmpty()) {
            GlobalMediator.getInstance().handleToolBarButtons(activeSequentialPerspectiveList.get(activeSequentialPerspectiveList.size() - 1), true);
        }

    }

    private void initPerspective(Perspective<Node, EventHandler<Event>, Event, Object> perspective) {
        registerComponent(perspective);
        log("3.4.1: register component: " + perspective.getContext().getName());
        final CountDownLatch waitForInit = new CountDownLatch(1);
        log("3.4.2: init perspective");
        if (perspective.getContext().isActive()) {
            final Runnable r = () -> {
                componentHandler.initComponent(
                        new MessageImpl(perspective.getContext().getId(), perspective
                                .getContext().getId(), "init", null), perspective);
                waitForInit.countDown();
            };
            if (Platform.isFxApplicationThread()) {
                r.run();
            } else {
                Platform.runLater(r);

            }
            try {
                // wait for possible async execution
                waitForInit.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * handles sequence for workbench size, menu bar, tool bar and perspective
     * initialisation
     */
    private void handleInitialisationSequence() {
        stage.show();
        // start perspective Observer worker thread
        // TODO create status daemon which observes
        // thread component on
        // failure and restarts if needed!!
        ((Thread) messageCoordinator)
                .start();
        ((Thread) componentDelegator)
                .start();
        ((Thread) messageDelegator)
                .start();
        // handle perspective
        log("3.3: workbench init perspective");
        initComponents(null);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public final void registerComponent(
            final Perspective<Node, EventHandler<Event>, Event, Object> perspective) {
        final String perspectiveId = PerspectiveUtil.getPerspectiveIdFromAnnotation(perspective);
        final MessageCoordinator messageCoordinatorLocal = new MessageCoordinator(perspectiveId, launcher);
        messageCoordinatorLocal.setDelegateQueue(messageDelegator.getMessageDelegateQueue());
        messageCoordinatorLocal.setPerspectiveHandler(componentHandler);
        // use compleatableFuture
        perspective.init(componentDelegator.getComponentDelegateQueue(),
                messageDelegator.getMessageDelegateQueue(),
                messageCoordinatorLocal, launcher);
        messageCoordinatorLocal.start();
        WorkbenchUtil.handleMetaAnnotation(perspective, getWorkbenchAnnotation().id());
        addComponent(perspective);
    }

    @Override
    public final void addComponent(
            final Perspective<Node, EventHandler<Event>, Event, Object> perspective) {
        PerspectiveRegistry.registerPerspective(perspective);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    // TODO remove this!!
    public final void unregisterComponent(
            final Perspective<Node, EventHandler<Event>, Event, Object> perspective) {
        FXUtil.setPrivateMemberValue(AFXPerspective.class, perspective,
                FXUtil.APERSPECTIVE_MQUEUE, null);
        PerspectiveRegistry.removePerspective(perspective);
    }

    @Override
    public final void removeAllCompnents() {
        perspectives.forEach(this::unregisterComponent);
        perspectives.clear();
    }

    /**
     * {@inheritDoc}
     */
    private FXWorkbenchLayout getWorkbenchLayout() {
        return (FXWorkbenchLayout) workbenchLayout;
    }

    @Override
    public ComponentHandler<Perspective<Node, EventHandler<Event>, Event, Object>, Message<Event, Object>> getComponentHandler() {
        return componentHandler;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public final List<Perspective<Node, EventHandler<Event>, Event, Object>> getPerspectives() {
        return perspectives;
    }



    private void log(final String message) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(">> " + message);
        }
    }

    @Override
    public JacpContext getContext() {
        return context;
    }

    private FXWorkbench getWorkbenchHandle() {
        return handle;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FXWorkbench getComponentHandle() {
        return handle;
    }

    @Override
    public <X extends Injectable> void setComponentHandle(X handle) {
        this.handle = (FXWorkbench) handle;
    }

    protected WorkbenchDecorator getWorkbenchDecorator() {
        return this.workbenchDecorator;
    }

    protected void setWorkbenchDecorator(WorkbenchDecorator workbenchDecorator) {
        this.workbenchDecorator = workbenchDecorator;
    }
}
