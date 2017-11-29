package com.wang.test;

import com.wang.archiver.Archiver;

public class App2 {
	public static void main(String[] args) {
		Archiver archiver = new Archiver();
		if(args.length != 2){
			new Exception("参数长度是2，第一个参数是归档文件，第二个是解归档文件夹存放目录！");
			return;
		}
		archiver.unArchiver(args[0],args[1]);
	}
}
