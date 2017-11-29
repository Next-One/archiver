package com.wang.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * @author root �鵵��ʹ�õĹ�����
 */
public class Utils {
	private static final Logger LOG = Logger.getLogger(Utils.class);
	/**
	 * �ر���
	 * 
	 * @param io
	 */
	public static void close(Closeable... io) {
		for (Closeable temp : io) {
			try {
				if (temp != null) {
					temp.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static byte[] shortToBytes(short s) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (s);
		bytes[1] = (byte) (s >> 8);
		return bytes;
	}

	public static short bytesToShort(byte[] bytes) {
		int s0 = bytes[0] & 0xFF;
		int s1 = (bytes[1] & 0xFF) << 8;
		return (short) (s0 + s1);
	}

	/**
	 * ��һ���ֽ�����תΪint
	 * 
	 * @param bytes
	 * @return
	 */
	public static int bytesToInt(byte[] bytes) {
		int i0 = bytes[0] & 0xFF;
		int i1 = (bytes[1] & 0xFF) << 8;
		int i2 = (bytes[2] & 0xFF) << 16;
		int i3 = (bytes[3] & 0xFF) << 24;
		return i0 + i1 + i2 + i3;
	}

	/**
	 * ��һ��intתΪ�ֽ�����
	 * 
	 * @param i
	 * @return
	 */
	public static byte[] intToBytes(int i) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) i;
		bytes[1] = (byte) (i >> 8);
		bytes[2] = (byte) (i >> 16);
		bytes[3] = (byte) (i >> 24);
		return bytes;
	}

	/**
	 * ��ȡ�ļ�·����ǰ׺
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getPrefix(String filePath) {
		int index = filePath.lastIndexOf('.');
		return filePath.substring(0, index);
	}

	/**
	 * ��һ���������д��byte[]
	 * 
	 * @param out
	 * @param data
	 */
	public static void write(OutputStream out, byte[]... data) {
		try {
			for (byte[] bs : data) {
				out.write(bs);
			}
		} catch (IOException e) {
			LOG.error("output stream write error ! ", e);
		}
	}

	/**
	 * ��һ���������ж�ȡһ��int����
	 * 
	 * @param in
	 * @return
	 */
	public static int read(InputStream in) {
		byte[] buf = new byte[4];
		try {
			in.read(buf);
		} catch (IOException e) {
			LOG.error("input stream read error ! ", e);
		}
		return bytesToInt(buf);
	}
}
