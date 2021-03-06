/************************************************************************
 *
 * Copyright (C) 2010 - 2014
 *
 * [Message.java]
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

package org.jacpfx.api.message;


/**
 * Represents an message used by specific listener. An message targets a component
 * and contains a message body; every target get a specific instance of an message
 * (clone) containing only his specific message body and message event.
 *
 * @param <M> defines the type of message
 * @param <A> defines the type of ActionEvent
 * @author Andy Moncsek
 */
public interface Message<A, M> extends Cloneable {


    /**
     * Get the message payload.
     *
     * @return M returns the message object
     */
    M getMessageBody();


    /**
     * Get the caller id.
     *
     * @return the source id
     */
    String getSourceId();

    /**
     * Get source of this message event.
     *
     * @return the event
     */
    A getSourceEvent();

    /**
     * Clone message and containing event.
     *
     * @return a clone of current message instance
     */
    Message<A, M> clone();

    /**
     * Returns message target id.
     *
     * @return the target id
     */
    String getTargetId();

    /**
     * Checks if message is type of a given class.
     *
     * @param clazz, class to check if message is equal to
     * @param <T>,   the generic type
     * @return true if message body type equals clazz
     */
    <T> boolean isMessageBodyTypeOf(final Class<T> clazz);

    /**
     * Returns a typed message, if applicable.
     *
     * @param clazz, the class to cast the message
     * @param <T>    , the generic type
     * @return returns the typed message body
     */
    <T> T getTypedMessageBody(final Class<T> clazz);

    /**
     * Check if message equals given input.
     *
     * @param object, an object to check for equal
     * @return returns true if object equals the message body
     */
    boolean messageBodyEquals(final Object object);

}
