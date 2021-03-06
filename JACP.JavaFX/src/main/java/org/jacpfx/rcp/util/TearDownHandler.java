/************************************************************************
 *
 * Copyright (C) 2010 - 2014
 *
 * [TearDownHandler.java]
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
package org.jacpfx.rcp.util;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.component.Perspective;
import org.jacpfx.api.component.StatelessCallabackComponent;
import org.jacpfx.api.component.SubComponent;
import org.jacpfx.api.workbench.Base;
import org.jacpfx.rcp.component.EmbeddedFXComponent;
import org.jacpfx.rcp.component.ASubComponent;
import org.jacpfx.rcp.component.CallbackComponent;
import org.jacpfx.rcp.registry.ComponentRegistry;
import org.jacpfx.rcp.workbench.GlobalMediator;
import org.jacpfx.rcp.worker.AComponentWorker;
import org.jacpfx.rcp.worker.TearDownWorker;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles TearDown annotations on all component when application is closed.
 *
 * @author Andy Moncsek
 */
@SuppressWarnings("Convert2streamapi")
public class TearDownHandler {
    private static final ExecutorService executor = Executors
            .newCachedThreadPool(new HandlerThreadFactory(
                    "PerspectiveHandlerImpl:"));
    private static Base<Node,EventHandler<Event>, Event, Object> rootWorkbench;

    /**
     * Register the parent workbench, from here all perspective and component
     * should be reachable.
     *
     * @param rootWorkbench, the root workbench
     */
    public static void registerBase(
            Base<Node,EventHandler<Event>, Event, Object> rootWorkbench) {
        TearDownHandler.rootWorkbench = rootWorkbench;
    }

    /**
     * perform global teardown on all component. This method will cause all @TearDown
     * annotated methods to be executed.
     */
    public static void handleGlobalTearDown() {
        if (rootWorkbench == null)
            throw new UnsupportedOperationException("can't teardown workbench");
        final List<Perspective<Node, EventHandler<Event>, Event, Object>> perspectives = rootWorkbench
                .getPerspectives();
        if (perspectives == null) return;
        for (final Perspective<Node, EventHandler<Event>, Event, Object> perspective : perspectives) {
            // TODO ... teardown perspective itself
            final List<SubComponent<EventHandler<Event>, Event, Object>> subcomponents = perspective
                    .getSubcomponents();
            if (subcomponents != null) {
                final List<SubComponent<EventHandler<Event>, Event, Object>> handleAsync = new ArrayList<>();
                // TODO FIXME for teardow all parameters in PreDestroyed should be passed -- see init process
                for (final SubComponent<EventHandler<Event>, Event, Object> component : subcomponents) {
                    if (CallbackComponent.class.isAssignableFrom(component.getClass())) {
                        handleAsync
                                .add(component);
                    } else {
                        // run teardown in app thread
                        FXUtil.invokeHandleMethodsByAnnotation(PreDestroy.class,
                                component.getComponent());
                    }

                }
                if (!handleAsync.isEmpty())
                    handleAsyncTearDown(handleAsync);

            }

            FXUtil.invokeHandleMethodsByAnnotation(PreDestroy.class,
                    perspective.getPerspective());

        }
        executor.shutdown();
    }

    /**
     * executes all methods in ICallbackComponent, annotated with @OnTeardown
     * outside application thread.
     *
     * @param components, all component that should execute an async teardown
     */
    @SafeVarargs
    public static void handleAsyncTearDown(
            SubComponent<EventHandler<Event>, Event, Object>... components) {
        final List<SubComponent<EventHandler<Event>, Event, Object>> handleAsync = new ArrayList<>();
        Collections.addAll(handleAsync, components);
        handleAsyncTearDown(handleAsync);
    }

    /**
     * executes all methods in ICallbackComponent, annotated with @OnTeardown
     * outside application thread.
     *
     * @param components, all component that should execute an async teardown
     */
    private static void handleAsyncTearDown(
            final List<SubComponent<EventHandler<Event>, Event, Object>> components) {
        try {
            final Set<Future<Boolean>> set = new HashSet<>();
            for (final SubComponent<EventHandler<Event>, Event, Object> component : components) {
                if (component instanceof StatelessCallabackComponent) {
                    final StatelessCallabackComponent<EventHandler<Event>, Event, Object> tmp = (StatelessCallabackComponent<EventHandler<Event>, Event, Object>) component;
                    final List<SubComponent<EventHandler<Event>, Event, Object>> instances = tmp.getInstances();
                    for (final SubComponent<EventHandler<Event>, Event, Object> instance : instances) {
                        set.add(executor.submit(new TearDownWorker(instance)));
                    }
                }
                set.add(executor.submit(new TearDownWorker(component)));
            }
            awaitTermination(set);
        } catch (RejectedExecutionException e) {
            log("component teardown was not executed");
        }

    }

    public static void shutDownFXComponent(final EmbeddedFXComponent component, final String parentId, final Object... params) {
        // run teardown
        FXUtil.invokeHandleMethodsByAnnotation(PreDestroy.class,
                component.getComponent(), params);
        GlobalMediator.getInstance().clearToolbar(component, parentId);
        component.interruptWorker();
        component.initEnv(null, null);
        ComponentRegistry.removeComponent(component);
    }

    private static void awaitTermination(Set<Future<Boolean>> set) {
        // await termination
        for (final Future<Boolean> future : set) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log("error while handle TearDown");
                e.printStackTrace();
            }
        }
    }

    public static void shutDownAsyncComponent(final ASubComponent component, final Object... params) {
        if (component instanceof StatelessCallabackComponent) {
            final Set<Future<Boolean>> set = new HashSet<>();
            final StatelessCallabackComponent<EventHandler<Event>, Event, Object> tmp = (StatelessCallabackComponent<EventHandler<Event>, Event, Object>) component;
            final List<SubComponent<EventHandler<Event>, Event, Object>> instances = tmp.getInstances();
            for (final SubComponent<EventHandler<Event>, Event, Object> instance : instances) {
                if (instance.isStarted())
                    set.add(executor.submit(new TearDownWorker(instance)));
            }
            awaitTermination(set);
            tmp.getExecutorService().shutdownNow();
            instances.clear();
            ComponentRegistry.removeComponent(component);
        } else {
            try {
                executor.submit(new TearDownWorker(component)).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            component.interruptWorker();
            component.initEnv(null, null);
            ComponentRegistry.removeComponent(component);
        }
    }

    public static void executePredestroy(final SubComponent<EventHandler<Event>, Event, Object> component) {
        FXUtil.invokeHandleMethodsByAnnotation(PreDestroy.class,
                component.getComponent());
    }

    private static void log(final String message) {
        if (Logger.getLogger(AComponentWorker.class.getName()).isLoggable(
                Level.FINE)) {
            Logger.getLogger(AComponentWorker.class.getName()).fine(
                    ">> " + message);
        }
    }
}
