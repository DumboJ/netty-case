package cn.dumboj.netty.sclass.v6.http;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * J2EE 的 Servlet 封装 响应客户端 API 是更高层次的封装
 * */
public class J2eeServletDemo extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String header = req.getHeader("Content-Type");
        resp.getWriter().write("this is servlet server.");
    }
}
