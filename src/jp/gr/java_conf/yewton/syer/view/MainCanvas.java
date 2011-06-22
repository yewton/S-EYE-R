package jp.gr.java_conf.yewton.syer.view;

import java.util.Vector;

import jp.gr.java_conf.yewton.docomostar.ui.util.CanvasUtil;
import jp.gr.java_conf.yewton.syer.Main;

import com.docomostar.StarApplication;
import com.docomostar.device.DeviceException;
import com.docomostar.device.RawImageCapture;
import com.docomostar.io.ConnectionException;
import com.docomostar.media.EncodedImage;
import com.docomostar.media.MediaImage;
import com.docomostar.media.MediaManager;
import com.docomostar.system.ImageStore;
import com.docomostar.ui.Canvas;
import com.docomostar.ui.Display;
import com.docomostar.ui.Font;
import com.docomostar.ui.Graphics;
import com.docomostar.ui.Image;
import com.docomostar.ui.ImageEncoder;

public class MainCanvas extends Canvas {
    Main parent = null;
    Vector list = null;
    RawImageCapture ric = null;
    EffectListBoxSettings stg = null;
    RawImageHandler rih = null;
    byte[] rawImage = null;
    public static final int STATE_INIT = 0;
    public static final int STATE_IN_EFFECT_MODE = 1;
    public static final int STATE_RECORDING = 2;
    int state = STATE_INIT;
    int prevState = STATE_INIT;
    int imageWidth;
    int imageHeight;
    int imageFocusMode;
    String imageColorSpace;
    int[] pixels;
    int imagePosX;
    int imagePosY;
    Image image;
    boolean paintListBox = true;
    Image listboxImage = null;
    Image listboxSelectedImage = null;

    class EffectListBoxSettings {
	static final int WIDTH = 100;
	static final int HEIGHT = 80;
	static final int MARGIN_TOP = 20;
	static final int MARGIN_RIGHT = 20;
	static final int MARGIN_BOTTOM = 20;

	public int getMarginTop() {
	    return MARGIN_TOP;
	}

	public int getMarginRight() {
	    return MARGIN_RIGHT;
	}

	public int getMarginBottom() {
	    return MARGIN_BOTTOM;
	}

	public int getWidth() {
	    return WIDTH;
	}

	public int getHeight() {
	    return HEIGHT;
	}
    }

    public MainCanvas() {
	this((Main) StarApplication.getThisStarApplication(), RawImageCapture
		.getRawImageCapture(0));
    }

    public MainCanvas(Main parent, RawImageCapture ric) {
	this.parent = parent;
	list = new Vector();
	stg = new EffectListBoxSettings();
	this.ric = ric;

	list.addElement("Normal");
	list.addElement("Monochrome");
	list.addElement("Thermal");
	list.addElement("Nocht");
	list.addElement("Moz");
	list.addElement("Mono.Moz");
	refreshSoftLabel();
	setBackground(Graphics.getColorOfName(Graphics.BLACK));
	init();
	try {
	    MediaImage m = MediaManager.getImage("resource:///listbox.gif");
	    m.use();
	    listboxImage = m.getImage();
	    listboxImage.setAlpha(200);
	    m = MediaManager.getImage("resource:///listbox_selected.gif");
	    m.use();
	    listboxSelectedImage = m.getImage();
	    listboxSelectedImage.setAlpha(200);
	} catch (ConnectionException e) {
	    e.printStackTrace();
	    parent.terminate();
	}
    }

    public void init() {
	rawImage = new byte[ric.getRawImageLength()];
	imageWidth = ric.getImageSize()[0];
	imageHeight = ric.getImageSize()[1];
	imageFocusMode = ric.getFocusMode();
	imageColorSpace = ric.getColorSpace();
	pixels = new int[imageWidth * imageHeight];
	for (int i = 0; i < pixels.length; i++) {
	    pixels[i] = 0;
	}
	if (rih != null) {
	    rih.dispose();
	}
	rih = new RawImageHandler(this, ric, rawImage, pixels);
	(new Thread(rih)).start();
	imagePosX = (Display.getWidth() - imageWidth) / 2;
	imagePosY = (Display.getHeight() - imageHeight) / 2;

	image = Image.createImage(imageWidth, imageHeight);
    }

    public int getState() {
	return state;
    }

    public void paint(Graphics g) {
	Font font = Font.getFont(Font.FACE_SYSTEM | Font.STYLE_PLAIN, 24);
	g.lock();
	g.clearRect(0, 0, Display.getWidth(), Display.getHeight());
	g.setColor(Graphics.getColorOfName(Graphics.BLACK));
	g.setFont(font);
	paintImage(g);
	if (paintListBox) {
	    paintListBox(g);
	}
	g.unlock(true);
    }

    void paintImage(Graphics g) {
	if (rih.getMode() == RawImageHandler.MODE_NORMAL
		&& rih.isRawImageAvailable()) {
	    g.drawRawImage(rawImage, imagePosX, imagePosY, ric.getColorSpace(),
		    ric.getImageSize()[0], ric.getImageSize()[1]);
	} else {
	    Graphics img = image.getGraphics();
	    img.setRGBPixels(0, 0, imageWidth, imageHeight, pixels, 0);
	    g.drawImage(image, imagePosX, imagePosY);
	}
    }

    void paintListBox(Graphics g) {
	int dw = Display.getWidth();
	int dh = Display.getHeight();
	int x = dw - stg.getWidth() - stg.getMarginRight();
	int y = dh - stg.getHeight() - stg.getMarginBottom();
	int mode;
	int sy;
	switch (state) {
	case STATE_INIT:
	case STATE_RECORDING:
	    sy = stg.getHeight() * rih.getMode();
	    g.drawImage(listboxImage, x, y, 0, sy, 100, 80);
	    break;
	case STATE_IN_EFFECT_MODE:
	    mode = rih.getMode();
	    int y2 = y;
	    for (int i = 0; i < rih.getAvailableModes().length; i++, mode = rih
		    .getNextMode(mode), y2 -= stg.getHeight()) {
		sy = stg.getHeight() * mode;
		if (i == 0) {
		    paintEffectDescription(mode, g);
		    g.drawImage(listboxSelectedImage, x, y2, 0, sy,
			    stg.getWidth(), stg.getHeight());
		} else {
		    g.drawImage(listboxImage, x, y2, 0, sy, stg.getWidth(),
			    stg.getHeight());
		}
	    }
	    break;
	}
    }

    void paintEffectDescription(int mode, Graphics g) {
	int dw = Display.getWidth();
	int dh = Display.getHeight();

	g.setColor(Graphics.getColorOfRGB(0xFF, 0xFF, 0xFF, 0xC0));
	int w = 300, h = 200;
	int x = (dw - (stg.getMarginRight() + stg.getWidth()) - w) / 2;
	int y = (dh - h) / 2;
	g.fillRect(x, y, w, h);
	Font font = Font.getFont(Font.FACE_SYSTEM | Font.STYLE_BOLD, 24);
	int color = Graphics.getColorOfName(Graphics.BLACK);

	String text = "";
	switch (mode) {
	case RawImageHandler.MODE_NORMAL:
	    text = "　画像に何もエフェクトを加えません。";
	    break;
	case RawImageHandler.MODE_MOSAIC:
	    text = "　画像にモザイクをかけます。";
	    break;
	case RawImageHandler.MODE_MONOCHROME:
	    text = "　画像をモノクロにします。";
	    break;
	case RawImageHandler.MODE_MONOCHROME_MOSAIC:
	    text = "　画像をモノクロにし、モザイクをかけます。";
	    break;
	case RawImageHandler.MODE_NOCHT:
	    text = "　画像に暗視ゴーグルのようなエフェクトを加えます。"
		    + "\n　某タクティカル・エスピオナージ・アクションゲーム風です。";
	    break;
	case RawImageHandler.MODE_THERMAL:
	    text = "　画像にサーマルゴーグルのような効果を加えます。"
		    + "\n　某タクティカル・エスピオナージ・アクションゲーム風です。";
	    ;
	    break;
	}
	CanvasUtil.prettyPrintText(x + 10, y + 10, w - 15, 4, font, color,
		text, g);
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

    void processKeyPressedEvent(int param) {
	switch (state) {
	case STATE_INIT:
	    switch (param) {
	    case Display.KEY_SELECT:
		changeState(STATE_RECORDING);
		break;
	    }
	    break;
	case STATE_IN_EFFECT_MODE:
	    switch (param) {
	    case Display.KEY_DOWN:
		rih.setMode(rih.getNextMode(rih.getMode()));
		repaint();
		break;
	    case Display.KEY_UP:
		rih.setMode(rih.getPrevMode(rih.getMode()));
		repaint();
		break;
	    }
	    break;
	case STATE_RECORDING:
	    switch (param) {
	    case Display.KEY_SELECT:
		changeState(STATE_INIT);
		break;
	    }
	    break;
	}
    }

    void processKeyReleasedEvent(int param) {
	switch (state) {
	case STATE_INIT:
	    switch (param) {
	    case Display.KEY_SOFT1:
		parent.showOptionCanvas();
		break;
	    case Display.KEY_SOFT2:
		changeState(STATE_IN_EFFECT_MODE);
		break;
	    case Display.KEY_SOFT3:
		saveImage();
		break;
	    case Display.KEY_SOFT4:
		parent.terminate();
		break;
	    }
	    break;
	case STATE_IN_EFFECT_MODE:
	    switch (param) {
	    case Display.KEY_SELECT:
		changeState(prevState);
		break;
	    case Display.KEY_SOFT2:
		changeState(prevState);
		break;
	    }
	    break;
	case STATE_RECORDING:
	    switch (param) {
	    case Display.KEY_SOFT2:
		changeState(STATE_IN_EFFECT_MODE);
		break;
	    }
	    break;
	}
    }

    void refreshSoftLabel() {
	switch (this.state) {
	case STATE_INIT:
	    setSoftLabel(SOFT_KEY_1, "設定");
	    setSoftLabel(SOFT_KEY_2, "効果");
	    setSoftLabel(SOFT_KEY_3, "保存");
	    setSoftLabel(SOFT_KEY_4, "終了");
	    setSoftLabel(SELECT_KEY, "開始");
	    setSoftArrowLabel(0);
	    break;
	case STATE_IN_EFFECT_MODE:
	    setSoftLabel(SOFT_KEY_1, "");
	    setSoftLabel(SOFT_KEY_2, "決定");
	    setSoftLabel(SOFT_KEY_3, "");
	    setSoftLabel(SOFT_KEY_4, "");
	    setSoftLabel(SELECT_KEY, "決定");
	    setSoftArrowLabel(ARROW_DOWN | ARROW_UP);
	    break;
	case STATE_RECORDING:
	    setSoftLabel(SOFT_KEY_1, "");
	    setSoftLabel(SOFT_KEY_2, "効果");
	    setSoftLabel(SOFT_KEY_3, "");
	    setSoftLabel(SOFT_KEY_4, "");
	    setSoftLabel(SELECT_KEY, "停止");
	    setSoftArrowLabel(0);
	    break;
	}
    }

    void saveImage() {
	if ((state != STATE_RECORDING) && (state != STATE_IN_EFFECT_MODE)) {
	    paintListBox = false;
	    repaint();
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException e1) {
		// TODO 自動生成された catch ブロック
		e1.printStackTrace();
	    }
	    ImageEncoder ie = ImageEncoder.getEncoder("JPEG");
	    EncodedImage eImage = ie.encode(this, imagePosX, imagePosY,
		    imageWidth, imageHeight);
	    try {
		MediaImage mImage = eImage.getImage();
		mImage.use();
		ImageStore.addEntry(mImage);
		mImage.dispose();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    paintListBox = true;
	    repaint();
	}
    }

    public void changeState(int state) {
	if (this.state == state) {
	    return;
	}
	switch (this.state) {
	case STATE_RECORDING:
	    if (state != STATE_IN_EFFECT_MODE) {
		rih.stop();
		ric.stop();
	    }
	    break;
	}
	this.prevState = this.state;
	switch (state) {
	case STATE_INIT:
	    this.state = STATE_INIT;
	    break;
	case STATE_IN_EFFECT_MODE:
	    this.state = STATE_IN_EFFECT_MODE;
	    break;
	case STATE_RECORDING:
	    if (this.state != STATE_IN_EFFECT_MODE) {
		ric.start();
		rih.start();
	    }
	    this.state = STATE_RECORDING;
	    break;
	}
	refreshSoftLabel();
	repaint();
    }

    class RawImageHandler implements Runnable {
	public final static int MODE_NORMAL = 0;
	public final static int MODE_MOSAIC = 1;
	public final static int MODE_MONOCHROME = 2;
	public final static int MODE_MONOCHROME_MOSAIC = 3;
	public final static int MODE_NOCHT = 4;
	public final static int MODE_THERMAL = 5;

	private boolean isAlive = true;

	public final static int DEFAULT_BG_COLOR_NOCHT = (0x33 * 0x10000)
		+ (0x66 * 0x100) + 0x33;
	public final static int DEFAULT_BG_COLOR_THERMAL = (0x66 * 0x10000)
		+ (0x33 * 0x100) + 0x33;

	boolean rawImageAvailable = false;

	final int[] availableModes = { MODE_NORMAL, MODE_MONOCHROME,
		MODE_NOCHT, MODE_THERMAL, MODE_MONOCHROME_MOSAIC, MODE_MOSAIC };

	int mode = MODE_NORMAL;

	final static int STATE_READY = 0;
	final static int STATE_BUSY = 1;
	int state = STATE_READY;
	RawImageCapture ric = null;
	MainCanvas parent = null;
	int[] pixels = null;
	byte[] rawImage = null;

	public boolean isRawImageAvailable() {
	    return rawImageAvailable;
	}

	public String getModeName(int mode) {
	    switch (mode) {
	    case MODE_NORMAL:
		return "NORMAL";
	    case MODE_MONOCHROME:
		return "MONO";
	    case MODE_MONOCHROME_MOSAIC:
		return "MONO.MOZ";
	    case MODE_MOSAIC:
		return "MOSAIC";
	    case MODE_NOCHT:
		return "NOCHT";
	    case MODE_THERMAL:
		return "THERMAL";
	    }
	    return "INVALID";
	}

	public int getNextMode(int mode) {
	    switch (mode) {
	    case MODE_NORMAL:
		return MODE_MOSAIC;
	    case MODE_MOSAIC:
		return MODE_MONOCHROME;
	    case MODE_MONOCHROME:
		return MODE_MONOCHROME_MOSAIC;
	    case MODE_MONOCHROME_MOSAIC:
		return MODE_NOCHT;
	    case MODE_NOCHT:
		return MODE_THERMAL;
	    case MODE_THERMAL:
		return MODE_NORMAL;
	    }
	    return -1;
	}

	public int getPrevMode(int mode) {
	    switch (mode) {
	    case MODE_NORMAL:
		return MODE_THERMAL;
	    case MODE_MOSAIC:
		return MODE_NORMAL;
	    case MODE_MONOCHROME:
		return MODE_MOSAIC;
	    case MODE_MONOCHROME_MOSAIC:
		return MODE_MONOCHROME;
	    case MODE_NOCHT:
		return MODE_MONOCHROME_MOSAIC;
	    case MODE_THERMAL:
		return MODE_NOCHT;
	    }
	    return -1;
	}

	public int getMode() {
	    return mode;
	}

	public void setMode(int mode) {
	    this.mode = mode;
	    if (isRawImageAvailable()) {
		switch (mode) {
		case MODE_NORMAL:
		    // colored();
		    break;
		case MODE_MONOCHROME:
		    mono();
		    break;
		case MODE_NOCHT:
		    nocht();
		    break;
		case MODE_THERMAL:
		    thermal();
		    break;
		case MODE_MONOCHROME_MOSAIC:
		    monoMosaic();
		    break;
		case MODE_MOSAIC:
		    mosaic();
		    break;
		}
	    }
	}

	public int[] getAvailableModes() {
	    return availableModes;
	}

	RawImageHandler(MainCanvas parent, RawImageCapture ric,
		byte[] rawImage, int[] pixels) {
	    this.ric = ric;
	    this.pixels = pixels;
	    this.parent = parent;
	    this.rawImage = rawImage;
	}

	public void start() {
	    if (state == STATE_READY) {
		state = STATE_BUSY;
	    }
	}

	public void stop() {
	    if (state == STATE_BUSY) {
		state = STATE_READY;
	    }
	}

	public void dispose() {
	    isAlive = false;
	}

	public void run() {
	    while (isAlive) {
		try {
		    Thread.sleep(100);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		if (state == STATE_BUSY) {
		    try {
			ric.getRawImage(rawImage);
			rawImageAvailable = true;
			switch (mode) {
			case MODE_NORMAL:
			    // colored();
			    break;
			case MODE_MONOCHROME:
			    mono();
			    break;
			case MODE_NOCHT:
			    nocht();
			    break;
			case MODE_THERMAL:
			    thermal();
			    break;
			case MODE_MONOCHROME_MOSAIC:
			    monoMosaic();
			    break;
			case MODE_MOSAIC:
			    mosaic();
			    break;
			}
		    } catch (DeviceException e) {
			parent.changeState(MainCanvas.STATE_INIT);
		    }
		    parent.repaint();
		}
	    }
	}

	void mosaic() {
	    int w = ric.getImageSize()[0];
	    int h = ric.getImageSize()[1];
	    int size = 8;
	    int len = rawImage.length;
	    int plen = pixels.length;
	    for (int y = 0; y < h; y += size) {
		for (int x = 0; x < w; x += size) {
		    int i = 0;
		    i = (y * w * 2) + 2 * x;
		    if (len <= i) {
			break;
		    }
		    int y0 = rawImage[i] & 0xFF;
		    int u0 = rawImage[i + 1] & 0xFF;
		    int v0 = rawImage[i + 3] & 0xFF;
		    int c = y0 - 16;
		    int d = u0 - 128;
		    int e = v0 - 128;
		    int r = clip((298 * c + 409 * e + 128) >> 8);
		    int g = clip((298 * c - 100 * d - 208 * e + 128) >> 8);
		    int b = clip((298 * c + 516 * d + 128) >> 8);
		    int a = (r * 0x10000) + (g * 0x100) + b;
		    for (int dy = 0; dy < size; dy++) {
			for (int dx = 0; dx < size; dx++) {
			    int idx = ((y + dy) * w) + (x + dx);
			    if (plen <= idx) {
				break;
			    }
			    pixels[idx] = a;
			}
		    }
		}
	    }
	}

	void monoMosaic() {
	    int w = ric.getImageSize()[0];
	    int h = ric.getImageSize()[1];
	    int size = 8;
	    int len = rawImage.length;
	    int plen = pixels.length;
	    for (int y = 0; y < h; y += size) {
		for (int x = 0; x < w; x += size) {
		    int i = 0;
		    i = (y * w * 2) + 2 * x;
		    if (len <= i) {
			break;
		    }
		    int y0 = rawImage[i] & 0xFF;
		    int a = (y0 * 0x10000) + (y0 * 0x100) + y0;
		    for (int dy = 0; dy < size; dy++) {
			for (int dx = 0; dx < size; dx++) {
			    int idx = ((y + dy) * w) + (x + dx);
			    if (plen <= idx) {
				break;
			    }
			    pixels[idx] = a;
			}
		    }
		}
	    }
	}

	void mono() {
	    int len = rawImage.length;
	    for (int i = 0, j = 0; i < len; i += 4, j += 2) {
		int y0 = rawImage[i] & 0xFF;
		int a = (y0 * 0x10000) + (y0 * 0x100) + y0;
		pixels[j] = pixels[j + 1] = a;
	    }
	}

	void nocht() {
	    int w = ric.getImageSize()[0];
	    int len = rawImage.length;
	    for (int i = 0; i < pixels.length; i++) {
		pixels[i] = DEFAULT_BG_COLOR_NOCHT;
	    }
	    for (int i = 0, j = 0; i < len; i += 4, j += 2) {
		if (j % w == 0) {
		    if ((j / w) % 3 == 1) {
			i += (w << 3) - 4;
			j += (w << 2) - 2;
			continue;
		    }
		}
		int m = rawImage[i] & 0xFF;
		int r = (0xFF - m) >> 4;
		int g = r >> 2;
		int b = r = 0xFF - r * r;
		g = 0xFF - g * g * g * g;
		pixels[j] = pixels[j + 1] = (r * 0x10000) + (g * 0x100) + b;
	    }
	}

	void thermal() {
	    int w = ric.getImageSize()[0];
	    int len = rawImage.length;
	    for (int i = 0; i < pixels.length; i++) {
		pixels[i] = DEFAULT_BG_COLOR_THERMAL;
	    }
	    for (int i = 0, j = 0; i < len; i += 4, j += 2) {
		if (j % w == 0) {
		    if ((j / w) % 3 == 1) {
			i += (w << 3) - 4;
			j += (w << 2) - 2;
			continue;
		    }
		}
		int m = rawImage[i] & 0xFF;
		int r = (0xFF - m) >> 4;
		int b = m >> 4;
		int g = b = b * b;
		r = 0xFF - r * r;
		pixels[j] = pixels[j + 1] = (r * 0x10000) + (g * 0x100) + b;
	    }
	}

	void colored() {
	    for (int i = 0, j = 0; i < rawImage.length; i += 4, j += 2) {
		int y0 = rawImage[i] & 0xFF;
		int u0 = rawImage[i + 1] & 0xFF;
		int v0 = rawImage[i + 3] & 0xFF;
		int c = y0 - 16;
		int d = u0 - 128;
		int e = v0 - 128;
		int r = clip((298 * c + 409 * e + 128) >> 8);
		int g = clip((298 * c - 100 * d - 208 * e + 128) >> 8);
		int b = clip((298 * c + 516 * d + 128) >> 8);
		pixels[j] = pixels[j + 1] = (r * 0x10000) + (g * 0x100) + b;
	    }
	}

	int clip(int v) {
	    if (0xFF < v) {
		return 0xFF;
	    } else if (v < 0) {
		return 0;
	    } else {
		return v;
	    }
	}
    }
}
