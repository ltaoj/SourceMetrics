package util;

import java.util.List;

/**
 * Created by ltaoj on 2018/06/14 03:49.
 *
 * @version : 1.0
 */
public class StringUtil {

    private static int getLineOfString(String str) {
        if (str == null || str.length() == 0)
            return 0;
        int res = 1;
        int occur = str.indexOf('\n');
        if (occur != -1) {
            res += getLineOfString(str.substring(occur+1));
        }
        return res;
    }

    /**
     * 获得字符集合总行数
     * @param list
     * @return
     */
    public static int getLineOfString(List<String> list) {
        int lineCount = 0;

        for (String string : list) {
            lineCount += getLineOfString(string);
        }

        return lineCount;
    }

    /**
     * 判断str是否为空行
     * @param str
     * @return
     */
    public static boolean isEmptyString(String str) {
        str = str.replaceAll("( |\n|\t)+", "");
        return str == null || str.isEmpty();
    }

    /**
     * 判断是否为多行注释的开始
     * @param str
     * @return
     */
    public static boolean hasDocCommentStart(String str) {
        return str.contains("/*");
    }

    /**
     * 判断是否为多行注释的结尾
     * @param str
     * @return
     */
    public static boolean hasDocCommentEnd(String str) {
        return str.contains("*/");
    }
}
