package org.jacpfx.rcp.perspective;

import org.jacpfx.api.component.Injectable;

/**
 * Created with IntelliJ IDEA.
 * User: ady
 * Date: 19.08.13
 * Time: 21:23
 * To change this template use File | Settings | File Templates.
 */
public class EmbeddedFXPerspective extends AFXPerspective {

    public EmbeddedFXPerspective(final Injectable perspective) {
        this.perspective = perspective;
    }

    @Override
    public String toString() {
        return this.getContext() != null ? this.getContext().getId() : this.perspective.toString();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
