/**
 * 
 */
package cn.himma.business;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.himma.entity.MenuEntity;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月28日
 * @ClassName ICacheBiz
 */
public interface ICacheBiz {

    /**
     * 获取缓存菜单列表
     * 
     * @return
     */
    List<MenuEntity> getCacheMenuList();

    /**
     * 添加菜单，用于接口
     * 
     * @param toAdd
     */
    void addMenu(MenuEntity toAdd);

    /**
     * 根据请求地址获取当前位置
     * 
     * @param request
     * @return
     */
    String getLocation(HttpServletRequest request);

    /**
     * 获取当前菜单
     * 
     * @param request
     * @return
     */
    MenuEntity getCurrentMenu(HttpServletRequest request);

    /**
     * @param request
     * @return
     */
    MenuEntity getParentMenu(HttpServletRequest request);

}
