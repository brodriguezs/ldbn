package se.umu.cs.ldbn.client.io;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;

public final class UserManagement extends AbstractRequestSender {

	private static UserManagement inst;

	public static UserManagement get() {
		if(inst == null) {
			inst = new UserManagement();
		}
		return inst;
	}

	private ArrayList<UserManagementListener> listeners;
	private String url;
	private String data;

	private UserManagement() {
		listeners = new ArrayList<>();
	}

	public void addListener(UserManagementListener umc) {
		if (umc != null) {
			listeners.add(umc);
		}
	}

	public void removeListener(UserManagementListener umc) {
		if (umc != null) {
			listeners.remove(umc);
		}
	}

	public void sendUserRegistration(String newUserName, String newUserPassBase64,
			String newUserEmail) {
		url = Config.get().getUserRegisterScriptURL();
		data = "user_name="+newUserName+"&user_pass="+newUserPassBase64+"&user_mail="+newUserEmail;
		send();
	}

	public void sendUserChange(String newUserName, String newUserPassBase64, String newUserEmail) {
		url = Config.get().getUserChangeScriptURL();
		data = "";
		if(newUserName != null && !newUserName.equals("")) {
			data +=  "user_name="+newUserName+"&";
		}
		if(newUserPassBase64 != null && !newUserPassBase64.equals("")) {
			data +=  "user_pass="+newUserPassBase64+"&";
		}
		if(newUserEmail != null && !newUserEmail.equals("")) {
			data +=  "user_mail="+newUserEmail+"&";
		}
		if(data.equals("")) {
			for (UserManagementListener l : listeners) {
				l.setOKStatus();
			}
			return;
		}
		data = addSessionData(data);
		send();
	}

	protected String getData() {
		return data;
	}

	protected String getURL() {
		return url;
	}

	protected boolean handleResponse() {
		LdbnParser p = LdbnParser.get();
		if (p.getLastLdbnType() == LdbnParser.LDBN_TYPE.msg) {
			if(p.getMsgType() == LdbnParser.MSG_TYPE.ok) {
				Window.alert(p.getMsgText());
				for (UserManagementListener l : listeners) {
						l.setOKStatus();
				}
				return true;
			}
		}
		return false;
	}
}
