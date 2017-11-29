package com.wang.test;

import com.wang.archiver.Archiver;

public class App {
	public static void main(String[] args) {
		Archiver archiver = new Archiver();
		if(args.length != 2){
			new Exception("参数长度是2，第一个参数是归档文件夹，第二个是归档文件夹存放目录！");
			return;
		}
		archiver.createNewArchiver(args[0],args[1]);
	}
}
