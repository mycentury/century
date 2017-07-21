/**
 * 
 */
package cn.himma.design.creation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @Desc <5>原型模式（Prototype），就是克隆。
 * @question 为什么要实现空接口？答：空接口相当于一个标识，在native（底层实现）方法中会判断是否实现，用于规范代码
 * @author wewenge.yan
 * @Date 2016年8月23日
 * @ClassName Prototype
 */
public class Prototype implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Prototype clone() {
		Prototype copy = null;
		try {
			copy = (Prototype) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return copy;
	}

	public Prototype deepClone() {
		Prototype copy = null;
		try {
			/* 写入当前对象的二进制流 */
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(this);

			/* 读出二进制流产生的新对象 */
			ByteArrayInputStream bis = new ByteArrayInputStream(
					bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);
			copy = (Prototype) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return copy;
	}
}
