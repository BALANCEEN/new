package com.example.some.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * cglib动态代理
 */
public class CglibProxy<T> implements MethodInterceptor {
    private T target;
    public CglibProxy(T target){
        this.target=target;
    }
    public T create(){
        Enhancer enhancer=new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        return (T)enhancer.create();
    }

    /**
     *
     * @param o              代理对象
     * @param method        目标对象方法
     * @param objects       目标对象入参
     * @param methodProxy   方法代理对象
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("代理内容在接口调用之前执行");
        return methodProxy.invokeSuper(o,objects);
    }

    public static void main(String[] args) {
        CglibProxy<DehuaSinger> cglibProxy=new CglibProxy<>(new DehuaSinger());
        DehuaSinger dehuaSinger = cglibProxy.create();
        dehuaSinger.sing();
    }
}
