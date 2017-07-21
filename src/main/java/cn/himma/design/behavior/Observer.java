/**
 * 
 */
package cn.himma.design.behavior;

import java.util.Enumeration;
import java.util.Vector;

/**
 * @Desc <15>观察者模式（Observer）当一个对象变化时，其它依赖该对象的对象都会收到通知，并且随着变化！对象之间是一种一对多的关系
 * @example 订阅收到通知
 * @author wewenge.yan
 * @Date 2016年8月24日
 * @ClassName Observer
 */
public class Observer {
	public static void main(String[] args) {
		Subject subject = new SubjectA();
		ObserverA observerA = new ObserverA();
		ObserverB observerB = new ObserverB();

		subject.add(observerA);
		subject.add(observerB);

		subject.operation();
		subject.del(observerB);

		subject.operation();
	}
}

interface IObserver {
	void receiveMsg(String msg);
}

class ObserverA implements IObserver {
	@Override
	public void receiveMsg(String msg) {
		System.out.println("A 收到消息:" + msg);
	}
}

class ObserverB implements IObserver {
	@Override
	public void receiveMsg(String msg) {
		System.out.println("B 收到消息:" + msg);
	}
}

interface Subject {

	/* 增加观察者 */
	public void add(IObserver observer);

	/* 删除观察者 */
	public void del(IObserver observer);

	/* 通知所有的观察者 */
	public void notifyObservers(String msg);

	/* 自身的操作 */
	public void operation();

	/* 自身的操作 */
	public void onOperation(String msg);
}

abstract class AbstractSubject implements Subject {

	private Vector<IObserver> vector = new Vector<IObserver>();

	@Override
	public void add(IObserver observer) {
		vector.add(observer);
	}

	@Override
	public void del(IObserver observer) {
		vector.remove(observer);
	}

	@Override
	public void notifyObservers(String msg) {
		Enumeration<IObserver> enumo = vector.elements();
		while (enumo.hasMoreElements()) {
			enumo.nextElement().receiveMsg(msg);
		}
	}

	@Override
	public void onOperation(String msg) {
		notifyObservers(msg);
	}
}

class SubjectA extends AbstractSubject {
	@Override
	public void operation() {
		System.out.println("发生改变");
		onOperation("改变通知");
	}
}