/**
 * 
 */
package cn.himma.util.service;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cn.himma.util.excutor.ThreadPool;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年7月7日
 * @ClassName FileUploadRunnable
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileUploadRunnable extends
		ThreadPool.PoolRunnable<FileUploadRunnable> {
	private final static Logger logger = Logger
			.getLogger(FileUploadRunnable.class);
	private ThreadPool<FileUploadRunnable> pool;
	private MultipartFile file;
	private File targetFile;

	@Override
	protected void excute() {
		try {
			// if (!targetFile.exists()) {
			// logger.error("文件已存在！" + targetFile);
			// return;
			// }
			file.transferTo(targetFile);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public ThreadPool<FileUploadRunnable> getPool() {
		return pool;
	}

	public void setParameters(ThreadPool<FileUploadRunnable> pool,
			MultipartFile file, File targetFile) {
		this.pool = pool;
		this.file = file;
		this.targetFile = targetFile;
	}

}
