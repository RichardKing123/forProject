package min.t.member.control;

public class LoginCommand {
	private String id;
	private String passwd;
	private String dong;
	private int age;
	private String name;
	public LoginCommand(String id, String passwd, String dong, int age, String name) {
		super();
		this.id = id;
		this.passwd = passwd;
		this.dong = dong;
		this.age = age;
		this.name = name;
	}
	@Override
	public String toString() {
		return "아이디=" + id + ", 비밀번호=" + passwd + ", 동=" + dong + ", 나이=" 
				+ age+ ", 이름=" + name+ "]";
	}
	
	
}
