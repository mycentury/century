/**
 * 
 */
package cn.himma.entity;

import lombok.Data;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月20日
 * @ClassName User
 */
@Data
public class UserEntity {
    private String username;
    private String password;
    private String usertype;
}
