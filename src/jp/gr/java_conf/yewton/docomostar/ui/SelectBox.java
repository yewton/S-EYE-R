package jp.gr.java_conf.yewton.docomostar.ui;

import com.docomostar.ui.Font;
import com.docomostar.ui.Graphics;

public class SelectBox {
    public final static int FOCUS_OUT = 0;
    public final static int FOCUS_ON = 1;
    public final static int FOCUS_IN = 2;

    private final static int SELECTBOX_BUTTON_PADDING = 8;

    private String[] options = null;
    private int width = 0;
    private int height = 0;
    private Font font = null;
    private int current_selection = 0;
    private int selection = 0;
    private int focus = FOCUS_OUT;

    public SelectBox(String[] options, Font font) {
	height = getMaxHeight(options, font) + 5;
	width = getMaxWidth(options, font) + height + 10;
	this.font = font;
	this.options = options;
    }

    public int getValue() {
	return current_selection;
    }

    public String[] getOptions() {
	return options;
    }

    public void setSelection(int s) {
	current_selection = s;
	selection = s;
    }

    public void focusOut() {
	focus = FOCUS_OUT;
    }

    public void focusOn() {
	current_selection = selection;
	focus = FOCUS_ON;
    }

    public void focusIn() {
	focus = FOCUS_IN;
    }

    public int getFocus() {
	return focus;
    }

    public void selectNext() {
	int next = selection + 1;
	if (options.length <= next) {
	    next = 0;
	}
	selection = next;
    }

    public void selectPrev() {
	int prev = selection - 1;
	if (prev < 0) {
	    prev = options.length - 1;
	}
	selection = prev;
    }

    public void draw(int x, int y, Graphics g) {
	g.setFont(font);
	drawButton(x, y, g);
	switch (focus) {
	case FOCUS_OUT:
	    drawFocusOut(x, y, g);
	    break;
	case FOCUS_ON:
	    drawFocusOn(x, y, g);
	    break;
	case FOCUS_IN:
	    drawFocusIn(x, y, g);
	    break;
	}
    }

    private void drawButton(int x, int y, Graphics g) {
	int bx = x + width - height;
	int by = y;
	g.setColor(Graphics.getColorOfName(Graphics.GRAY));
	g.fillRect(bx, by, height, height);
	g.setColor(Graphics.getColorOfName(Graphics.WHITE));
	int[] xPoints = { bx + SELECTBOX_BUTTON_PADDING,
		bx + height - SELECTBOX_BUTTON_PADDING, bx + (height / 2) };
	int[] yPoints = { by + SELECTBOX_BUTTON_PADDING,
		by + SELECTBOX_BUTTON_PADDING,
		by + height - SELECTBOX_BUTTON_PADDING };
	g.fillPolygon(xPoints, yPoints, 3);
    }

    private void drawFocusOn(int x, int y, Graphics g) {
	g.setColor(Graphics.getColorOfName(Graphics.YELLOW));
	g.drawRect(x - 1, y - 1, width + 2, height + 2);
	g.drawRect(x, y, width, height);
	g.setColor(Graphics.getColorOfName(Graphics.BLACK));
	g.drawString(options[current_selection], x + 5, y + height - 5);
    }

    private void drawFocusOut(int x, int y, Graphics g) {
	g.setColor(Graphics.getColorOfName(Graphics.BLACK));
	g.drawRect(x, y, width, height);
	g.setColor(Graphics.getColorOfName(Graphics.BLACK));
	g.drawString(options[current_selection], x + 5, y + height - 5);
    }

    private void drawFocusIn(int x, int y, Graphics g) {
	g.setColor(Graphics.getColorOfName(Graphics.RED));
	g.drawRect(x - 1, y - 1, width + 2, height + 2);
	g.drawRect(x, y, width, height);
	g.setColor(Graphics.getColorOfName(Graphics.BLACK));
	g.drawString(options[current_selection], x + 5, y + height - 5);
	for (int i = 0; i < options.length; i++) {
	    int by = y + (height * (i + 1)) + 2;
	    int fg_color = Graphics.getColorOfName(Graphics.BLACK);
	    int bg_color = Graphics.getColorOfName(Graphics.WHITE);
	    if (selection == i) {
		fg_color = Graphics.getColorOfName(Graphics.WHITE);
		bg_color = Graphics.getColorOfName(Graphics.BLUE);
	    }
	    g.setColor(bg_color);
	    g.fillRect(x, by, width, height);
	    g.setColor(Graphics.getColorOfName(Graphics.BLACK));
	    g.drawRect(x, by, width, height);
	    g.setColor(fg_color);
	    g.drawString(options[i], x + 5, by + height - 5);
	}
    }

    private static int getMaxWidth(String[] options, Font font) {
	int w = 0;
	for (int i = 0; i < options.length; i++) {
	    int tmp = font.getBBoxWidth(options[i]);
	    if (w < tmp) {
		w = tmp;
	    }
	}
	return w;
    }

    private static int getMaxHeight(String[] options, Font font) {
	int h = 0;
	for (int i = 0; i < options.length; i++) {
	    int tmp = font.getBBoxHeight(options[i]);
	    if (h < tmp) {
		h = tmp;
	    }
	}
	return h;
    }
}
