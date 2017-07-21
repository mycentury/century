/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.himma.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @Desc 单页xlsx文件导出器 （优点：文件占用空间较小 ；缺点：导出效率低，尤其是追加，消耗CPU、内存大）
 * @author wenge.yan
 * @Date 2016年6月12日
 * @ClassName OneSheetXlsxWriter
 */
public class OneSheetXlsxGenerater {
	// SXSSFWorkbook缓冲行大小
	private static final int ROWS_OF_WRITE_BUFFER = 1000;
	// 备份缓冲字节量大小
	private static final int SIZE_OF_BACK_BUFFER = 1024 * 1024;

	/**
	 * @param excelFilePath
	 *            excel文件绝对路径
	 * @param appendable
	 *            是否可追加
	 * @param data
	 *            数据
	 * @return
	 */
	public int exportXlsxFile(String excelFilePath, boolean appendable,
			Data data) {
		if (!checkParam(excelFilePath, data)) {
			return 0;
		}
		// 如果有txt文件，则备份【并读取数据；然后结合传入数据生成新数据；】最后数据写入新txt文件，原文件删除，如报错则恢复
		List<Data> datas = readDatasFromExportedTxtFile(excelFilePath,
				appendable, data);
		// 如果有excel文件，则备份；然后传入数据生写入新txt文件，原文件删除，如报错则恢复
		int count = writeDatasIntoExcelFile(excelFilePath, datas);
		return count;
	}

	/**
	 * @param excelFilePath
	 * @param datas
	 * @return
	 */
	private int writeDatasIntoExcelFile(String excelFilePath, List<Data> datas) {
		if (datas == null || datas.isEmpty()) {
			return 0;
		}
		FileOutputStream fos = null;
		Sheet sheet = null;
		XSSFWorkbook xssfWorkbook = null;
		Workbook workbook = null;
		int count = 0;
		File excelFile = null;
		File backExcelFile = null;
		try {
			excelFile = new File(excelFilePath);
			// ①先备份(其实是重命名)
			if (excelFile.exists()) {
				excelFile = new File(excelFilePath);
				backExcelFile = new File(generateBackFilePath(excelFilePath));
				excelFile.renameTo(backExcelFile);
				System.out.println("备份Excel完成");
			}

			// ②写入
			xssfWorkbook = new XSSFWorkbook();
			sheet = xssfWorkbook.createSheet("new sheet");
			workbook = new SXSSFWorkbook(xssfWorkbook, ROWS_OF_WRITE_BUFFER,
					true);
			fos = new FileOutputStream(excelFilePath);
			count = writeContent(fos, excelFilePath, workbook, sheet, datas);
			System.out.println("写入Excel完成");

			// ③成功后删除备份
			if (backExcelFile != null && backExcelFile.exists()) {
				backExcelFile.delete();
				System.out.println("Excel备份已删除");
			}
		} catch (Exception e) {
			// ③失败后恢复
			if (backExcelFile != null && backExcelFile.exists()) {
				if (excelFile.exists()) {
					excelFile.delete();
				}
				backExcelFile.renameTo(excelFile);
				System.out.println("操作失败，Excel备份已恢复");
			}
			e.printStackTrace();
			return 0;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	/**
	 * @param excelFilePath
	 * @param appendable
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private List<Data> readDatasFromExportedTxtFile(String excelFilePath,
			boolean appendable, Data data) {
		List<Data> datas = null;
		String txtFilePath = generateTxtFilePath(excelFilePath);
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		File txtFile = null;
		File backTxtFile = null;
		try {
			txtFile = new File(txtFilePath);
			// ①先备份(其实是重命名),并从备份中读取数据
			if (txtFile.exists()) {
				txtFile = new File(txtFilePath);
				backTxtFile = new File(generateBackFilePath(txtFilePath));
				if (appendable) {
					ois = new ObjectInputStream(new FileInputStream(txtFile));
					Object readObject = ois.readObject();
					if (readObject != null) {
						datas = (List<Data>) readObject;
					}
					ois.close();
				}
				txtFile.renameTo(backTxtFile);
				System.out.println("备份Txt完成");
			}
			// ②写入
			if (datas == null) {
				datas = new ArrayList<Data>();
			}
			datas.add(data);
			oos = new ObjectOutputStream(new FileOutputStream(txtFile));
			oos.writeObject(datas);
			oos.flush();
			oos.close();
			System.out.println("写入Txt完成");

			// ③成功后删除备份
			if (backTxtFile != null && backTxtFile.exists()) {
				backTxtFile.delete();
				System.out.println("Txt备份已删除");
			}
		} catch (Exception e) {
			// ③失败后恢复
			if (backTxtFile != null && backTxtFile.exists()) {
				if (txtFile.exists()) {
					txtFile.delete();
				}
				backTxtFile.renameTo(txtFile);
				System.out.println("操作失败，Txt备份已恢复");
			}
			return null;
		} finally {
			try {
				if (ois != null)
					ois.close();
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return datas;
	}

	/**
	 * @param filePath
	 * @return
	 */
	private String generateTxtFilePath(String filePath) {
		int splitIndex = filePath.lastIndexOf(".");
		if (splitIndex <= 0) {
			throw new IllegalArgumentException("文件名称有误:" + filePath);
		}
		StringBuilder sb = new StringBuilder(filePath.substring(0, splitIndex));
		sb.append(".txt");
		return sb.toString();
	}

	/**
	 * @param filePath
	 * @param head
	 * @param bodyTitle
	 * @param body
	 * @return
	 */
	private boolean checkParam(String filePath, Data data) {
		if (filePath == null) {
			return false;
		}
		if ((data.head == null || data.head.isEmpty())
				&& (data.bodyTitle == null || data.bodyTitle.isEmpty())
				&& (data.body == null || data.body.isEmpty())) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unused")
	private File backupFile(String filePath) {
		String backFilePath = generateBackFilePath(filePath);
		return backupFile(filePath, backFilePath);
	}

	/**
	 * @param filePath
	 * @param backFilePath
	 * @return
	 */
	private File backupFile(String filePath, String backFilePath) {
		File backFile = new File(backFilePath);
		if (backFile.exists()) {
			backFile.delete();
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;
		ByteBuffer byteBuffer = null;
		try {
			fis = new FileInputStream(filePath);
			fos = new FileOutputStream(backFilePath);

			FileChannel ic = fis.getChannel();
			FileChannel oc = fos.getChannel();
			while (ic.position() != ic.size()) {
				int capacity = (int) (ic.size() - ic.position() < SIZE_OF_BACK_BUFFER ? ic
						.size() - ic.position()
						: SIZE_OF_BACK_BUFFER);
				byteBuffer = ByteBuffer.allocate(capacity);
				ic.read(byteBuffer);
				byteBuffer.flip();
				oc.write(byteBuffer);
				oc.force(false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return backFile;
	}

	/**
	 * @param filePath
	 * @return
	 */
	private String generateBackFilePath(String filePath) {
		int splitIndex = filePath.lastIndexOf(".");
		if (splitIndex <= 0) {
			throw new IllegalArgumentException("文件名称有误:" + filePath);
		}
		StringBuilder sb = new StringBuilder(filePath.substring(0, splitIndex));
		sb.append("_back_").append(
				new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		sb.append(filePath.substring(splitIndex));
		String backFilePath = sb.toString();
		return backFilePath;
	}

	private int writeContent(FileOutputStream fos, String filePath,
			Workbook wb, Sheet sheet, List<Data> datas) throws IOException {
		int index = 0;
		if (datas == null || datas.isEmpty()) {
			return 0;
		}
		for (Data data : datas) {
			index = createExcelDatas(wb, sheet, index, data);
		}
		wb.write(fos);
		return index;
	}

	/**
	 * @param wb
	 * @param sheet
	 * @param index
	 * @param data
	 * @return
	 */
	private int createExcelDatas(Workbook wb, Sheet sheet, int index, Data data) {
		if (data.head != null && data.head.size() > 0) {
			for (int j = 0; j < data.head.size(); j++) {
				CellStyle style = wb.createCellStyle();
				Font font = wb.createFont();
				font.setBold(true);
				style.setFont(font);
				style.setWrapText(false);
				style.setAlignment(CellStyle.ALIGN_CENTER);

				Row row = sheet.createRow(index++);
				Cell cell = row.createCell(0);
				cell.setCellValue(data.head.get(j).toString());
				cell.setCellStyle(style);
			}
		}
		if (data.bodyTitle != null) {
			CellStyle style = wb.createCellStyle();
			Font font = wb.createFont();
			font.setBold(true);
			style.setFont(font);
			style.setWrapText(false);
			style.setAlignment(CellStyle.ALIGN_CENTER);
			Row row = sheet.createRow(index++);
			row.setRowStyle(style);
			for (int j = 0; j < data.bodyTitle.size(); j++) {
				Cell cell = row.createCell(j);
				cell.setCellValue(data.bodyTitle.get(j).toString());
			}
		}
		if (data.body != null) {
			for (int i = 0; i < data.body.size(); i++) {
				Row row = sheet.createRow(index++);
				List<Object> list = data.body.get(i);
				for (int j = 0; j < list.size(); j++) {
					row.createCell(j).setCellValue(list.get(j).toString()); // 序号
				}
			}
		}
		return index;
	}

	public static class Data implements Serializable {
		private static final long serialVersionUID = 1L;
		List<Object> head;
		List<Object> bodyTitle;
		List<List<Object>> body;
		int rows;

		/**
		 * @param head
		 * @param bodyTitle
		 * @param body
		 */
		public Data(List<Object> head, List<Object> bodyTitle,
				List<List<Object>> body) {
			super();
			this.head = head;
			this.bodyTitle = bodyTitle;
			this.body = body;
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		OneSheetXlsxGenerater writer = new OneSheetXlsxGenerater();
		int rows = 1000000;
		int cols = 30;
		String oldFilePath = "D:\\项目读写文件\\workbookBase.xlsx";
		String useFilePath = "D:\\项目读写文件\\workbookBase6.xlsx";
		// writer.backupFile(oldFilePath, useFilePath);
		List<Object> head = new ArrayList<Object>();
		head.add(String.valueOf(rows * cols));
		List<Object> bodyTitle = new ArrayList<Object>();
		for (int i = 0; i < cols; i++) {
			bodyTitle.add("test t" + (i + 1));
		}
		List<List<Object>> body = new ArrayList<List<Object>>();
		for (int i = 0; i < rows; i++) {
			List<Object> row = new ArrayList<Object>();
			for (int j = 0; j < cols; j++) {
				row.add((10 - j) + "年之前");
			}
			body.add(row);
		}

		long startTime = System.currentTimeMillis();
		writer.exportXlsxFile(useFilePath, true,
				new Data(head, bodyTitle, body));
		// writer.exportXlsxFile(useFilePath, true, new Data(head, bodyTitle,
		// body));
		long endTime = System.currentTimeMillis();
		long second = (endTime - startTime) / 1000;
		System.out.println((rows * cols) + "耗时:" + second / 3600 + "h"
				+ (second / 60) % 60 + "m" + second % 60 + "s");
	}
}
