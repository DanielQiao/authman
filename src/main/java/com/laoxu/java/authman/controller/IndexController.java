package com.laoxu.java.authman.controller;

import com.feilong.core.Validator;
import com.laoxu.java.authman.common.MD5Util;
import com.laoxu.java.authman.common.Result;
import com.laoxu.java.authman.common.ResultUtil;
import com.laoxu.java.authman.model.SysMenu;
import com.laoxu.java.authman.model.SysUser;
import com.laoxu.java.authman.service.SysMenuService;
import com.laoxu.java.authman.service.SysOperLogService;
import com.laoxu.java.authman.service.SysUserService;
import com.laoxu.java.authman.util.RandomValidateCodeUtil;
import com.laoxu.java.authman.util.TreeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户账户表 前端控制器
 * </p>
 *
 * @author xj
 * @since 2016-12-20
 */
@Controller
public class IndexController {
	private Logger logger = LogManager.getLogger(IndexController.class.getName());

	@Resource
	private SysMenuService menuService;

	@Autowired
	SysOperLogService logService;

	@Autowired
	SysUserService userService;

	@RequestMapping({"/","/index" })
	public String index(Map<String, Object> map, String page) {
		SysUser userEntity = (SysUser) SecurityUtils.getSubject().getPrincipal();
		List<SysMenu> treeMenuList = null;
		// 判断用户是否授予角色
		if(userEntity.getRole()==null){
			return "403";
		}
		if(userEntity.getRole().getRoleCode().equals("administrator")){
			treeMenuList = new TreeUtil().treeMenuList((List<SysMenu>) menuService.listByMap(null), 0);
		}else{
			treeMenuList = menuService.findTreeMenusByUserId(userEntity.getId());
		}
		map.put("menus", treeMenuList);
		map.put("user", userEntity);
		map.put("page", page);

		return "index";
	}

	@RequestMapping(value="/login",method= RequestMethod.GET)
    public String login(HttpServletResponse response, ServletRequest request, @RequestHeader HttpHeaders header, Map<String, Object> map, String msg){
		Subject subject = SecurityUtils.getSubject();
		if(subject.isAuthenticated()){
			return "redirect:index";
		}
		if(Validator.isNotNullOrEmpty(header.get("X-Requested-With"))){
			response.setHeader("sessionstatus", "timeout");
		}
       return "login";
    }

    @ResponseBody
	@RequestMapping(value = "/dologin", method = RequestMethod.POST)
	public Result<String> doLogin(String username, String password, String rememberMe,
								  String verifyCode, Map<String, Object> map,
								  HttpSession session) {
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>{},{},{}",username,password,rememberMe);
		String msg = "";
		Subject subject = SecurityUtils.getSubject();;
        // 判断验证码
		String sessionVerifyCode = (String) session.getAttribute("SESSION_VERIFY_CODE");
		if(!verifyCode.equals(sessionVerifyCode)){
			msg = "验证码错误！";
		}else {
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			token.setRememberMe(rememberMe==null?false:true);
			subject.getSession().setAttribute("account", username);
			try{
				subject.login(token);
				logger.info("{}登陆成功!",username);
				subject.getSession().removeAttribute("msg");
				//return "redirect:/index";
				return ResultUtil.ok();
			}catch(UnknownAccountException e){
				msg = "账户不存在！";
			}catch(IncorrectCredentialsException e){
				msg = "密码错误！";
			}catch (LockedAccountException e) {
				msg = "您的账户已被锁定,请与管理员联系！";
			}catch(ExcessiveAttemptsException e){
				msg = "登录失败次数过多,请稍后再试！";
			}catch(Exception e){
				msg="系统发生错误，请联系管理员！";
			}
		}

		map.put("msg", msg);
		// 此方法不处理登录成功,由shiro进行处理.
		logger.info("{}登陆失败，error={}!",username,msg);
		subject.getSession().setAttribute("msg", msg);
		return ResultUtil.fail(msg);
	}

	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logout() {
		// 注销登录
		SecurityUtils.getSubject().logout();
		return "redirect:login";
	}

	/**
	 * 生成验证码
	 */
	@RequestMapping(value = "/getVerifyCode")
	public void getVerify(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
			response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expire", 0);
			RandomValidateCodeUtil randomValidateCode = new RandomValidateCodeUtil();
			randomValidateCode.getRandcode(request, response);//输出验证码图片方法
		} catch (Exception e) {
			logger.error("获取验证码失败>>>>   ", e);
		}
	}

	/**
	 *  修改密码
	 * @param param
	 * @return
	 */
	@PostMapping("/modifyPassword")
	@ResponseBody
	public Result<String> modifyPassword(@RequestBody Map<String,String> param,HttpServletRequest request){
		// 校验密码
		String oldPassword = param.get("oldPassword");
		String newPassword = param.get("newPassword");
		String newPassword2 = param.get("newPassword2");

		if(StringUtils.isEmpty(oldPassword)){
			return ResultUtil.fail("原密码不能为空！");
		}

		if(StringUtils.isEmpty(newPassword)){
			return ResultUtil.fail("新密码不能为空！");
		}

		if(StringUtils.isEmpty(newPassword2)){
			return ResultUtil.fail("确认密码不能为空！");
		}

		SysUser user = (SysUser) request.getSession().getAttribute("userSession");

		String realPassword = user.getPassword();
		if(!(MD5Util.md5Password(oldPassword,2).equals(realPassword))){
			return ResultUtil.fail("原密码输入错误！");
		}

		if(!(newPassword.equals(newPassword2))){
			return ResultUtil.fail("新密码输入不一致！");
		}

		user.setPassword(MD5Util.md5Password(newPassword,2));

		userService.saveOrUpdate(user);

		return ResultUtil.ok();
	}
}
