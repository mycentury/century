/**
 * 
 */
package cn.himma.hs.impl;

import org.springframework.stereotype.Service;

import cn.himma.hs.api.HelloApi;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月21日
 * @ClassName HelloApiImpl
 */
@Service
public class HelloApiImpl implements HelloApi {

	@Override
	public String sayHello(String name) {
		return "Hello," + name + "!";
	}

}
