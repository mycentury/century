package cn.himma.hessian.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.hubs1.base.hessian.domain.HmethodDomain;
import net.hubs1.base.hessian.domain.HparamDomain;
import net.hubs1.base.hessian.domain.HserviceDomain;
import net.hubs1.base.hessian.domain.VisitDomain;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.DispatcherServlet;

public class HessianServiceServlet extends DispatcherServlet {
	private static final long serialVersionUID = -6115220116808169703L;
	public static Map<String, VisitDomain> mvisits = new HashMap<String, VisitDomain>();

	public static List<HserviceDomain> hservices = new ArrayList<HserviceDomain>();

	public static HserviceDomain getHserviceDomainByName(String name) {
		if (!StringUtils.hasText(name)) {
			return null;
		}
		for (HserviceDomain hd : hservices) {
			if (name.equals(hd.getName())) {
				return hd;
			}
		}
		return null;
	}

	@Override
	protected void onRefresh(ApplicationContext context) {
		super.onRefresh(context);
		String[] bnames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
				context, Object.class);
		if (null != bnames)
			for (String bname : bnames)
				if ((context.getBean(bname) instanceof HessianServiceServletExporter)) {
					HessianServiceServletExporter hservice = (HessianServiceServletExporter) context
							.getBean(bname);
					Class<?> apiClass = hservice.getServiceInterface();
					Method[] methods = apiClass.getDeclaredMethods();
					List<HmethodDomain> hmethods = new ArrayList<HmethodDomain>();
					for (Method method : methods) {
						Class<?> returnClass = method.getReturnType();
						Class<?>[] params = method.getParameterTypes();
						List<HparamDomain> hparams = new ArrayList<HparamDomain>();
						int index = 0;
						for (Class<?> param : params) {
							HparamDomain hparam = new HparamDomain();
							hparam.setName("arg" + index);
							hparam.setTname(param.getName());
							hparam.setStname(param.getSimpleName());
							hparams.add(hparam);
							index++;
						}
						HmethodDomain hmethod = new HmethodDomain();
						hmethod.setName(method.getName());
						hmethod.setRname(returnClass.getName());
						hmethod.setSrname(returnClass.getSimpleName());
						hmethod.setHparams(hparams);
						hmethods.add(hmethod);
					}
					HserviceDomain hsdomain = new HserviceDomain();
					hsdomain.setUrl(bname);
					hsdomain.setName(apiClass.getName());
					hsdomain.setShortname(apiClass.getSimpleName());
					hsdomain.setHmethods(hmethods);
					hservices.add(hsdomain);
				}
	}

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String method = request.getMethod();
		if (method.equalsIgnoreCase(RequestMethod.PATCH.name())) {
			super.processRequest(request, response);
		} else if (method.equalsIgnoreCase(RequestMethod.GET.name())) {
			int index = request.getRequestURI().indexOf("count");
			String servletName = "http://" + request.getServerName() + ":"
					+ request.getServerPort() + request.getContextPath() + "/"
					+ getServletName();
			String title = "Hessian服务接口";
			String paction = request.getParameter("action");
			paction = paction == null ? "" : paction.trim();
			String pname = request.getParameter("name");
			pname = pname == null ? "" : pname.trim();
			StringBuffer html = new StringBuffer();
			html.append("<!DOCTYPE html>")
					.append("<html><head>")
					.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />")
					.append("<title>")
					.append(title)
					.append("</title>")
					.append("<style type='text/css'>table,table td,table th{margin: 4px 8px;padding: 4px 8px;border: 1px solid #BABABA;border-collapse: collapse;}")
					.append("table td{font-size: 15px;}table th{font-size: 16px;}</style></head><body style='background-color: #ddd;'>");

			html.append("<div style='margin: 24px; padding: 18px; background-color: #fff; border: 1px solid #ccc; border-radius: 4px;'>");
			if (index >= 0) {
				int cleanIndex = request.getRequestURI().indexOf("clean");
				if (cleanIndex >= 0) {
					mvisits = new HashMap<String, VisitDomain>();
					html.append("<div style=\"font-size: 20px;margin: 20px;\">清空成功！</div>");
					html.append("<script type=\"text/javascript\">window.location.href='../count';</script>");
				} else {
					html.append("<div style='margin: 12px 0px; text-align: center;'>");
					html.append("<a href=\""
							+ servletName
							+ "/\" style=\"border: 1px solid #BABABA; background-color: #FFFFFF;padding: 12px 28px; color: #000000;border-radius: 3px;text-decoration: none;font-size: 20px;\">Hessian服务接口</a>");
					html.append("<a href=\""
							+ servletName
							+ "/count\" style=\"margin-left: 28px;border: 1px solid #169fe6; background-color: #169fe6;padding: 12px 28px; color: #FFFFFF;border-radius: 3px;text-decoration: none;font-size: 20px;\">服务访问统计</a>");
					html.append("</div>");

					html.append("<div style='font-size: 24px; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid #ccc;'>服务访问统计</div>");
					html.append("<table>");
					html.append("<tr>").append("<th>").append("类名")
							.append("</th>").append("<th>").append("方法名")
							.append("</th>").append("<th>").append("访问次数")
							.append("</th>").append("<th>").append("平均响应时间")
							.append("</th>").append("<th>").append("最后访问时间")
							.append("</th>").append("</tr>");

					List<VisitDomain> visits = new ArrayList<VisitDomain>();
					Iterator<Entry<String, VisitDomain>> datas = mvisits
							.entrySet().iterator();
					Map.Entry<String, VisitDomain> entry;
					while (datas.hasNext()) {
						entry = datas.next();
						VisitDomain vd = entry.getValue();
						visits.add(vd);
					}
					Collections.sort(visits, new Comparator<VisitDomain>() {
						@Override
						public int compare(VisitDomain o1, VisitDomain o2) {
							return o1.getClassName().compareTo(
									o2.getClassName());
						}
					});
					for (VisitDomain vd : visits) {
						html.append("<tr>")
								.append("<td>")
								.append(vd.getClassName())
								.append("</td>")
								.append("<td>")
								.append(vd.getMethodName())
								.append("</td>")
								.append("<td>")
								.append(vd.getTotalTimes())
								.append("</td>")
								.append("<td>")
								.append(vd.getAvgTime() + "ms")
								.append("</td>")
								.append("<td>")
								.append(new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss").format(vd
										.getLastTime())).append("</td>")
								.append("</tr>");
					}

					html.append("</table>");
					html.append("<div style=\"margin: 28px 0px 18px 8px\"><a href=\"./count/clean\" style=\"border: 1px solid #BABABA; background-color: #FFFFFF;padding: 8px 24px; color: #000000;border-radius: 3px;text-decoration: none;font-size: 18px;\">清空</a></div>");
					html.append("</div>");
				}
			} else {
				html.append("<div style='margin: 12px 0px; text-align: center;'>");
				html.append("<a href=\""
						+ servletName
						+ "/\" style=\"border: 1px solid #169fe6; background-color: #169fe6;padding: 12px 28px; color: #FFFFFF;border-radius: 3px;text-decoration: none;font-size: 20px;\">Hessian服务接口</a>");
				html.append("<a href=\""
						+ servletName
						+ "/count\" style=\"margin-left: 28px;border: 1px solid #BABABA; background-color: #FFFFFF;padding: 12px 28px; color: #000000;border-radius: 3px;text-decoration: none;font-size: 20px;\">服务访问统计</a>");
				html.append("</div>");
				html.append(
						"<div style='margin: 24px; padding: 18px; background-color: #fff; border: 1px solid #ccc; border-radius: 4px;'>")
						.append("<div style='font-size: 24px; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid #ccc;'>")
						.append(title).append("</div>");
				if (hservices.size() >= 1)
					for (int i = 0; i < hservices.size(); i++) {
						HserviceDomain hservice = hservices.get(i);
						if (pname.equals(hservice.getName())) {
							if ("1".equals(paction))
								hservice.setIsused(true);
							else if ("0".equals(paction)) {
								hservice.setIsused(false);
							}
						}
						String action = "1";
						String text = "启用";
						if (hservice.isIsused()) {
							text = "暂停";
							action = "0";
						}
						html.append("<div style='margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px dashed #ccc;'>");
						html.append(
								"<div style='border-radius: 4px;margin-bottom: 12px; padding: 10px 20px; background-color: #169fe6;color: #fff;font-size: 18px;'><span>服务接口：</span>")
								.append(hservice.getName())
								.append("<input type=\"button\" id=\"")
								.append(hservice.getName())
								.append("\" value=\"")
								.append(text)
								.append("\" onclick=\"window.location.href='")
								.append(request.getRequestURI())
								.append("?action=")
								.append(action)
								.append("&name=")
								.append(hservice.getName())
								.append("'\" style=\"margin-left: 18px; padding: 5px 18px; font-size: 16px;\" />")
								.append("</div>");
						html.append("<div>");
						List<HmethodDomain> hmethods = hservice.getHmethods();
						html.append("<table>");
						html.append("<tr>").append("<th>").append("方法名")
								.append("</th>").append("<th>").append("返回类型")
								.append("</td>").append("<th>").append("参数")
								.append("</td>").append("</tr>");
						for (int j = 0; j < hmethods.size(); j++) {
							HmethodDomain hmethod = hmethods.get(j);
							html.append("<tr>");
							html.append("<td valign='top'><p>")
									.append(hmethod.getName())
									.append("</p></td>");
							html.append("<td valign='top'><p>")
									.append(hmethod.getRname())
									.append("</p></td>");
							html.append("<td>");
							List<HparamDomain> hparams = hmethod.getHparams();
							for (int k = 0; k < hparams.size(); k++) {
								HparamDomain hparam = hparams.get(k);
								html.append("<p>").append(hparam.getName())
										.append(":").append(hparam.getTname())
										.append("</p>");
							}
							html.append("</td>").append("</tr>");
						}
						html.append("</table></div>");
						html.append(
								"<div style='border-radius: 4px;margin-top: 12px; padding: 8px 20px; background-color: #eee;font-size: 14px;'><span>服务地址：</span>")
								.append("<a href=\"").append(servletName)
								.append(hservice.getUrl())
								.append("\" style='color: #0000ff'>")
								.append(servletName).append(hservice.getUrl())
								.append("</a></div>");
						html.append("</div>");
					}
				else {
					html.append("<p>Hessian没有对外提供服务接口</p>");
				}
				html.append("</div>");
			}
			html.append("</body></html>");
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print(html.toString());
			out.close();
		} else {
			super.service(request, response);
		}
	}
}
