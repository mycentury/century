/**
 * 
 */
package cn.himma.business.impl;

import org.springframework.stereotype.Service;

import cn.himma.business.IWeatherBiz;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月27日
 * @ClassName WeatherService
 */
@Service
public class WeatherBizImpl implements IWeatherBiz {
    public void test() {
        String s = "http://www.weather.com.cn/adat/sk/101010100.html";
    }
}
