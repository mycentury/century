/**
 * 
 */
package cn.himma.design.creation;

/**
 * @Desc <3>单例模式（Singleton）考虑线程安全和效率，应该用同步代码块再判断一次
 * @author wewenge.yan
 * @Date 2016年8月23日
 * @ClassName Singleton
 */
public class Singleton {
	private static Singleton instance = null;

	private Singleton() {
	}

	public static Singleton getInstance() {
		if (instance == null) {
			// 锁在类或者静态对象
			synchronized (instance) {
				if (instance == null) {
					instance = new Singleton();
				}
			}
		}
		return instance;
	}
}
