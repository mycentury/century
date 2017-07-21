/**
 * 
 */
package cn.himma.util.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年8月12日
 * @ClassName BufferedRandomAccessFileTest
 */
public class BufferedRandomAccessFileTest {

	/**
     * 
     */
	private static final String NEXT_LINE = "\r\n";

	private String filePath = "D:\\项目读写文件\\高效读写\\BRAF.txt";

	private String filePath1 = "D:\\项目读写文件\\高效读写\\1.log";

	private String filePath2 = "D:\\项目读写文件\\高效读写\\2.log";

	private String filePath3 = "D:\\项目读写文件\\高效读写\\3.log";

	private String filePath4 = "D:\\项目读写文件\\高效读写\\4.log";

	@Test
	public void testReadCompare() {
		try {
			RandomAccessFileExtension rafe = new RandomAccessFileExtension(
					filePath1, "r");
			RandomAccessFile raf = new RandomAccessFile(filePath2, "r");
			String line = null;
			while ((line = raf.readLine()) != null) {
				line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
				String readLineByBuffer = rafe.readLineByBuffer();
				long readLineByBufferPosition = rafe
						.getReadLineByBufferPosition();
				long filePointer = raf.getFilePointer();
				if (!line.equals(readLineByBuffer)
						|| readLineByBufferPosition != filePointer) {
					System.out.println(line);
					System.out.println(readLineByBuffer);
				}
			}
			rafe.close();
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link com.test.BufferedRandomAccessFile#read(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadByteArrayIntInt() throws IOException {
		BufferedRandomAccessFile rafReader = new BufferedRandomAccessFile(
				filePath, "r", 2);
		String readLine = null;
		String string = "中文,，测试1";
		System.out.println(string.length() + ":" + string + ":"
				+ Arrays.toString(string.getBytes()));
		while ((readLine = rafReader.readLineByBuffer()) != null) {
			System.out.println(readLine.length() + ":" + readLine + ":"
					+ Arrays.toString(readLine.getBytes("UTF-8")));
		}
		rafReader.close();
		// System.out.println(new
		// String(rafReader.readLine().getBytes("ISO-8859-1"), "UTF-8"));
	}

	/**
	 * Test method for {@link com.test.BufferedRandomAccessFile#read(byte[])}.
	 */
	@Test
	public void testReadByteArray() {
		long start = System.currentTimeMillis();
		RandomAccessFile rafReader = null;
		byte[] lastLine = null;
		List<String> linesOne = new ArrayList<String>();
		try {
			rafReader = new RandomAccessFile(filePath1, "r");
			// RandomAccessFile rafWriter = new RandomAccessFile(filePath3,
			// "rw");
			byte[] buffer = new byte[1024 * 10];
			int length = 0;
			while ((length = rafReader.read(buffer)) != -1) {
				byte[] temp = Arrays.copyOfRange(buffer, 0, length);
				temp = addTwoArrays(lastLine, temp);
				lastLine = null;
				int lastIndex = 0;
				for (int i = 0; i < temp.length - 1; i++) {
					// 只有\r\n一起
					if (temp[i] == 13 && temp[i + 1] == 10) {
						byte[] tempByte = Arrays
								.copyOfRange(temp, lastIndex, i);
						// System.out.println(new String(tempByte));
						// rafWriter.write(addTwoArrays(tempByte,
						// "\r\n".getBytes()));
						linesOne.add(new String(tempByte));
						lastIndex = i + 2;
					} else if (temp[i] != 13 && temp[i + 1] == 10) {
						byte[] tempByte = Arrays.copyOfRange(temp, lastIndex,
								i + 1);
						// rafWriter.write(addTwoArrays(tempByte,
						// "\n".getBytes()));
						linesOne.add(new String(tempByte));
						lastIndex = i + 2;
					} else if (i + 1 == temp.length - 1) {
						lastLine = Arrays.copyOfRange(temp, lastIndex,
								temp.length);
					}
				}
			}
			if (lastLine != null) {
				linesOne.add(new String(lastLine));
				// rafWriter.write(lastLine);
				lastLine = null;
				// System.out.println(lastLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		long second = System.currentTimeMillis() - start;
		System.out.println("缓冲读耗时：" + second + "毫秒," + linesOne.size() + "行");

		if (rafReader != null) {
			try {
				rafReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		long start2 = System.currentTimeMillis();
		RandomAccessFile rafReader2 = null;
		List<String> linesTwo = new ArrayList<String>();
		try {
			rafReader2 = new RandomAccessFile(filePath2, "r");
			String line = null;
			while ((line = rafReader2.readLine()) != null) {
				line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
				linesTwo.add(line);
				// System.out.println(x);
			}
			if (rafReader2 != null) {
				try {
					rafReader2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		long second2 = System.currentTimeMillis() - start2;
		System.out.println("非缓冲读耗时：" + second2 + "毫秒," + linesTwo.size() + "行,"
				+ second2 / second + "倍！");

		System.out.println(linesOne.size() == linesTwo.size());
		for (int i = 0; i < linesOne.size(); i++) {
			if (!linesOne.get(i).equals(linesTwo.get(i))) {
				System.out.println(Arrays.toString(linesOne.get(i).getBytes())
						+ "\n" + Arrays.toString(linesTwo.get(i).getBytes()));
			}
		}
	}

	/**
	 * Test method for {@link com.test.BufferedRandomAccessFile#read(byte[])}.
	 */
	@Test
	public void testReadByteArray2() {
		long start2 = System.currentTimeMillis();
		RandomAccessFile rafReader2 = null;
		List<String> linesTwo = new ArrayList<String>();
		try {
			rafReader2 = new RandomAccessFile(filePath2, "r");
			String line = null;
			while ((line = rafReader2.readLine()) != null) {
				line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
				linesTwo.add(line);
				// System.out.println(x);
			}
			if (rafReader2 != null) {
				try {
					rafReader2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		long second2 = System.currentTimeMillis() - start2;
		long start = System.currentTimeMillis();
		RandomAccessFile rafReader = null;
		byte[] lastLine = null;
		List<String> linesOne = new ArrayList<String>();
		try {
			rafReader = new RandomAccessFile(filePath1, "r");
			// RandomAccessFile rafWriter = new RandomAccessFile(filePath3,
			// "rw");
			byte[] buffer = new byte[1024 * 10];
			int length = 0;
			while ((length = rafReader.read(buffer)) != -1) {
				byte[] temp = Arrays.copyOfRange(buffer, 0, length);
				temp = addTwoArrays(lastLine, temp);
				lastLine = null;
				int lastIndex = 0;
				for (int i = 0; i < temp.length - 1; i++) {
					// 只有\r\n一起
					if (temp[i] == 13 && temp[i + 1] == 10) {
						byte[] tempByte = Arrays
								.copyOfRange(temp, lastIndex, i);
						// System.out.println(new String(tempByte));
						// rafWriter.write(addTwoArrays(tempByte,
						// "\r\n".getBytes()));
						linesOne.add(new String(tempByte));
						lastIndex = i + 2;
					} else if (temp[i] != 13 && temp[i + 1] == 10) {
						byte[] tempByte = Arrays.copyOfRange(temp, lastIndex,
								i + 1);
						// rafWriter.write(addTwoArrays(tempByte,
						// "\n".getBytes()));
						linesOne.add(new String(tempByte));
						lastIndex = i + 2;
					} else if (i + 1 == temp.length - 1) {
						lastLine = Arrays.copyOfRange(temp, lastIndex,
								temp.length);
					}
				}
			}
			if (lastLine != null) {
				linesOne.add(new String(lastLine));
				// rafWriter.write(lastLine);
				lastLine = null;
				// System.out.println(lastLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		long second = System.currentTimeMillis() - start;
		System.out.println("缓冲读耗时：" + second + "毫秒," + linesOne.size() + "行");

		if (rafReader != null) {
			try {
				rafReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("非缓冲读耗时：" + second2 + "毫秒," + linesTwo.size() + "行,"
				+ second2 / second + "倍！");

		System.out.println(linesOne.size() == linesTwo.size());
		for (int i = 0; i < linesOne.size(); i++) {
			if (!linesOne.get(i).equals(linesTwo.get(i))) {
				System.out.println(Arrays.toString(linesOne.get(i).getBytes())
						+ "\n" + Arrays.toString(linesTwo.get(i).getBytes()));
			}
		}
	}

	@Test
	public void testReadLine() {
		long start2 = System.currentTimeMillis();
		RandomAccessFile rafReader2 = null;
		List<String> linesTwo = new ArrayList<String>();
		try {
			rafReader2 = new RandomAccessFile(filePath2, "r");
			String line = null;
			while ((line = rafReader2.readLine()) != null) {
				line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
				linesTwo.add(line);
				// System.out.println(x);
			}
			if (rafReader2 != null) {
				try {
					rafReader2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		long second2 = System.currentTimeMillis() - start2;
		System.out.println("非缓冲读耗时：" + second2 + "毫秒," + linesTwo.size() + "行");
	}

	@Test
	public void testReadByBuffer() {
		long start = System.currentTimeMillis();
		RandomAccessFile rafReader = null;
		byte[] lastLine = null;
		List<String> linesOne = new ArrayList<String>();
		try {
			rafReader = new RandomAccessFile(filePath1, "r");
			// RandomAccessFile rafWriter = new RandomAccessFile(filePath3,
			// "rw");
			byte[] buffer = new byte[1024 * 10];
			int length = 0;
			while ((length = rafReader.read(buffer)) != -1) {
				byte[] temp = Arrays.copyOfRange(buffer, 0, length);
				temp = addTwoArrays(lastLine, temp);
				lastLine = null;
				int lastIndex = 0;
				for (int i = 0; i < temp.length - 1; i++) {
					// \r\n或\n均为换行符（写入文件有微小差别）
					if (temp[i] == 13 && temp[i + 1] == 10) {
						byte[] tempByte = Arrays
								.copyOfRange(temp, lastIndex, i);
						linesOne.add(new String(tempByte));
						lastIndex = i + 2;
					} else if (temp[i] != 13 && temp[i + 1] == 10) {
						byte[] tempByte = Arrays.copyOfRange(temp, lastIndex,
								i + 1);
						linesOne.add(new String(tempByte));
						lastIndex = i + 2;
					} else if (i + 1 == temp.length - 1) {
						lastLine = Arrays.copyOfRange(temp, lastIndex,
								temp.length);
					}
				}
			}
			// 针对结尾不换行的文件
			if (lastLine != null) {
				linesOne.add(new String(lastLine));
				lastLine = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		long second = System.currentTimeMillis() - start;
		System.out.println("缓冲读耗时：" + second + "毫秒," + linesOne.size() + "行");
	}

	/**
	 * Test method for {@link com.test.BufferedRandomAccessFile#write(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testWriteByteArray() throws IOException {
		RandomAccessFile rafWriter = new BufferedRandomAccessFile(filePath,
				"rw");
		rafWriter.write("中文,，测试1\r\n".getBytes());
		rafWriter.write("中,，测 试2\r\n".getBytes());
		rafWriter.write("文,，测 试3".getBytes());
		rafWriter.close();
	}

	/**
	 * Test method for
	 * {@link com.test.BufferedRandomAccessFile#write(byte[], int, int)}.
	 */
	@Test
	public void testWriteByteArrayIntInt() {
		byte[] src = "中文,，\n测试12".getBytes();
		byte[] dest = new byte[src.length - 8];
		// System.arraycopy(src, 0, dest, 0, src.length - 8);
		// System.out.println(new String(dest));
		System.out.println(new String(new byte[] { (byte) '\n' }));
		System.out.println(Arrays.toString("\n".getBytes()));
		byte b = 10;
		System.out.println((char) b);
		System.out.println("\r".getBytes()[0]);
	}

	/**
	 * Test method for {@link com.test.BufferedRandomAccessFile#seek(long)}.
	 */
	@Test
	public void testSeek() {
		byte[] bytes = "中文\n".getBytes();
		byte[] former = Arrays.copyOfRange(bytes, 0, bytes.length - 3);
		byte[] latter = Arrays.copyOfRange(bytes, bytes.length - 3,
				bytes.length);
		System.out.println(new String((new String(former) + new String(latter))
				.getBytes()));
		System.out.println(new String(addTwoArrays(former, latter)));
	}

	private byte[] addTwoArrays(byte[] former, byte[] latter) {
		if (former == null) {
			return latter;
		} else if (latter == null) {
			return former;
		}
		byte[] array3 = new byte[former.length + latter.length];
		System.arraycopy(former, 0, array3, 0, former.length);
		System.arraycopy(latter, 0, array3, former.length, latter.length);
		return array3;
	}

	/**
	 * Test method for {@link com.test.BufferedRandomAccessFile#read(long)}.
	 */
	@Test
	public void testReadLong() {
		List<String> list = new ArrayList<String>() {
			{
				add("1");
				add("2");
				add("3");
				add("4");
			}
		};
		list.remove(0);
		System.out.println(list.get(0));
	}

	/**
	 * Test method for {@link com.test.BufferedRandomAccessFile#write(byte)}.
	 */
	@Test
	public void testWriteByte() {
		try {
			RandomAccessFileExtension rafe = new RandomAccessFileExtension(
					filePath1, "r");
			RandomAccessFile raf = new RandomAccessFile(filePath3, "rw");
			String line = null;
			while ((line = rafe.readLineByBuffer(false)) != null) {
				raf.write(addTwoArrays(line.getBytes(), "\n".getBytes()));
			}
			rafe.close();
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link com.test.BufferedRandomAccessFile#append(byte)}.
	 */
	@Test
	public void testAppend() {
		System.out.println("start---test\r---end");
		System.out.println("start---test\n---end");
		System.out.println("start---test\r\n---end");
	}

	/**
	 * Test method for
	 * {@link com.test.BufferedRandomAccessFile#write(byte, long)}.
	 */
	@Test
	public void testWriteByteLong() {
		System.out.println(Arrays.toString(new String(new byte[] { -26 })
				.getBytes()));
		System.out.println(Arrays.toString(new String(
				new byte[] { -26, 13, 10 }).getBytes()));
	}

	@Test
	public void testTimeOfReadLineByBuffer() {
		long start = System.currentTimeMillis();
		RandomAccessFileExtension rafReader = null;
		List<String> lines = new ArrayList<String>();
		try {
			rafReader = new RandomAccessFileExtension(filePath1, "r");
			rafReader.readBoolean();
			// rafReader.setBuffer(new byte[10 * 1024]);
			String line = null;
			while ((line = rafReader.readLineByBuffer()) != null) {
				lines.add(line);
			}
			if (rafReader != null) {
				try {
					rafReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		long second = System.currentTimeMillis() - start;
		System.out.println("缓冲读耗时：" + second + "毫秒," + lines.size() + "行");
	}

	@Test
	public void testTimeOfReadLine() {
		long start = System.currentTimeMillis();
		RandomAccessFile rafReader = null;
		List<String> lines = new ArrayList<String>();
		try {
			rafReader = new RandomAccessFile(filePath2, "r");
			String line = null;
			while ((line = rafReader.readLine()) != null) {
				line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
				lines.add(line);
			}
			if (rafReader != null) {
				try {
					rafReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		long second = System.currentTimeMillis() - start;
		System.out.println("非缓冲读耗时：" + second + "毫秒," + lines.size() + "行");
	}

}
