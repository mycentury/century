/**
 * 
 */
package cn.himma.util.file;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年7月6日
 * @ClassName SpecialMultipartResolver
 */
public class SpecialMultipartResolver extends CommonsMultipartResolver {
    @Override
    protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
        return super.newFileUpload(fileItemFactory);
    }
}
