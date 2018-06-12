package com.tim.config.aop;

import com.alibaba.fastjson.JSON;
import com.tim.service.user.ISysUserService;
import com.tim.syslog.SysControllerLog;
import com.tim.syslog.SysServiceLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @description: AOP统一处理WEB请求日志
 * @auther: lizhiming
 * @date: 2018/4/20 15:53
 */
@Aspect
@Component
@Slf4j
public class WebLogAspect {

    @Autowired
    private ISysUserService sysUserService;

    //Controller层切点
    @Pointcut("@annotation(com.tim.syslog.SysControllerLog)")
    public void controllerAspect(){
    }

    //Service层切点
    @Pointcut("@annotation(com.tim.syslog.SysServiceLog)")
    public void serviceAspect(){
    }

    /**
     * 前置通知 用于拦截Controller层记录用户的操作
     * @param point
     */
    @Before("controllerAspect()")
    public void doBefore(JoinPoint point) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        long beginTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) point.getSignature();
        //获取被拦截的方法
        Method method = signature.getMethod();
        //获取被拦截的方法名
        String methodName = method.getName();
        //获取请求ip
        String ip = request.getRemoteAddr();

        //获取用户请求方法的参数并序列化为JSON格式字符串
        String params = "";
        if (point.getArgs() != null && point.getArgs().length > 0) {
            for (int i = 0; i < point.getArgs().length; i++) {
                params += JSON.toJSONString(point.getArgs()[i]) + ";";
            }
        }
        System.out.println("异常方法:" + (point.getTarget().getClass().getName() + "." + point.getSignature().getName() + "()"));
        System.out.println("请求人: + user.getName()");
        System.out.println("请求IP:" + ip);
        try {
            String desc =  getControllerMethodDescription(point);
            long costMs = System.currentTimeMillis() - beginTime;
            LOGGER.info("【WEB控制器请求日志】切入点请求方法名称：{} ({}) 耗时：{}ms", methodName, desc, costMs);
            LOGGER.info("【WEB控制器请求日志】请求方法参数：" + JSON.toJSONString(params));
        } catch (Exception ex){
            LOGGER.info("【WEB控制器请求日志】获取请求方法描述异常：" + ex.getMessage());
        }
        /*List<User> users = userService.getAllUser();
        System.out.println("users:" + JSON.toJSONString(users));*/
    }

    /**
     * 异常通知 用于拦截service层记录异常日志
     * @param point
     */
    @AfterThrowing(pointcut = "serviceAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint point, Throwable e) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        long beginTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod(); //获取被拦截的方法
        String methodName = method.getName(); //获取被拦截的方法名

        //获取请求ip
        String ip = request.getRemoteAddr();

        //获取用户请求方法的参数并序列化为JSON格式字符串
        String params = "";
        if (point.getArgs() != null && point.getArgs().length > 0) {
            for (int i = 0; i < point.getArgs().length; i++) {
                params += JSON.toJSONString(point.getArgs()[i]) + ";";
            }
        }
        System.out.println("异常代码:" + e.getClass().getName());
        System.out.println("异常信息:" + e.getMessage());
        System.out.println("异常方法:" + (point.getTarget().getClass().getName() + "." + point.getSignature().getName() + "()"));
        System.out.println("请求人: + user.getName()");
        System.out.println("请求IP:" + ip);
        System.out.println("请求参数:" + params);
        try {
            System.out.println("方法描述:" + getServiceMthodDescription(point));
        } catch (Exception ex){
            LOGGER.info("【WEB请求日志】获取请求方法描述异常：" + ex.getMessage());
        }

        long costMs = System.currentTimeMillis() - beginTime;
        LOGGER.info("【WEB请求日志】切入点请求方法名称：{}  耗时：{}ms", methodName, costMs);
        LOGGER.info("【WEB请求日志】请求方法参数：" + JSON.toJSONString(params));
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public static String getControllerMethodDescription(JoinPoint joinPoint) throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(SysControllerLog.class).desc();
                    break;
                }
            }
        }
        return description;
    }

    /**
     * 获取注解中对方法的描述信息 用于service层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public static String getServiceMthodDescription(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(SysServiceLog.class).desc();
                    break;
                }
            }
        }
        return description;
    }
}
