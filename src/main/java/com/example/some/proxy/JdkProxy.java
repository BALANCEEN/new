package com.example.some.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk动态代理
 * 代理的总用类似于aop，将多次调用，并且与业务代码没有必然联系的代码抽取出来，
 * 可以在业务代码的前后调用，降低代码耦合度，多次利用。
 */
public class JdkProxy<T> implements InvocationHandler {
    private  T target;
    public JdkProxy(T target){
        this.target=target;
    }
    //绑定该类实现的所有接口，去的代理类
    public T create(){
        Object o = Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
        return (T)o;
    }
    /**
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("代理内容在接口调用之前执行");
        return method.invoke(target,args);
    }

    public static void main(String[] args) {
        JdkProxy<Singer> jdkProxy=new JdkProxy<>(new DehuaSinger());
        Singer singer = jdkProxy.create();
        singer.sing();
    }
}
