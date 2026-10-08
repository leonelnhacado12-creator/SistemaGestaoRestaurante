package org.netbeans.lib.awtextra;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AbsoluteLayout implements LayoutManager2, java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<Component, AbsoluteConstraints> constraints = new HashMap<>();
    public void addLayoutComponent(Component c, Object o) { constraints.put(c, (AbsoluteConstraints)o); }
    public void addLayoutComponent(String name, Component c) { }
    public void removeLayoutComponent(Component c) { constraints.remove(c); }
    public Dimension preferredLayoutSize(Container parent) { return layoutSize(parent, false); }
    public Dimension minimumLayoutSize(Container parent) { return layoutSize(parent, true); }
    private Dimension layoutSize(Container p, boolean min) {
        int w=0,h=0;
        for(Component c:p.getComponents()) {
            AbsoluteConstraints a=constraints.get(c); if(a==null) continue;
            Dimension d=min?c.getMinimumSize():c.getPreferredSize();
            int cw=a.width>=0?a.width:d.width, ch=a.height>=0?a.height:d.height;
            w=Math.max(w,a.x+cw); h=Math.max(h,a.y+ch);
        }
        Insets i=p.getInsets(); return new Dimension(w+i.left+i.right,h+i.top+i.bottom);
    }
    public Dimension maximumLayoutSize(Container target) { return new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE); }
    public float getLayoutAlignmentX(Container target) { return 0.5f; }
    public float getLayoutAlignmentY(Container target) { return 0.5f; }
    public void invalidateLayout(Container target) { }
    public void layoutContainer(Container p) {
        Insets in=p.getInsets();
        for(Component c:p.getComponents()) {
            AbsoluteConstraints a=constraints.get(c); if(a==null) continue;
            Dimension d=c.getPreferredSize(); int w=a.width>=0?a.width:d.width, h=a.height>=0?a.height:d.height;
            c.setBounds(in.left+a.x,in.top+a.y,w,h);
        }
    }
}
