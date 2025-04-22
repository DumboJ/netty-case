package cn.dumboj.netty.sclass.v3.async;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Netty 中的 Future 实现
 * */
public class NettyFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //DefaultEventLoop 单线程实现
        DefaultEventLoop defaultEventLoop = new DefaultEventLoop();
        Future<Integer> submit = defaultEventLoop.submit(
                (Callable<Integer>) () -> {
                    return -1;
                }
        );
        submit.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                System.out.println(future.get());
            }
        });
    }
}
