/**
 * 
 */
package cn.himma.entity;

import java.util.List;

import lombok.Data;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月2日
 * @ClassName MenuEntry
 */
@Data
public class MenuEntity {
    private String code;
    private String name;
    private int seq;
    private List<MenuEntity> children;

    /**
     * @param code
     * @param parentCode
     * @param name
     * @param seq
     */
    public MenuEntity(String code, String name, int seq, List<MenuEntity> children) {
        super();
        this.code = code;
        this.name = name;
        this.seq = seq;
        this.children = children;
    }

}