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
    // �e�L�X�g�̓ǂݍ���
    public static synchronized String readText(String url) throws IOException {
	// �o�C�g�f�[�^�̃e�L�X�g�ւ̕ϊ�
	byte[] data = readByte(url);
	return new String(data);
    }

    // �摜�̓ǂݍ���
    public static synchronized Image readImage(String url) throws IOException {
	// �o�C�g�f�[�^�̉摜�ւ̕ϊ�
	byte[] data = readByte(url);

	MediaImage m = MediaManager.getImage(data);
	m.use();
	return m.getImage();
    }

    // �l�b�g����̃o�C�g�f�[�^�̓ǂݍ���
    private static byte[] readByte(String url) throws IOException {
	HttpConnection c = null;
	InputStream in = null;
	ByteArrayOutputStream out = null;
	try {
	    // �ڑ�
	    c = (HttpConnection) Connector.open(url, Connector.READ, true);
	    c.setRequestMethod(HttpConnection.GET);
	    c.connect();
	    in = c.openInputStream();
	    out = new ByteArrayOutputStream();

	    // �ǂݍ���
	    byte[] w = new byte[10240];
	    while (true) {
		int size = in.read(w);
		if (size <= 0)
		    break;
		out.write(w, 0, size);
	    }

	    return out.toByteArray();
	} catch (IOException e) {
	    // ��O����
	    throw e;
	} finally {
	    // �ؒf
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
