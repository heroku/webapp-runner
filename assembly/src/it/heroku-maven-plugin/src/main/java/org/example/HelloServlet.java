package org.example;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "HelloServlet", urlPatterns = {"/hello"})
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        int count = 0;
        if(req.getSession().getAttribute("count") != null) {
            count = (Integer) req.getSession().getAttribute("count");
        }
        count++;
        out.write("hello, world: ".getBytes());
        out.write(req.getSession().getClass().toString().getBytes());
        out.write((" (count: " + count +")").getBytes());
        req.getSession().setAttribute("count", count);
        out.flush();
        out.close();
    }

}
