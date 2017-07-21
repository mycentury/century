/**
 * 
 */
package cn.himma.design.behavior;

import java.util.HashMap;
import java.util.Map;

/**
 * @Desc <13>策略模式（strategy）自己理解为多种算法实现分开，分别对应一种策略，由一个统一入口管理
 * @skill 结合spring管理bean，扩展算法时，其他代码都不需要改变
 * @author wewenge.yan
 * @Date 2016年8月23日
 * @ClassName Strategy
 */
public class Strategy {
	public static void main(String[] args) {
		live("farmer");
		live("business");
	}

	/**
	 * 
	 */
	private static void live(String role) {
		Map<String, ILife> map = new HashMap<String, ILife>();
		map.put("farmer", new Farmer());
		map.put("business", new Merchant());
		map.get(role).live();
	}
}

interface ILife {
	void live();
}

class Merchant implements ILife {
	@Override
	public void live() {
		System.out.println("经商");
	}
}

class Farmer implements ILife {
	@Override
	public void live() {
		System.out.println("务农");
	}
}