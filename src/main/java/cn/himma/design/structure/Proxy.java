/**
 * 
 */
package cn.himma.design.structure;

/**
 * @Desc <8>代理模式（Proxy）代理模式关注于控制对对象的访问。由于权限等问题的限制 ，功能可能不全面。
 * @example 比如代理帮商家（被代理）卖商品，客户不给钱则不能卖。商品库存完了，也不能卖。
 * @author wewenge.yan
 * @Date 2016年8月23日
 * @ClassName Proxy
 */
public class Proxy implements Sourceable {

	private Sourceable source;

	/**
	 * @param source
	 */
	public Proxy() {
		super();
		this.source = new SourceA();
	}

	@Override
	public void method1() {
		if (source == null) {
			System.out.println("不处理");
		}
	}

	@Override
	public void method2() {
		System.out.println("不处理");
	}

}
