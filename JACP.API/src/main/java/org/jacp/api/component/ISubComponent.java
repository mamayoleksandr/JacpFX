/************************************************************************
 * 
 * Copyright (C) 2010 - 2012
 *
 * [ISubComponent.java]
 * AHCP Project (http://jacp.googlecode.com)
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
package org.jacp.api.component;

import org.jacp.api.action.IAction;
import org.jacp.api.context.Context;

import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;

/**
 * Defines a subcomponent handled by a root component. A subcomponent is running
 * in a perspective environment an can be represented by an visible UI or non
 * visible background component.
 * 
 * @author Andy Moncsek
 * 
 * @param <L>
 *            defines the action listener type
 * @param <A>
 *            defines the basic action type
 * @param <M>
 *            defines the basic message type
 */
public interface ISubComponent<L, A, M> extends IComponent<L, A, M> {

	/**
	 * Returns true if component has message in pipe.
	 * 
	 * @return returns true if incoming message is in queue
	 */
	boolean hasIncomingMessage();

	/**
	 * Add new message to component.
	 * 
	 * @param action
	 */
	void putIncomingMessage(final IAction<A, M> action);

	/**
	 * Returns next message in pipe.
	 * 
	 * @return the next action to handle
	 */
	IAction<A, M> getNextIncomingMessage() throws InterruptedException;

	/**
	 * Component is blocked when executed in thread.
	 * 
	 * @return blocked state
	 */
	boolean isBlocked();
	
	/**
	 * Lock Component for execution in thread.
	 */
	void lock();
	
	/**
	 * Release lock after execution in thread.
	 */
	void release();

	/**
	 * returns the id of parent component
	 * 
	 * @return the parent id
	 */
	String getParentId();

	/**
	 * Set parentId and global message queue to component
	 * 
	 * @param messageQueue
	 */
	void initEnv(final String parentId,
			final BlockingQueue<IAction<A, M>> messageQueue);


    /**
     * Returns the component handle class, this is the users implementation of the component.
     * @return IComponentHandle, the component handle.
     */
    <X extends IComponentHandle<?,  A, M>> X getComponent();

    /**
     * Set the component handle class. This is the users implementation of the component.
     * @param handle
     * @param <X>
     */
    <X extends IComponentHandle<?, A, M>>  void setComponent(final X handle);



}
