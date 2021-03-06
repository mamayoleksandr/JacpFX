package org.jacpfx.rcp.component;

import org.jacpfx.api.component.ComponentHandle;

/**
 * Created with IntelliJ IDEA.
 * User: Andy Moncsek
 * Date: 17.07.13
 * Time: 10:02
 * This is an implementation of an AStatefulCallbackComponent which will be used to encapsulate handles on application startup.
 */
public class EmbeddedStatefulComponent extends ASubComponent{

    public EmbeddedStatefulComponent(ComponentHandle handle) {
        this.setComponent(handle);
    }

    @Override
    public String toString() {
        return this.getContext() != null ? this.getContext().getId() : this.getComponent().toString();
    }

}
