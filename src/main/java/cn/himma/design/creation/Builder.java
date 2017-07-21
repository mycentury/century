/**
 * 
 */
package cn.himma.design.creation;

import java.util.ArrayList;
import java.util.List;

/**
 * @Desc <4>建造者模式（Builder），创建复合对象，相当于创建多个工厂，同样可像抽象工厂一样实现抽象建造，以便扩展
 * @author wewenge.yan
 * @Date 2016年8月23日
 * @ClassName Builder
 */
public class Builder {
	public List<Cat> produceCats(int count) {
		List<Cat> cats = new ArrayList<Cat>();
		for (int i = 0; i < count; i++) {
			cats.add(new Cat());
		}
		return cats;
	}

	public List<Dog> produceDogs(int count) {
		List<Dog> dogs = new ArrayList<Dog>();
		for (int i = 0; i < count; i++) {
			dogs.add(new Dog());
		}
		return dogs;
	}

	public List<Animal> produceDogsAndCats(int dogsCount, int catsCount) {
		List<Animal> animals = new ArrayList<Animal>();
		for (int i = 0; i < dogsCount; i++) {
			animals.add(new Dog());
		}
		for (int i = 0; i < catsCount; i++) {
			animals.add(new Cat());
		}
		return animals;
	}
}
