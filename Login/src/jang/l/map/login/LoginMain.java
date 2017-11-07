package jang.l.map.login;

import java.util.Scanner;

import min.t.member.control.LoginCommand;

public class LoginMain {
	public static void main(String[] args) {
		boolean isStop = false;
		Scanner scanner = new Scanner(System.in);
		LoginService loginService = new LoginService();
	do {
		System.out.println();
		System.out.println("로그인 화면입니다");
		System.out.println("아이디와 비번 입력하셈");
		System.out.print("아이디 : ");
		String id = scanner.next();
		System.out.print("비밀번호: ");
		String passwd = scanner.next();
		LoginCommand command = loginService.login(id, passwd);
		if (command == null) {
			System.out.println("아이디나 비밀번호가 일치하지 않습니다");
		} else {
			System.out.println("로그인한 사용자 정보");
			System.out.println(command);
			isStop = true;
		}
	} while (isStop);
		scanner.close();
	}
}
