package zsc.edu.cn.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ������
 * @author Abouerp
 *
 */
public class CatUtil {
	@SuppressWarnings("unused")
	public static String getTimer() {
		//��ȡʱ���ʽ
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
}
