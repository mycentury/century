/**
 * 
 */
package cn.himma.design.structure;

/**
 * @Desc <7>装饰者模式（Decorator），关注于动态地为对象【增添】一些新的功能，原功能必不可少，比如IO流嵌套
 * @author wewenge.yan
 * @Date 2016年8月23日
 * @ClassName Decorator
 */
public class Decorator implements Sourceable {
	private Sourceable source;

	public Decorator(Sourceable source) {
		super();
		this.source = source;
	}

	@Override
	public void method1() {
		System.out.println("before decorator!");
		source.method1();
		System.out.println("after decorator!");
	}

	@Override
	public void method2() {
		System.out.println("before decorator!");
		source.method2();
		System.out.println("after decorator!");
	}

	public static void main(String[] args) {
		Sourceable source = new SourceA();
		Decorator decorator = new Decorator(source);
		decorator.method1();
	}
}

class SourceA implements Sourceable {
	@Override
	public void method1() {
		System.out.println("A 的行为 1");
	}

	@Override
	public void method2() {
		System.out.println("A 的行为 2");
	}
}

class SourceB implements Sourceable {
	@Override
	public void method1() {
		System.out.println("B 的行为 1");
	}

	@Override
	public void method2() {
		System.out.println("B 的行为 2");
	}
}
