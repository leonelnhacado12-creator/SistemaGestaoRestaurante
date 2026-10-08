package org.netbeans.lib.awtextra;

import java.io.Serializable;

public class AbsoluteConstraints implements Serializable {
    private static final long serialVersionUID = 1L;
    public int x, y, width, height;
    public AbsoluteConstraints(int x, int y) { this(x,y,-1,-1); }
    public AbsoluteConstraints(int x, int y, int width, int height) { this.x=x; this.y=y; this.width=width; this.height=height; }
}
