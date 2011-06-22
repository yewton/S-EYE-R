package jp.gr.java_conf.yewton.docomostar.ui.util;

import com.docomostar.ui.Display;
import com.docomostar.ui.Font;
import com.docomostar.ui.Graphics;

public class CanvasUtil {
    public final static int FOCUS_OUT = 0;
    public final static int FOCUS_ON = 1;
    public final static int FOCUS_IN = 2;

    private final static int SELECTBOX_BUTTON_PADDING = 8;

    /**
     * 与えられたテキストを適宜改行しながら表示する
     *
     * @param posX
     * @param posY
     * @param width
     * @param linespace
     * @param font
     * @param color
     * @param text
     * @param g
     * @return 下端の Y 位置
     */
    public static int prettyPrintText(int posX, int posY, int width,
	    int linespace, Font font, int color, String text, Graphics g) {
	g.setFont(font);
	g.setColor(color);
	int patch = font.getHeight() / 10;

	if (font.getBBoxWidth(text) < width) {
	    // もともとの文字列幅が描画域幅より小さければ描画して終了
	    g.drawString(text, posX, posY + font.getHeight() + linespace);
	    return posY + font.getBBoxHeight("┿") + linespace + patch;
	}

	int line = 1; // 行番号
	int offsetX = posX; // 描画開始 X 位置
	int offsetY = posY + (font.getBBoxHeight("┿") * line) + linespace;
	int rightEdge = posX + width; // 描画可能域の再右端
	// 一文字ずつ描画する
	for (int i = 0; i < text.length(); i++) {
	    String s = text.substring(i, i + 1);
	    if (s.equals("\n")) {
		// 改行コード
		offsetX = posX;
		line++;
		offsetY = posY + (font.getBBoxHeight("┿") * line) + linespace;
		continue;
	    }
	    int fw = font.getBBoxWidth(s);
	    if (rightEdge < offsetX + fw) {
		// 次に描画する文字が規定幅よりはみ出す場合
		line++; // 行番号をインクリメント
		offsetX = posX; // 描画開始 X 位置をリセット
		offsetY = posY + (font.getBBoxHeight("┿") * line) + linespace;
		if (font.getBBoxWidth(text.substring(i)) < width) {
		    // 残りの文字列幅が描画域幅より小さければ描画して終了
		    g.drawString(text.substring(i), offsetX, offsetY);
		    break;
		}
	    }
	    g.drawString(s, offsetX, offsetY);
	    offsetX += fw;
	}
	return offsetY + patch;
    }

    public static void drawScrollBar(int canvasHeight, int offset,
	    int scrollBarWidth, int fgColor, int bgColor, Graphics g) {
	g.setColor(bgColor);
	g.fillRect(Display.getWidth() - scrollBarWidth, 0, scrollBarWidth,
		Display.getHeight());
	int scrollBoxHeight = (Display.getHeight() * Display.getHeight())
		/ canvasHeight;
	int scrollBoxOffset = (Display.getHeight() * offset) / canvasHeight;
	g.setColor(fgColor);
	g.fillRect(Display.getWidth() - scrollBarWidth, scrollBoxOffset,
		scrollBarWidth, scrollBoxHeight);
    }

    public static void drawSelectBox(int x, int y, String[] list,
	    int current_selection, int selection, int focus, Font font,
	    Graphics g) {
	int h = getMaxHeight(list, font) + 5;
	int w = getMaxWidth(list, font) + h + 10;
	drawSelectBoxButton(x, y, w, h, g);
	switch (focus) {
	case FOCUS_OUT:
	case FOCUS_ON:
	    drawSelectBoxFocusOnOut(x, y, w, h, list, current_selection,
		    selection, focus, font, g);
	    break;
	case FOCUS_IN:
	    drawSelectBoxFocusIn(x, y, w, h, list, current_selection,
		    selection, font, g);
	    break;
	}
    }

    private static void drawSelectBoxButton(int x, int y, int w, int h,
	    Graphics g) {
	int bx = x + w - h;
	int by = y;
	g.setColor(Graphics.getColorOfName(Graphics.GRAY));
	g.fillRect(bx, by, h, h);
	g.setColor(Graphics.getColorOfName(Graphics.WHITE));
	int[] xPoints = { bx + SELECTBOX_BUTTON_PADDING,
		bx + h - SELECTBOX_BUTTON_PADDING, bx + (h / 2) };
	int[] yPoints = { by + SELECTBOX_BUTTON_PADDING,
		by + SELECTBOX_BUTTON_PADDING,
		by + h - SELECTBOX_BUTTON_PADDING };
	g.fillPolygon(xPoints, yPoints, 3);
    }

    private static void drawSelectBoxFocusOnOut(int x, int y, int w, int h,
	    String[] list, int current_selection, int selection, int focus,
	    Font font, Graphics g) {
	switch (focus) {
	case FOCUS_ON:
	    g.setColor(Graphics.getColorOfName(Graphics.YELLOW));
	    break;
	case FOCUS_OUT:
	    g.setColor(Graphics.getColorOfName(Graphics.BLACK));
	    break;
	}
	g.drawRect(x, y, w, h);
	g.setColor(Graphics.getColorOfName(Graphics.BLACK));
	g.drawString(list[current_selection], x + 5, y + h - 5);
    }

    private static void drawSelectBoxFocusIn(int x, int y, int w, int h,
	    String[] list, int current_selection, int selection, Font font,
	    Graphics g) {
	drawSelectBox(x, y, list, current_selection, current_selection,
		FOCUS_OUT, font, g);
	for (int i = 0; i < list.length; i++) {
	    int by = y + (h * (i + 1));
	    int fg_color = Graphics.getColorOfName(Graphics.BLACK);
	    int bg_color = Graphics.getColorOfName(Graphics.WHITE);
	    if (selection == i) {
		fg_color = Graphics.getColorOfName(Graphics.WHITE);
		bg_color = Graphics.getColorOfName(Graphics.BLUE);
	    }
	    g.setColor(bg_color);
	    g.fillRect(x, by, w, h);
	    g.setColor(Graphics.getColorOfName(Graphics.BLACK));
	    g.drawRect(x, by, w, h);
	    g.setColor(fg_color);
	    g.drawString(list[i], x + 5, by + h - 5);
	}
    }

    private static int getMaxWidth(String[] list, Font font) {
	int w = 0;
	for (int i = 0; i < list.length; i++) {
	    int tmp = font.getBBoxWidth(list[i]);
	    if (w < tmp) {
		w = tmp;
	    }
	}
	return w;
    }

    private static int getMaxHeight(String[] list, Font font) {
	int h = 0;
	for (int i = 0; i < list.length; i++) {
	    int tmp = font.getBBoxHeight(list[i]);
	    if (h < tmp) {
		h = tmp;
	    }
	}
	return h;
    }
}
