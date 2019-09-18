package servlet.admin;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.json.JSONUtil;

import baen.Admin;
import model.service.AdminService;

/**
 * Servlet implementation class AdminLoginServlet
 * 管理员登录，修改密码，修改个人信息
 */
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final String LOGIN_ADMIN = "LOGIN_ADMIN";

	AdminService adminService = new AdminService();
	
	
	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String m = request.getParameter("m");
		
		if("saveLogin".equals(m)) {
			
			String adminName = request.getParameter("adminName");
			
			String adminPwd = request.getParameter("adminPwd");
			
			//向数据库查询管理员
			Admin admin = adminService.getAdmin(adminName);
			
			if(admin == null || !(admin.getAdminPwd().equals(adminPwd))) {
				
				//登录失败
				
				request.setAttribute("msg", "用户名或密码错误!");
				
				request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
				
				
			}else { 
				
				//登录成功
				
				request.getSession().setAttribute(LOGIN_ADMIN,admin);
				
				response.sendRedirect(request.getContextPath() + "/admin/index.jsp");
				
			}
			
			
		}else if("updatePwd".equals(m)){
			
			//修改密码
			
			String newPwd = request.getParameter("newPwd");
			
			String password = request.getParameter("password");
			
			Admin admin = (Admin)request.getSession().getAttribute(LOGIN_ADMIN);
			
			
			String msg = "";
			if( admin.getAdminPwd().equals(password) ) {
				
				//成功
				adminService.updateAdminPwd(admin.getAdminId(),newPwd);
				
				msg = "{\"result\":\"true\",\"msg\":\"修改成功,请重新登录！\"}";
				
				request.getSession().removeAttribute(LOGIN_ADMIN);
				
				
			}else if(!admin.getAdminPwd().equals(password)){
				msg = "{\"result\":\"false\",\"msg\":\"修改失败，原始密码错误！\"}";
			}else {
				msg = "{\"result\":\"false\",\"msg\":\"修改失败，请重试！\"}";
			}
			
			
			response.setContentType("application/json; charset=utf-8");
			
			Writer writer = response.getWriter();
			
			writer.write(msg);
			
			writer.close();
			
			
		}else if("updateBase".equals(m)){
			
			//修改基本信息
			
			Admin admin = new Admin();
			admin.setName(request.getParameter("name"));
			admin.setSex( Integer.parseInt( request.getParameter("sex")));
			admin.setAge(Integer.parseInt(request.getParameter("age")));
			admin.setPhone(request.getParameter("phone"));
			admin.setEmail(request.getParameter("email"));
			
			//当前登录用户
			Admin adminNow = (Admin)request.getSession().getAttribute(LOGIN_ADMIN);
			
			
			//更新
			int i = adminService.updateAdminBase(admin,adminNow.getAdminId());
			
			Writer writer = response.getWriter();
			
			String msg = "";
			if(i > 0) {
				//成功
				msg = "{\"result\":\"true\",\"msg\":\"修改成功,请刷新！\"}";
				
				//重新在查询一遍管理员信息
				Admin adminNew = adminService.getAdmin(adminNow.getAdminName());
				
				request.getSession().setAttribute(LOGIN_ADMIN,adminNew);
				
			}else {
				//失败
				msg = "{\"result\":\"false\",\"msg\":\"修改失败，请刷新页面后重试\"}";
			}
			writer.write(msg);
			writer.close();
		
			
			
			
			
		}else {
			
			request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
		}
		
	
	}

}
