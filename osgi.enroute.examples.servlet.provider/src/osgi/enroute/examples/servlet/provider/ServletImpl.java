package osgi.enroute.examples.servlet.provider;


import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(name="osgi.enroute.examples.servlet", property="alias=/hello", service=Servlet.class)
public class ServletImpl extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger log = LoggerFactory.getLogger(ServletImpl.class);


	@Override
	public void doGet(HttpServletRequest rq, HttpServletResponse rsp) throws IOException {
		rsp.getWriter().println("Hello World");
	}
	
}
