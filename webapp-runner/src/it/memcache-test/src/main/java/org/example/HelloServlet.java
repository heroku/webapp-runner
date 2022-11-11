package org.example;

import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.StandardSessionFacade;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

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

        try {
            StandardSessionFacade session = (StandardSessionFacade) req.getSession();
            Field facadeSessionField = StandardSessionFacade.class.getDeclaredField("session");
            facadeSessionField.setAccessible(true);
            StandardSession stdSession = (StandardSession) facadeSessionField.get(session);
            out.write(stdSession.getClass().toString().getBytes());
        } catch (IllegalAccessException e) {
            throw new ServletException(e);
        } catch (NoSuchFieldException e) {
            throw new ServletException(e);
        }

        out.write((" (count: " + count +")").getBytes());
        req.getSession().setAttribute("count", count);
        out.flush();
        out.close();
    }

}
