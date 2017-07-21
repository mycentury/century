/**
 * 
 */
package cn.himma.design.structure;

/**
 * @Desc <9>外观模式（Facade）降低类与类之间的耦合度，创建一个外观类
 * @author wewenge.yan
 * @Date 2016年8月23日
 * @ClassName Facade
 */
public class Facade {
	public static void main(String[] args) {
		Computer computer = new Computer();
		computer.startup();
		computer.shutdown();
	}
}

class Cpu {

	public void startup() {
		System.out.println("cpu startup!");
	}

	public void shutdown() {
		System.out.println("cpu shutdown!");
	}
}

class Memory {

	public void startup() {
		System.out.println("memory startup!");
	}

	public void shutdown() {
		System.out.println("memory shutdown!");
	}
}

class Disk {

	public void startup() {
		System.out.println("disk startup!");
	}

	public void shutdown() {
		System.out.println("disk shutdown!");
	}
}

class Computer {
	private Cpu cpu;
	private Memory memory;
	private Disk disk;

	public Computer() {
		cpu = new Cpu();
		memory = new Memory();
		disk = new Disk();
	}

	public void startup() {
		System.out.println("start the computer!");
		cpu.startup();
		memory.startup();
		disk.startup();
		System.out.println("start computer finished!");
	}

	public void shutdown() {
		System.out.println("begin to close the computer!");
		cpu.shutdown();
		memory.shutdown();
		disk.shutdown();
		System.out.println("computer closed!");
	}
}