/**
 * 
 */
package cn.himma.design.creation;

/**
 * @Desc <2>抽象工厂模式（Abstract Factory）
 * @author wewenge.yan
 * @Date 2016年8月23日
 * @ClassName AbstractFactory
 */
public class AbstractFactory {
	public static void main(String[] args) {
		new CatProvider().produce().yell();
		new DogProvider().produce().yell();
	}
}

interface Provider {
	Animal produce();
}

class CatProvider implements Provider {
	@Override
	public Animal produce() {
		return new Cat();
	}

}

class DogProvider implements Provider {
	@Override
	public Animal produce() {
		return new Dog();
	}

}
