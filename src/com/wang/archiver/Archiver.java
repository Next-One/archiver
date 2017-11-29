package com.wang.archiver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.wang.util.Utils;

/**
 * ��һ���ļ����µ������ļ��鵵��Ϊһ���ļ� ����û��ж���ļ������Խ������һ���ļ��£����й鵵 ��ʽ����: ǰ4�ֽڣ�Ϊ���ļ���������
 * �������������ֽ�Ϊ�ļ����� Ȼ��4���ֽ�Ϊ���ļ������ļ��������������ļ��У� ���ŵ��ĸ��ֽ�Ϊ��һ���ļ����ĳ��ȣ� �����������ֽ�Ϊ�ļ���
 * �����ĸ��ֽ�Ϊ�ļ����ȣ�������ֻ�ܹ鵵������4G(2��32�η�)�ĵ����ļ� Ȼ�����ļ��еĸ��� ���ŵ��ĸ��ֽ�Ϊ��һ���ļ������ĳ��ȣ�
 * �����������ֽ�Ϊ�ļ����� �����ĸ��ֽ�Ϊ�ļ������ļ��������Ӷ��γɵݹ�
 * 
 * @author root
 *
 */
public class Archiver {
	private static final Logger LOG = Logger.getLogger(Archiver.class);
	/**
	 * Ĭ�ϵĻ�������С
	 */
	private static final int DEFALUT_SIZE = 10 * 1024;
	/**
	 * �鵵�ļ���׺
	 */
	private static final String POSTFIX = ".yar";
	/**
	 * �ļ��зָ���
	 */
	private static final String separator = "/";

	/**
	 * �ļ������
	 */
	private BufferedOutputStream out;
	/**
	 * �鵵�ļ�������
	 */
	private BufferedInputStream in;

	/**
	 * �����ļ��е�ǰ׺��ȡ�鵵�ļ���·��
	 * 
	 * @param srcPath
	 *            Ŀ���ļ�����·��
	 */
	public void createNewArchiver(String srcPath) {
		String destPath = Utils.getPrefix(srcPath);
		createNewArchiver(srcPath, destPath);
	}

	/**
	 * �����鵵�ļ�
	 * 
	 * @param srcPath
	 *            Ŀ���ļ�����·��
	 * @param destPath
	 *            �鵵�ļ����Ŀ¼
	 */
	public void createNewArchiver(String srcPath, String destPath) {
		File src = new File(srcPath);

		if (!destPath.contains(POSTFIX)) {
			String name = src.getName();
			destPath = destPath + separator + name + POSTFIX;
		}
		try {
			out = new BufferedOutputStream(new FileOutputStream(destPath));
		} catch (FileNotFoundException e) {
			LOG.error("destPath is exists! ", e);
		}

		setFileNameHead(src);

		if (!src.exists()) {
			LOG.warn("this file isn't exists!");
		} else if (src.isDirectory()) {
			handleDir(src);
		} else if (src.isFile()) {
			LOG.warn("file don't need archiver!");
		}
		// �鵵��ɣ��ر������
		Utils.close(out);
	}

	/**
	 * ���õ����ļ����ļ�ͷ
	 * 
	 * @param src
	 *            �ļ�
	 */
	private void setFileNameHead(File src) {
		String fileName = src.getName();
		byte[] nameBytes = fileName.getBytes();
		byte[] lens = Utils.intToBytes(nameBytes.length);
		Utils.write(out, lens, nameBytes);
	}

	/**
	 * ����鵵�ļ���
	 * 
	 * @param src
	 */
	private void handleDir(File src) {
		List<File> files = new ArrayList<>();
		List<File> dirs = new ArrayList<>();
		for (File file : src.listFiles()) {
			if (file.isDirectory()) {
				dirs.add(file);
			} else if (file.isFile()) {
				files.add(file);
			}
		}
		handlerFiles(files);
		handlerDirs(dirs);
	}

	/**
	 * ��������ļ���
	 * 
	 * @param dirs
	 *            �ļ���
	 */
	private void handlerDirs(List<File> dirs) {
		setContentHead(dirs.size());
		for (File dir : dirs) {
			setFileNameHead(dir);
			handleDir(dir);
		}
	}

	/**
	 * ��������ļ�
	 * 
	 * @param files
	 *            �ļ�
	 */
	private void handlerFiles(List<File> files) {
		setContentHead(files.size());
		for (File file : files) {
			writeOneFile(file);
		}
	}

	/**
	 * д�������ļ�
	 * 
	 * @param file
	 *            �ļ�
	 */
	private void writeOneFile(File file) {
		setFileOneHead(file);
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			byte[] buf = new byte[DEFALUT_SIZE];
			int len = -1;
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			out.flush();
		} catch (IOException e) {
			LOG.error("read file error", e);
		} finally {
			Utils.close(in);
		}
	}

	/**
	 * ���õ����ļ����ļ�ͷ
	 * 
	 * @param file
	 */
	private void setFileOneHead(File file) {
		setFileNameHead(file);
		// �ļ��������ΪInteger.MAX_VALUE
		setContentHead((int) file.length());
	}

	/**
	 * �����ļ���ͷ
	 * 
	 * @param size
	 */
	private void setContentHead(int size) {
		byte[] fileNums = Utils.intToBytes(size);
		Utils.write(out, fileNums);
	}

	/**
	 * �жϴ�����ļ��Ƿ����
	 * 
	 * @param src
	 * @return
	 */
	private boolean isExists(File src) {
		if (!src.exists()) {
			LOG.warn("file isn't exists");
			return false;
		} else {
			return true;
		}
	}

	public void unArchiver(String srcPath) {
		File src = new File(srcPath);
		String destPath = src.getParent();
		unArchiver(src, destPath);
	}

	public void unArchiver(String srcPath, String destPath) {
		unArchiver(new File(srcPath), destPath);
	}

	/**
	 * ��鵵�ļ�
	 * 
	 * @param src
	 * @param destPath
	 */
	public void unArchiver(File src, String destPath) {
		if (!isExists(src)) {
			return;
		}
		/*String fullPath = destPath;
		String fileName = Utils.getPrefix(src.getName());
		if(!destPath.endsWith(fileName)){
			fullPath += separator + fileName;
		}*/
		File dest = new File(destPath);
		dest.mkdirs();
		try {
			in = new BufferedInputStream(new FileInputStream(src));

			String dirName = readName();
			splitDir(destPath + separator + dirName);

		} catch (IOException e) {
			LOG.error("file not found !", e);
		} finally {
			// ��鵵��ɣ��ر�������
			Utils.close(in);
		}
	}

	/**
	 * ��ȡһ���鵵�ļ��ľ���·��
	 * 
	 * @param dirPath
	 */
	private void splitDir(String dirPath) {
		new File(dirPath).mkdirs();
		int filelen = Utils.read(in);
		for (int i = 0; i < filelen; i++) {
			String fileName = readName();
			splitFile(dirPath + separator + fileName);
		}

		int dirlen = Utils.read(in);
		for (int i = 0; i < dirlen; i++) {
			String dirName = readName();
			splitDir(dirPath + separator + dirName);
		}
	}

	/**
	 * ��ѹһ���鵵�ļ�
	 * 
	 * @param dest
	 */
	private void splitFile(String dest) {
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(dest));
			int len = Utils.read(in);
			int bufsize = 0;
			int count = 1;
			if (len < DEFALUT_SIZE) {
				bufsize = len;
			} else {
				count = len / DEFALUT_SIZE + 1;
				bufsize = DEFALUT_SIZE;
			}
			byte[] buf = new byte[bufsize];
			while (count > 0) {
				// ��������һ���ı仺������С
				if (count == 1) {
					bufsize = len % DEFALUT_SIZE;
					buf = new byte[bufsize];
				}
				in.read(buf);
				out.write(buf);
				count--;
			}
			out.flush();
		} catch (Exception e) {
			LOG.error(e);
		} finally {
			Utils.close(out);
		}
	}

	/**
	 * ��������ж�ȡ�ַ���
	 * 
	 * @return
	 */
	private String readName() {
		int len = Utils.read(in);
		byte[] buf = new byte[len];
		try {
			in.read(buf);
		} catch (IOException e) {
			LOG.error("file not found !", e);
		}
		return new String(buf, 0, len);
	}

}
