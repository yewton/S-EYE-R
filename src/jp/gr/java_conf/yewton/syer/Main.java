package jp.gr.java_conf.yewton.syer;

/*
 * Main.java
 *
 * DATE : 2011/04/24 17:27
 */
import jp.gr.java_conf.yewton.docomostar.ui.EasyDialog;
import jp.gr.java_conf.yewton.syer.view.MainCanvas;
import jp.gr.java_conf.yewton.syer.view.OpeningCanvas;
import jp.gr.java_conf.yewton.syer.view.OptionCanvas;

import com.docomostar.StarApplication;
import com.docomostar.device.RawImageCapture;
import com.docomostar.ui.Display;

/**
 * Main
 *
 * @author NAME
 */
public class Main extends StarApplication {
    OpeningCanvas oc = null;
    MainCanvas mc = null;
    OptionCanvas opc = null;
    RawImageCapture ric = null;

    class Settings {
	private static final int TIME_SHOWING_TITLE = 5000;

	public int getTimeShowingTitle() {
	    return TIME_SHOWING_TITLE;
	}
    }

    public void activated(int activateInfo) {
	if (mc.getState() == MainCanvas.STATE_RECORDING) {
	    ric.start();
	}
    }

    public void started(int launchType) {
	ric = RawImageCapture.getRawImageCapture(0);
	boolean yuy2Available = false;
	String msg = "";
	for (int i = 0; i < ric.getAvailableColorSpaces().length; i++) {
	    if (RawImageCapture.COLORSPACE_YUV422_YUY2.equals(ric
		    .getAvailableColorSpaces()[i])) {
		yuy2Available = true;
		ric.setColorSpace(RawImageCapture.COLORSPACE_YUV422_YUY2);
		break;
	    }
	    msg += ric.getAvailableColorSpaces()[i] + ", ";
	}
	if (yuy2Available == false) {
	    EasyDialog
		    .error("このアプリを利用するには、YUY2 色空間が使用可能である必要があります。お使いの端末ではご利用になれません。お使いの端末で使用可能な色空間は次のとおりです:"
			    + msg);
	    this.terminate();
	}
	oc = new OpeningCanvas(this);
	mc = new MainCanvas(this, ric);
	opc = new OptionCanvas(this, ric);
	Display.setCurrent(oc);
    }

    public void showMainCanvas() {
	if (!Display.getCurrent().equals(mc)) {
	    oc = null;
	    mc.init();
	    Display.setCurrent(mc);
	}
    }

    public void showOptionCanvas() {
	try {
	    Display.setCurrent(opc);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
