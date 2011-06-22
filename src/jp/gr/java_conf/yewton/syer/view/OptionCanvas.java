package jp.gr.java_conf.yewton.syer.view;

import jp.gr.java_conf.yewton.docomostar.ui.EasyDialog;
import jp.gr.java_conf.yewton.docomostar.ui.SelectBox;
import jp.gr.java_conf.yewton.syer.Main;

import com.docomostar.StarApplication;
import com.docomostar.device.Camera;
import com.docomostar.device.RawImageCapture;
import com.docomostar.ui.Canvas;
import com.docomostar.ui.Display;
import com.docomostar.ui.Font;
import com.docomostar.ui.Graphics;

public class OptionCanvas extends Canvas {
    Main parent = null;
    RawImageCapture ric = null;

    private final static int PADDING = 30;
    private final static int LISTBOX_IMAGE_SIZE = 0;
    private final static int LISTBOX_FOCUS_MODE = 1;

    private final static int LISTBOX_IMAGE_SIZE_LABEL_X = PADDING;
    private final static int LISTBOX_IMAGE_SIZE_LABEL_Y = PADDING + 40;
    private final static int LISTBOX_IMAGE_SIZE_X = PADDING + 30;
    private final static int LISTBOX_IMAGE_SIZE_Y = PADDING + 50;
    private final static int LISTBOX_FOCUS_MODE_LABEL_X = PADDING;
    private final static int LISTBOX_FOCUS_MODE_LABEL_Y = PADDING + 120;
    private final static int LISTBOX_FOCUS_MODE_X = PADDING + 30;
    private final static int LISTBOX_FOCUS_MODE_Y = PADDING + 130;

    private int focus = LISTBOX_IMAGE_SIZE;
    private int[] listboxList = { LISTBOX_IMAGE_SIZE, LISTBOX_FOCUS_MODE };
    private SelectBox imageSize = null;
    private SelectBox focusMode = null;
    private SelectBox currentFocused = null;
    private SelectBox[] selectBoxes = null;

    public OptionCanvas() {
	this((Main) StarApplication.getThisStarApplication(), RawImageCapture
		.getRawImageCapture(0));
    }

    public OptionCanvas(Main parent, RawImageCapture ric) {
	this.parent = parent;
	this.ric = ric;

	setSoftLabel(SOFT_KEY_2, "戻る");
	setSoftLabel(SOFT_KEY_1, "保存");
	setBackground(Graphics.getColorOfName(Graphics.WHITE));
	Font font = Font.getFont(Font.FACE_SYSTEM | Font.STYLE_PLAIN, 24);
	{
	    int[][] l = ric.getAvailableImageSizes();
	    String[] options = new String[l.length];
	    for (int i = 0; i < l.length; i++) {
		options[i] = l[i][0] + "x" + l[i][1];
	    }
	    imageSize = new SelectBox(options, font);
	    imageSize.focusOn();
	}
	{
	    int[] l = ric.getAvailableFocusModes();
	    String[] options = new String[l.length];
	    for (int i = 0; i < l.length; i++) {
		String msg = "未定義";
		switch (l[i]) {
		case Camera.FOCUS_HARDWARE_SWITCH:
		    msg = "ハードウェアスイッチ";
		    break;
		case Camera.FOCUS_MACRO_MODE:
		    msg = "接写モード";
		    break;
		case Camera.FOCUS_NORMAL_MODE:
		    msg = "通常モード";
		    break;
		}
		options[i] = msg;
	    }
	    focusMode = new SelectBox(options, font);
	}
	selectBoxes = new SelectBox[2];
	selectBoxes[0] = imageSize;
	selectBoxes[1] = focusMode;
	currentFocused = imageSize;
    }

    private void save() {
	ric.setImageSize(ric.getAvailableImageSizes()[imageSize.getValue()][0],
		ric.getAvailableImageSizes()[imageSize.getValue()][1]);
	ric.setFocusMode(ric.getAvailableFocusModes()[focusMode.getValue()]);
	EasyDialog.info("保存しました");
    }

    public void init() {
	{
	    String[] l = imageSize.getOptions();
	    String s = ric.getImageSize()[0] + "x" + ric.getImageSize()[1];
	    for (int i = 0; i < l.length; i++) {
		if (s.equals(l[i])) {
		    imageSize.setSelection(i);
		    break;
		}
	    }
	}
	{
	    String[] l = focusMode.getOptions();
	    String s = ric.getImageSize()[0] + "x" + ric.getImageSize()[1];
	    switch (ric.getFocusMode()) {
	    case Camera.FOCUS_HARDWARE_SWITCH:
		s = "ハードウェアスイッチ";
		break;
	    case Camera.FOCUS_MACRO_MODE:
		s = "接写モード";
		break;
	    case Camera.FOCUS_NORMAL_MODE:
		s = "通常モード";
		break;
	    }
	    for (int i = 0; i < l.length; i++) {
		if (s.equals(l[i])) {
		    focusMode.setSelection(i);
		    break;
		}
	    }
	}
    }

    public void paint(Graphics g) {
	g.lock();
	g.clearRect(0, 0, Display.getWidth(), Display.getHeight());
	String msg = "設定";
	g.setColor(Graphics.getColorOfName(Graphics.BLACK));
	Font font = Font.getFont(Font.FACE_SYSTEM | Font.STYLE_PLAIN, 24);
	g.setFont(font);
	g.drawString(msg, (Display.getWidth() - font.getBBoxWidth(msg)) / 2,
		PADDING);
	paintOffFocusListBox(g);
	paintOnFocusListBox(g);
	g.drawString("問い合わせ先: yewton@gmail.com", 60, 300);
	g.unlock(true);
    }

    private void paintOffFocusListBox(Graphics g) {
	for (int i = 0; i < listboxList.length; i++) {
	    if (i != focus) {
		paintListBox(i, g);
	    }
	}
    }

    private void paintOnFocusListBox(Graphics g) {
	paintListBox(focus, g);
    }

    private void paintListBox(int i, Graphics g) {
	String label = null;
	switch (i) {
	case LISTBOX_IMAGE_SIZE:
	    label = "解像度の設定";
	    g.drawString(label, LISTBOX_IMAGE_SIZE_LABEL_X,
		    LISTBOX_IMAGE_SIZE_LABEL_Y);
	    imageSize.draw(LISTBOX_IMAGE_SIZE_X, LISTBOX_IMAGE_SIZE_Y, g);
	    break;
	case LISTBOX_FOCUS_MODE:
	    label = "フォーカスモードの設定";
	    g.drawString(label, LISTBOX_FOCUS_MODE_LABEL_X,
		    LISTBOX_FOCUS_MODE_LABEL_Y);
	    focusMode.draw(LISTBOX_FOCUS_MODE_X, LISTBOX_FOCUS_MODE_Y, g);
	    break;
	}
    }

    public void processEvent(int type, int param) {
	switch (type) {
	case Display.KEY_PRESSED_EVENT:
	    processKeyPressedEvent(param);
	    break;
	case Display.KEY_RELEASED_EVENT:
	    processKeyReleasedEvent(param);
	default:
	    break;
	}
    }

    void processKeyReleasedEvent(int param) {
	switch (param) {
	case Display.KEY_SOFT1:
	    save();
	    break;
	case Display.KEY_SOFT2:
	    init();
	    parent.showMainCanvas();
	    break;
	}
    }

    private void focusNext() {
	if (currentFocused.equals(imageSize)) {
	    imageSize.focusOut();
	    focusMode.focusOn();
	    currentFocused = focusMode;
	} else if (currentFocused.equals(focusMode)) {
	    focusMode.focusOut();
	    imageSize.focusOn();
	    currentFocused = imageSize;
	}
    }

    private void focusPrev() {
	if (currentFocused.equals(imageSize)) {
	    imageSize.focusOut();
	    focusMode.focusOn();
	    currentFocused = focusMode;
	} else if (currentFocused.equals(focusMode)) {
	    focusMode.focusOut();
	    imageSize.focusOn();
	    currentFocused = imageSize;
	}
    }

    void processKeyPressedEvent(int param) {
	switch (param) {
	case Display.KEY_SELECT:
	    switch (currentFocused.getFocus()) {
	    case SelectBox.FOCUS_ON:
		currentFocused.focusIn();
		break;
	    case SelectBox.FOCUS_IN:
		currentFocused.focusOn();
		break;
	    }
	    repaint();
	    break;
	case Display.KEY_DOWN:
	    switch (currentFocused.getFocus()) {
	    case SelectBox.FOCUS_IN:
		currentFocused.selectNext();
		break;
	    case SelectBox.FOCUS_ON:
		focusNext();
		break;
	    }
	    repaint();
	    break;
	case Display.KEY_UP:
	    switch (currentFocused.getFocus()) {
	    case SelectBox.FOCUS_IN:
		currentFocused.selectPrev();
		break;
	    case SelectBox.FOCUS_ON:
		focusPrev();
		break;
	    }
	    repaint();
	    break;
	}
    }
}
