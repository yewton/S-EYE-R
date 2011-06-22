package jp.gr.java_conf.yewton.docomostar.ui;

import java.util.Vector;

import com.docomostar.ui.Graphics;

public class CanvasOrganizer {
    private Vector components = new Vector();
    private int descent = 0;
    private int height = 0;

    public void addComponent(CanvasComponent c) {
	this.components.addElement(c);
    }

    public void draw(int initialOffset, Graphics g) {
	int offset = initialOffset;
	for (int i = 0; i < this.components.size(); i++) {
	    CanvasComponent c = (CanvasComponent) components.elementAt(i);
	    c.draw(offset, g);
	    offset = c.getDescent();
	    g.setColor(Graphics.getColorOfName(Graphics.SILVER));
	}
	this.descent = offset;
	this.height = this.descent - initialOffset;
    }

    public int getDescent() {
	return this.descent;
    }

    public int getHeight() {
	return this.height;
    }

    public void dispose() {
	this.components = new Vector();
	this.descent = 0;
    }
}
