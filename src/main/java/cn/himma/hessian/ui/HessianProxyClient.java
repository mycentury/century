package cn.himma.hessian.ui;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.util.ClassUtils;

public class HessianProxyClient extends HessianProxyFactoryBean {
	private static final Logger logger = Logger
			.getLogger("net.hubs1.base.hessian.clientlog");

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String apiName = ClassUtils.getQualifiedMethodName(invocation
				.getMethod());
		String params = getParams(invocation);
		logger.debug(new StringBuilder().append("remote api: ").append(apiName)
				.append(". ").append(params).toString());
		try {
			Object obj = super.invoke(invocation);
			logger.debug(new StringBuilder().append(apiName)
					.append(" response: ")
					.append(null == obj ? "" : obj.toString()).toString());
			return obj;
		} catch (Exception e) {
			logger.error(
					new StringBuilder().append("invoke api: ").append(apiName)
							.append(" error. params: ").append(params)
							.toString(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	private String getParams(MethodInvocation invocation) {
		String params = "";
		Object[] args = invocation.getArguments();
		if (null != args) {
			for (Object arg : args) {
				params = new StringBuilder()
						.append(params)
						.append(null == arg ? "null" : new StringBuilder()
								.append(arg.toString()).append(" | ")
								.toString()).toString();
			}
		}
		params = params.trim();
		if (!"".equals(params)) {
			params = params.substring(0, params.length() - 1);
		}
		return params;
	}
}