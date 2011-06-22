package jp.gr.java_conf.yewton.docomostar.ui;

import com.docomostar.ui.Graphics;

public interface CanvasComponent {
    public void draw(int offset, Graphics g);
    public int getDescent();
}
