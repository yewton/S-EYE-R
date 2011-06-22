package jp.gr.java_conf.yewton.syer.view;

import jp.gr.java_conf.yewton.syer.Main;

import com.docomostar.StarApplication;
import com.docomostar.io.ConnectionException;
import com.docomostar.media.MediaImage;
import com.docomostar.media.MediaManager;
import com.docomostar.ui.Canvas;
import com.docomostar.ui.Display;
import com.docomostar.ui.Graphics;
import com.docomostar.ui.Image;
import com.docomostar.util.Timer;
import com.docomostar.util.TimerListener;

public class OpeningCanvas extends Canvas implements TimerListener {
    Image title = null;
    Timer t = null;
    Main parent = null;
    int alpha = 0;
    int sign = 1;
    boolean ap = false;

    public OpeningCanvas(Main parent) {
	this.parent = parent;
	try {
	    MediaImage m = MediaManager.getImage("resource:///title.jpg");
	    m.use();
	    title = m.getImage();
	} catch (ConnectionException e) {
	    e.printStackTrace();
	    StarApplication.getThisStarApplication().terminate();
	}
	setBackground(Graphics.getColorOfName(Graphics.BLACK));
	t = new Timer();
	t.setListener(this);
	t.setRepeat(false);
	t.setTime(30);
	t.start();
    }

    public void paint(Graphics g) {
	g.lock();
	g.clearRect(0, 0, Display.getWidth(), Display.getHeight());
	title.setAlpha(alpha);
	g.drawImage(title, 0, 0);
	g.unlock(true);
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
	if(param == Display.KEY_SELECT) {
	    parent.showMainCanvas();
	    this.t.stop();
	}
    }

    void processKeyReleasedEvent(int param) {
    }

    public void timerExpired(Timer timer) {
	alpha += 0x11 * sign;
	if (0xFF <= alpha) {
	    alpha = 0xFF;
	    ap = true;
	    sign = -1;
	    t.setTime(2000);
	} else {
	    t.setTime(30);
	}
	if (ap && alpha < 0) {
	    alpha = 0;
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException e) {
		// TODO Ž©“®¶¬‚³‚ê‚½ catch ƒuƒƒbƒN
		e.printStackTrace();
	    }
	    parent.showMainCanvas();
	    return;
	}
	t.start();
	repaint();
    }
}
