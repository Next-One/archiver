package com.wang.test;

import com.wang.archiver.Archiver;

public class App {
	public static void main(String[] args) {
		Archiver archiver = new Archiver();
		if(args.length != 2){
			new Exception("����������2����һ�������ǹ鵵�ļ��У��ڶ����ǹ鵵�ļ��д��Ŀ¼��");
			return;
		}
		archiver.createNewArchiver(args[0],args[1]);
	}
}
