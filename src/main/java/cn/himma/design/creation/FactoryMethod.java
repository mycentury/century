/**
 * 
 */
package cn.himma.design.creation;

/**
 * @Desc <1>工厂方法模式（Factory Method）
 * @author wewenge.yan
 * @Date 2016年8月23日
 * @ClassName FactoryMethod
 */
public class FactoryMethod {
	public Animal produce(String type) {
		if ("cat".equals(type)) {
			return new Cat();
		} else if ("dog".equals(type)) {
			return new Dog();
		} else {
			System.out.println("请输入正确的类型!");
			return null;
		}
	}

	public Cat produceCat() {
		return new Cat();
	}

	public Dog produceDog() {
		return new Dog();
	}

	public static void main(String[] args) {
		FactoryMethod fm = new FactoryMethod();
		fm.produce("dog").yell();
		fm.produce("cat").yell();
	}
}

interface Animal {
	void yell();
}

class Cat implements Animal {
	@Override
	public void yell() {
		System.out.println("喵！喵！喵！");
	}
}

class Dog implements Animal {
	@Override
	public void yell() {
		System.out.println("汪！汪！汪！");
	}
}