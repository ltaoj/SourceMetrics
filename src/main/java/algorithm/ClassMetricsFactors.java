package algorithm;

import java.util.Map;

/**
 * Created by ltaoj on 2018/06/13 22:46.
 *
 * @version : 1.0
 */
public interface ClassMetricsFactors {

    /**
     * 获取类的代码行数
     * @return
     */
    Map<String, Integer> getLocInClass();

    /**
     * 获取类的语句数
     * @return
     */
    Map<String, Integer> getLineOfStatementInClass();

    /**
     * 获取类的注释数
     * @return
     */
    Map<String, Integer> getLineOfCommentInClass();

    /**
     * 获取类的方法数
     * @return
     */
    Map<String, Integer> getNumberOfMethodInClass();

    /**
     * 获取类的属性数
     * @return
     */
    Map<String, Integer> getNumberOfAttrInClass();

    /**
     * 获取类的圈复杂度
     * @return
     */
    Map<String, Integer> getCyclomaticComplexityOfClass();
}
