package cn.dumboj.netty.v3.async;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.TimeUnit;

/**
 * Netty Promise 的实现内容
 * */
public class NettyPromise {
    public static void main(String[] args) {
        //此处可以看出 EventLoop 为 Executor 实现
        EventLoop next = new DefaultEventLoop().next();
        DefaultPromise<Integer> promise = new DefaultPromise<>(next);
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Promise 对象同时处理状态及结果
            promise.setSuccess(10);
        });
        promise.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                System.out.println(promise.get());
            }
        });
    }
}
