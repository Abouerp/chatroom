package zsc.edu.cn.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 方法类
 * @author Abouerp
 *
 */
public class CatUtil {
	@SuppressWarnings("unused")
	public static String getTimer() {
		//获取时间格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
}
