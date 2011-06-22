package jp.gr.java_conf.yewton.docomostar.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;

import com.docomostar.io.HttpConnection;
import com.docomostar.media.MediaImage;
import com.docomostar.media.MediaManager;
import com.docomostar.ui.Image;

public class Http {
    // テキストの読み込み
    public static synchronized String readText(String url) throws IOException {
	// バイトデータのテキストへの変換
	byte[] data = readByte(url);
	return new String(data);
    }

    // 画像の読み込み
    public static synchronized Image readImage(String url) throws IOException {
	// バイトデータの画像への変換
	byte[] data = readByte(url);

	MediaImage m = MediaManager.getImage(data);
	m.use();
	return m.getImage();
    }

    // ネットからのバイトデータの読み込み
    private static byte[] readByte(String url) throws IOException {
	HttpConnection c = null;
	InputStream in = null;
	ByteArrayOutputStream out = null;
	try {
	    // 接続
	    c = (HttpConnection) Connector.open(url, Connector.READ, true);
	    c.setRequestMethod(HttpConnection.GET);
	    c.connect();
	    in = c.openInputStream();
	    out = new ByteArrayOutputStream();

	    // 読み込み
	    byte[] w = new byte[10240];
	    while (true) {
		int size = in.read(w);
		if (size <= 0)
		    break;
		out.write(w, 0, size);
	    }

	    return out.toByteArray();
	} catch (IOException e) {
	    // 例外処理
	    throw e;
	} finally {
	    // 切断
	    try {
		if (out != null)
		    c.close();
		if (in != null)
		    in.close();
		if (c != null)
		    c.close();
	    } catch (IOException e2) {
	    }
	}
    }
}
