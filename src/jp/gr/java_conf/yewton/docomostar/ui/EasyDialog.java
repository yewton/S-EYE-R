package jp.gr.java_conf.yewton.docomostar.ui;

import com.docomostar.ui.Dialog;
import com.docomostar.ui.UIException;

public class EasyDialog {
    public synchronized static void error(String text) {
	Dialog dialog = new Dialog(Dialog.DIALOG_ERROR, "ÉGÉâÅ[");
	dialog.setText(text);
	show(dialog);
    }

    public synchronized static void info(String text) {
	Dialog dialog = new Dialog(Dialog.DIALOG_INFO, "èÓïÒ");
	dialog.setText(text);
	show(dialog);
    }

    private synchronized static void show(Dialog dialog) {
	try {
	    dialog.show();
	} catch (UIException e) {
	    e.printStackTrace();
	}
    }
}
