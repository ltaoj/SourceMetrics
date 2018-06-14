package util;

import java.util.List;

/**
 * Created by ltaoj on 2018/06/14 03:49.
 *
 * @version : 1.0
 */
public class StringUtil {

    public static int getLineOfString(String str) {
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

    /**
     * 计算代码行
     * @param code 代码文本
     * @return
     */
    public static int getLoc(String code) {
        return getLoc(code, false);
    }

    /**
     * 计算代码行
     * @param code 代码文本
     * @param ignoreDocComment 是否忽略文档注释
     * @return
     */
    public static int getLoc(String code, boolean ignoreDocComment) {
        int endPos = code.indexOf('\n');
        if (endPos == -1)
            return 0;

        if (code.lastIndexOf('\n') != code.length()-1)
            code += "\n";
        // 将多个空行替换成一个空行
        code = code.replaceAll("\n+", "\n");

        int loc = 0;
        int startPos = 0;

        boolean newDocStart = false;
        String line;
        while ((endPos != -1 && (line = code.substring(startPos, endPos)) != null)) {
            if (StringUtil.isEmptyString(line))
                continue;
            // 当需要忽略文档注释时执行此代码块
            if (ignoreDocComment) {
                if (newDocStart && !StringUtil.hasDocCommentEnd(line)) {
                    System.out.println(line);
                    continue;
                }

                if (StringUtil.hasDocCommentStart(line) && !newDocStart) {
                    System.out.println(line);
                    newDocStart = !StringUtil.hasDocCommentEnd(line);
                    continue;
                }

                if (StringUtil.hasDocCommentEnd(line)) {
                    System.out.println(line);
                    newDocStart = false;
                    continue;
                }
            }
            loc++;
            startPos = endPos+1;
            endPos = code.indexOf('\n', startPos);
        }
        return loc;
    }
}
