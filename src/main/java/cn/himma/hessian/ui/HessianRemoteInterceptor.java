package cn.himma.hessian.ui;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.util.ClassUtils;

import cn.himma.hessian.ui.domain.HserviceDomain;
import cn.himma.hessian.ui.domain.VisitDomain;

public class HessianRemoteInterceptor implements MethodInterceptor {
	private static final Logger accesslog = Logger
			.getLogger("cn.himma.hessian.accesslog");
	private static final Logger logger = Logger
			.getLogger("cn.himma.hessian.reqreslog");

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Date startTime = new Date();
		Method method = invocation.getMethod();
		String apiName = ClassUtils.getQualifiedMethodName(method);
		accesslog.debug(new StringBuilder().append("remote api: ")
				.append(apiName).toString());
		String request = getRequest(invocation);
		HserviceDomain hd = HessianServiceServlet
				.getHserviceDomainByName(method.getDeclaringClass().getName());
		if ((null != hd) && (hd.isIsused()))
			logger.info(new StringBuilder().append(apiName)
					.append(" request: ").append(request).toString());
		else
			logger.debug(new StringBuilder().append(apiName)
					.append(" request: ").append(request).toString());
		try {
			Object retVal = invocation.proceed();
			String value = "null";
			if (null != retVal) {
				value = retVal.toString();
			}
			if ((null != hd) && (hd.isIsused()))
				logger.info(new StringBuilder().append(apiName)
						.append(" response: ").append(value).toString());
			else {
				logger.debug(new StringBuilder().append(apiName)
						.append(" response: ").append(value).toString());
			}
			saveVisit("", apiName, method.getName(), startTime);
			return retVal;
		} catch (Throwable ex) {
			logger.error(
					new StringBuilder().append("invoke api: ").append(apiName)
							.append(" error. request: ").append(request)
							.toString(), ex);
			throw ex;
		}
	}

	private String getRequest(MethodInvocation invocation) {
		String request = "";
		Object[] args = invocation.getArguments();
		if (null != args) {
			for (Object arg : args) {
				request = new StringBuilder()
						.append(request)
						.append(null == arg ? "null" : new StringBuilder()
								.append(arg.toString()).append(" | ")
								.toString()).toString();
			}
		}
		request = request.trim();
		if (!"".equals(request)) {
			request = request.substring(0, request.length() - 1);
		}
		return request;
	}

	private void saveVisit(String ip, String className, String methodName,
			Date startTime) {
		Date endTime = new Date();
		Long allTime = Long.valueOf(endTime.getTime() - startTime.getTime());
		String key = new StringBuilder().append(ip).append("-")
				.append(className).append("-").append(methodName).toString();
		Map<String, VisitDomain> map = HessianServiceServlet.mvisits;
		if (map.containsKey(key)) {
			VisitDomain vd = map.get(key);
			vd.setLastTime(endTime);
			Long times = Long.valueOf(vd.getTotalTimes().longValue() + 1L);
			Long avgTime = Long.valueOf((vd.getTotalTimes().longValue()
					* vd.getAvgTime().longValue() + allTime.longValue())
					/ times.longValue());
			vd.setTotalTimes(times);
			vd.setAvgTime(avgTime);
			map.put(key, vd);
		} else {
			VisitDomain vd = new VisitDomain();
			vd.setIp(ip);
			vd.setClassName(className);
			vd.setMethodName(methodName);
			vd.setTotalTimes(Long.valueOf(1L));
			vd.setAvgTime(allTime);
			vd.setLastTime(endTime);
			map.put(key, vd);
		}
	}
}
