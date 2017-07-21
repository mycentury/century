/**
 * 
 */
package cn.himma.design.behavior;

/**
 * @Desc <14>模板方法模式（Template Method）继承抽象类，重写抽象方法，通过调用抽象类，实现对子类的调用，
 * @example 比如重写toString()，对print的影响
 * @author wewenge.yan
 * @Date 2016年8月24日
 * @ClassName TemplateMethod
 */
public class TemplateMethod {
	public static void main(String[] args) {
		BaseService server = new ServiceA();
		server.serve();
	}
}

abstract class BaseService {
	public void serve() {
		prepare();
		coreServe();
		close();
	}

	protected void coreServe() {
		System.out.println("核心服务");
	}

	protected void close() {
		System.out.println("关闭");
	}

	protected abstract void prepare();
}

class ServiceA extends BaseService {
	@Override
	protected void prepare() {
		System.out.println("准备");
	}
}