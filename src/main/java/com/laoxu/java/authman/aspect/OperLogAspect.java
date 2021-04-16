
package com.laoxu.java.authman.aspect;

import com.alibaba.fastjson.JSON;
import com.feilong.lib.javassist.*;
import com.feilong.lib.javassist.bytecode.CodeAttribute;
import com.feilong.lib.javassist.bytecode.LocalVariableAttribute;
import com.feilong.lib.javassist.bytecode.MethodInfo;
import com.laoxu.java.authman.annotation.OperLog;
import com.laoxu.java.authman.common.IpUtil;
import com.laoxu.java.authman.model.SysOperLog;
import com.laoxu.java.authman.model.SysUser;
import com.laoxu.java.authman.service.SysOperLogService;
import com.laoxu.java.authman.util.DateUtil;
import com.sun.xml.internal.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class OperLogAspect {
    @Autowired
    private SysOperLogService logService;


    /**
     * 操作日志切入点
     */
    @Pointcut("@annotation(com.laoxu.java.authman.annotation.OperLog)")
    public void operLogPoinCut() {
    }

    @Around("operLogPoinCut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();

        String reqMethod = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        Object[] args = pjp.getArgs();
        String params = "";
        // 获取操作
        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        // 获取切入点所在的方法
        Method callMethod = signature.getMethod();
        OperLog opLog = callMethod.getAnnotation(OperLog.class);

        SysOperLog operlog = new SysOperLog();
        if (opLog != null) {
            String operModule = opLog.operModule();
            String operType = opLog.operType();
            String operDesc = opLog.operDesc();
            operlog.setOperModule(operModule); // 操作模块
            operlog.setOperType(operType); // 操作类型
            operlog.setOperDesc(operDesc); // 操作描述
        }

        // 获取请求的类名
        String className = pjp.getTarget().getClass().getName();
        // 获取调用的方法名
        String methodName = callMethod.getName();

        operlog.setOperMethod(className + "." + methodName); // 调用的方法
        operlog.setReqMethod(reqMethod);
        operlog.setOperIp(IpUtil.getRemoteIp(request)); // 请求IP
        operlog.setOperUri(uri); // 请求URI
        SysUser user = (SysUser) request.getSession().getAttribute("userSession");
        operlog.setOperUser(user.getUsername()); // 操作用户
        operlog.setCreateTime(DateUtil.getStringDateTime(new Date())); // 操作时间
        // 获取方法参数
        String[] paramNames = getFieldsName(className, methodName);
        Class clazz;
        //获取请求参数集合并进行遍历拼接
        if(args.length>0){
            if("POST".equals(reqMethod)){
                Object object=null;
                for (int i = 0; i < args.length; i++) {
                    object = args[i];
                    clazz = (Class) object.getClass();
                    if(object!=null){
                        // 如果是基本类型直接拼接
                        if(isPrimite(clazz)){
                            params += "{"+paramNames[i]+":"+object+"}\n";
                        }
                        // 数组类型转字符串再拼接
                        else if(object instanceof Integer[]){
                            params += "{"+paramNames[i]+":"+Arrays.toString((Integer[]) object)+"}\n";
                        }
                        // 对象类型转String再拼接
                        else{
                            params += object.toString();
                        }
                    }
                }
            }else if("GET".equals(reqMethod)){
                params = queryString;
            }
        }
        operlog.setOperParam(params);
        log.info("当前的记录是:" + methodName + params);
        logService.save(operlog);

        Object result = pjp.proceed();

        return result;
    }

    private String[] getFieldsName(String class_name, String method_name) throws Exception {
        Class<?> clazz = Class.forName(class_name);
        String clazz_name = clazz.getName();
        ClassPool pool = ClassPool.getDefault();
        ClassClassPath classPath = new ClassClassPath(clazz);
        pool.insertClassPath(classPath);

        CtClass ctClass = pool.get(clazz_name);
        CtMethod ctMethod = ctClass.getDeclaredMethod(method_name);
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if(attr == null){
            return null;
        }
        String[] paramsArgsName = new String[ctMethod.getParameterTypes().length];
        int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
        for (int i=0;i<paramsArgsName.length;i++){
            paramsArgsName[i] = attr.variableName(i + pos);
        }
        return paramsArgsName;
    }

    /**
     * 判断是否为基本类型：包括String
     * @param  obj
     * @return  true：是;     false：不是
     */
    private boolean isPrimite(Class clazz){
        if (clazz.isPrimitive() || clazz == String.class || clazz == Integer.class){
            return true;
        }else {
            return false;
        }
    }

    public static Map getKeyAndValue(Object obj) {
        Map map = new HashMap<>();
        // 得到类对象
        Class userCla = (Class) obj.getClass();
        /* 得到类中的所有属性集合 */
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true); // 设置些属性是可以访问的
            Object val = new Object();
            try {
                val = f.get(obj);
                // 得到此属性的值
                map.put(f.getName(), val);// 设置键值
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return map;
    }


    /**
     * 转换request 请求参数
     *
     * @param paramMap request获取的参数数组
     */

    public Map<String, String> converMap(Map<String, String[]> paramMap) {
        Map<String, String> rtnMap = new HashMap<String, String>();
        for (String key : paramMap.keySet()) {
            rtnMap.put(key, paramMap.get(key)[0]);
        }
        return rtnMap;
    }
}


