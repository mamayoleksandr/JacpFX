/************************************************************************
 *
 * Copyright (C) 2010 - 2014
 *
 * [StatelessComponentScheduler.java]
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
package org.jacpfx.api.scheduler;

import org.jacpfx.api.component.ComponentHandle;
import org.jacpfx.api.component.StatelessCallabackComponent;
import org.jacpfx.api.message.Message;

/**
 * Handles instances of a state less component; delegates message to a non
 * blocked component instance or if all component are blocked message is
 * delegated to queue in one of existing instances
 *
 * @param <L> The listener type.
 * @param <A> The message type.
 * @param <M> The Message type.
 * @author Andy Moncsek
 */
public interface StatelessComponentScheduler<L, A, M> {
    /**
     * Handles incoming message to managed state less component.
     *
     * @param message,   the message
     * @param component, the component instance
     */
    void incomingMessage(final Message<A, M> message,
                         final StatelessCallabackComponent<L, A, M> component);

    /**
     * Returns a new instance of managed state less component.
     *
     * @param <T>        , the component to clone
     * @param <H>,       H is an ComponentHandle
     * @param component, the component that should be cloned
     * @param clazz,     the class of the component.
     * @return an cloned instance of a state less component.
     */
    <T extends StatelessCallabackComponent<L, A, M>, H extends ComponentHandle> StatelessCallabackComponent<L, A, M> getCloneBean(
            final T component,
            final Class<H> clazz);

}