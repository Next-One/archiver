package com.wang.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * @author root 归档类使用的工具类
 */
public class Utils {
	private static final Logger LOG = Logger.getLogger(Utils.class);
	/**
	 * 关闭流
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
	 * 将一个字节数组转为int
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
	 * 将一个int转为字节数组
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
	 * 获取文件路径的前缀
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getPrefix(String filePath) {
		int index = filePath.lastIndexOf('.');
		return filePath.substring(0, index);
	}

	/**
	 * 向一个输出流中写出byte[]
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
	 * 从一个输入流中读取一个int数字
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
