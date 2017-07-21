/**
 * 
 */
package cn.himma.business;

import cn.himma.entity.UserEntity;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月28日
 * @ClassName ILoginBiz
 */
public interface ILoginBiz {

    /**
     * @param user
     * @return
     */
    boolean login(UserEntity user);

}
