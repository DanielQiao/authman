package com.laoxu.java.authman.util;

import com.laoxu.java.authman.model.SysUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;


public class SessionUtil {

	/**
	 * 获取登录用户的userId
	 * @return
	 */
	public static Integer getloginUserId()
	{
		Subject subject = SecurityUtils.getSubject();
		SysUser sessionUser = (SysUser)subject.getSession().getAttribute("userSession");
		return sessionUser.getId();
	}

	/**
	 * 获取登录用户的账号
	 * @return
	 */
	public static String getloginUserAccountName()
	{
		Subject subject = SecurityUtils.getSubject();
		SysUser sessionUser = (SysUser)subject.getSession().getAttribute("userSession");
		return sessionUser.getUsername();
	}

	/**
	 * 获取登录用户
	 * @return
	 */
	public static SysUser getloginUser()
	{
		Subject subject = SecurityUtils.getSubject();
		return (SysUser)subject.getSession().getAttribute("userSession");
	}


	/**
	 * 返回用户的IP地址
	 * @param
	 * @return
	 */
	public static String getUserIP() {
		Subject subject = SecurityUtils.getSubject();
		return subject.getSession().getHost();
	}

	public static Object getAttr(Object key) {
		Subject subject = SecurityUtils.getSubject();
		return subject.getSession().getAttribute(key);
	}
}
