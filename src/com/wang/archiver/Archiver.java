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
 * 将一个文件夹下的所有文件归档成为一个文件 如果用户有多个文件，可以将其放在一个文夹下，进行归档 格式如下: 前4字节，为该文件夹名长度
 * 接下来的若干字节为文件夹名 然后4个字节为该文件夹下文件个数（不包括文件夹） 接着的四个字节为第一个文件名的长度， 接下来若干字节为文件名
 * 接着四个字节为文件长度，因此这个只能归档不超过4G(2的32次方)的单个文件 然后是文件夹的个数 接着的四个字节为第一个文件夹名的长度，
 * 接下来若干字节为文件夹名 接着四个字节为文件夹下文件个数，从而形成递归
 * 
 * @author root
 *
 */
public class Archiver {
	private static final Logger LOG = Logger.getLogger(Archiver.class);
	/**
	 * 默认的缓冲区大小
	 */
	private static final int DEFALUT_SIZE = 10 * 1024;
	/**
	 * 归档文件后缀
	 */
	private static final String POSTFIX = ".yar";
	/**
	 * 文件夹分隔符
	 */
	private static final String separator = "/";

	/**
	 * 文件输出流
	 */
	private BufferedOutputStream out;
	/**
	 * 归档文件输入流
	 */
	private BufferedInputStream in;

	/**
	 * 根据文件夹的前缀获取归档文件的路径
	 * 
	 * @param srcPath
	 *            目标文件绝对路径
	 */
	public void createNewArchiver(String srcPath) {
		String destPath = Utils.getPrefix(srcPath);
		createNewArchiver(srcPath, destPath);
	}

	/**
	 * 创建归档文件
	 * 
	 * @param srcPath
	 *            目标文件绝对路径
	 * @param destPath
	 *            归档文件存放目录
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
		// 归档完成，关闭输出流
		Utils.close(out);
	}

	/**
	 * 设置单个文件的文件头
	 * 
	 * @param src
	 *            文件
	 */
	private void setFileNameHead(File src) {
		String fileName = src.getName();
		byte[] nameBytes = fileName.getBytes();
		byte[] lens = Utils.intToBytes(nameBytes.length);
		Utils.write(out, lens, nameBytes);
	}

	/**
	 * 处理归档文件夹
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
	 * 处理各个文件夹
	 * 
	 * @param dirs
	 *            文件夹
	 */
	private void handlerDirs(List<File> dirs) {
		setContentHead(dirs.size());
		for (File dir : dirs) {
			setFileNameHead(dir);
			handleDir(dir);
		}
	}

	/**
	 * 处理各个文件
	 * 
	 * @param files
	 *            文件
	 */
	private void handlerFiles(List<File> files) {
		setContentHead(files.size());
		for (File file : files) {
			writeOneFile(file);
		}
	}

	/**
	 * 写出单个文件
	 * 
	 * @param file
	 *            文件
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
	 * 设置单个文件的文件头
	 * 
	 * @param file
	 */
	private void setFileOneHead(File file) {
		setFileNameHead(file);
		// 文件内容最大为Integer.MAX_VALUE
		setContentHead((int) file.length());
	}

	/**
	 * 设置文件的头
	 * 
	 * @param size
	 */
	private void setContentHead(int size) {
		byte[] fileNums = Utils.intToBytes(size);
		Utils.write(out, fileNums);
	}

	/**
	 * 判断传入的文件是否存在
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
	 * 解归档文件
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
			// 解归档完成，关闭输入流
			Utils.close(in);
		}
	}

	/**
	 * 获取一个归档文件的绝对路径
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
	 * 解压一个归档文件
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
				// 如果是最后一个改变缓冲区大小
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
	 * 从输出流中读取字符串
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
