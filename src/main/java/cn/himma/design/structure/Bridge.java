/**
 * 
 */
package cn.himma.design.structure;

/**
 * @Desc <10>桥接模式（Bridge）
 * @author wewenge.yan
 * @Date 2016年8月23日
 * @ClassName Bridge
 */
public class Bridge {
	public static void main(String[] args) {
		AbstractBridge bridge = new BridgeForSource();

		bridge.setSource(new SourceA());
		bridge.method();

		bridge.setSource(new SourceB());
		bridge.method();
	}
}

abstract class AbstractBridge {
	private Sourceable source;

	public void method() {
		source.method1();
	}

	public Sourceable getSource() {
		return source;
	}

	public void setSource(Sourceable source) {
		this.source = source;
	}
}

class BridgeForSource extends AbstractBridge {

	@Override
	public void method() {
		getSource().method2();
	}

}