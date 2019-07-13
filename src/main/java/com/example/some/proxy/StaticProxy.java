package com.example.some.proxy;

/**
 * 静态代理类
 */
public class StaticProxy implements  Singer {
    private  Singer target;
    public StaticProxy(Singer target){
        this.target=target;
    }
    @Override
    public void sing() {
        System.out.println("代理内容在接口调用之前执行");
        target.sing();
        System.out.println("代理内容在接口调用之后执行");
    }

    @Override
    public void speak() {
        System.out.println("代理内容在接口调用之前执行");
        target.sing();
        System.out.println("代理内容在接口调用之后执行");
    }

    public static void main(String[] args) {
        StaticProxy proxy=new StaticProxy(new DehuaSinger());
        proxy.sing();
    }
}
