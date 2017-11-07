package jang.l.map.login;

import java.util.HashMap;

import min.t.member.control.LoginCommand;

public class LoginService {
	private HashMap<String, String> login;
	private HashMap<String, LoginCommand> member;
	public LoginService() {
		login = new HashMap<String, String>();
		member = new HashMap<String, LoginCommand>();
		login.put("min", "1234");
		member.put("min", new LoginCommand("min", "1234", "월계동", 33, "송정민"));
	}
	public LoginCommand login(String id, String passwd) {
		LoginCommand command = null;
		if (login.containsKey(id)) {
			if (login.get(id).equals(passwd)) {
				command = member.get(id);
			}
		} return command;
	}
}
