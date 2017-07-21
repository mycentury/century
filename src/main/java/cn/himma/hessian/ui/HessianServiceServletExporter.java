package cn.himma.hessian.ui;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.remoting.caucho.HessianServiceExporter;

public class HessianServiceServletExporter extends HessianServiceExporter {
	private Boolean registerTraceInterceptor;
	private Object[] interceptors;

	@Override
	public void setRegisterTraceInterceptor(boolean registerTraceInterceptor) {
		this.registerTraceInterceptor = Boolean
				.valueOf(registerTraceInterceptor);
	}

	@Override
	public void setInterceptors(Object[] interceptors) {
		this.interceptors = interceptors;
	}

	@Override
	protected Object getProxyForService() {
		checkService();
		checkServiceInterface();
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.addInterface(getServiceInterface());
		if (this.registerTraceInterceptor != null ? this.registerTraceInterceptor
				.booleanValue() : this.interceptors == null) {
			proxyFactory.addAdvice(new HessianRemoteInterceptor());
		}
		if (this.interceptors != null) {
			AdvisorAdapterRegistry adapterRegistry = GlobalAdvisorAdapterRegistry
					.getInstance();
			for (int i = 0; i < this.interceptors.length; i++) {
				proxyFactory.addAdvisor(adapterRegistry
						.wrap(this.interceptors[i]));
			}
		}
		proxyFactory.setTarget(getService());
		proxyFactory.setOpaque(true);
		return proxyFactory.getProxy(getBeanClassLoader());
	}
}