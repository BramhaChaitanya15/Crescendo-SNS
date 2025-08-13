package com.connect.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {
	//function for removing alerts from the sessions
	public void removeAlertFromSession() {
		try {
			HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
			session.removeAttribute("alert");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
