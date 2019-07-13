package com.example.some.proxy;

public class DehuaSinger implements  Singer {
    @Override
    public void sing() {
        System.out.println("实际接口方法执行---唱歌");
    }

    @Override
    public void speak() {
        System.out.println("实际接口方法执行---说话");
    }
}
