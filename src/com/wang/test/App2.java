package com.wang.test;

import com.wang.archiver.Archiver;

public class App2 {
	public static void main(String[] args) {
		Archiver archiver = new Archiver();
		if(args.length != 2){
			new Exception("����������2����һ�������ǹ鵵�ļ����ڶ����ǽ�鵵�ļ��д��Ŀ¼��");
			return;
		}
		archiver.unArchiver(args[0],args[1]);
	}
}
