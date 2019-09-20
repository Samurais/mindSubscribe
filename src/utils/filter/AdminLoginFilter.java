package utils.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import baen.Admin;
import servlet.admin.AdminLoginServlet;
import servlet.admin.AdminServlet;

public class AdminLoginFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;

		HttpServletResponse response = (HttpServletResponse) res;

		HttpSession session = request.getSession();

		String path = request.getRequestURI();

		if (path.endsWith("/admin/login")) {
			// 过滤器链
			chain.doFilter(req, res);
			return;
		}
		

		Admin admin = (Admin) session.getAttribute(AdminLoginServlet.LOGIN_ADMIN);

		if (admin == null) {

			// 未登录
			// 重定向到登录页面
			response.sendRedirect(request.getContextPath() + "/admin/login");
			return;
		}

		// 过滤器链
		chain.doFilter(req, res);

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}