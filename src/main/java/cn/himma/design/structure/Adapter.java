/**
 * 
 */
package cn.himma.design.structure;

/**
 * @Desc <6>适配器模式（Adapter），可将继承source改为依赖，继承也叫类适配，依赖也叫对象适配，接口抽象类也叫接口适配
 * @author wewenge.yan
 * @Date 2016年8月23日
 * @ClassName Adapter
 */
public class Adapter extends Source implements Targetable {
	@Override
	public void method2() {
		System.out.println("this is the targetable method!");
	}
}

class Source {

	public void method1() {
		System.out.println("this is original method!");
	}
}

interface Targetable {

	/* 与原类中的方法相同 */
	public void method1();

	/* 新类的方法 */
	public void method2();
}

interface Sourceable {

	/* 与原类中的方法相同 */
	public void method1();

	/* 新类的方法 */
	public void method2();
}

abstract class Wrapper implements Sourceable {
	@Override
	public void method1() {
	}

	@Override
	public void method2() {
	}
}

class WrapperA extends Wrapper {
	@Override
	public void method1() {
		System.out.println("重写方法1");
	}
}

class WrapperB extends Wrapper {
	@Override
	public void method2() {
		System.out.println("重写方法2");
	}
}