package algorithm;

import java.util.Map;

/**
 * Created by ltaoj on 2018/06/13 22:52.
 *
 * @version : 1.0
 */
public interface MethodMetricsFactors {

    /**
     * 获取方法代码行
     * @return
     */
    Map<String, Integer> getLocInMethod();

    /**
     * 获取方法语句数
     * @return
     */
    Map<String, Integer> getLineOfStatementInMethod();

    /**
     * 获取方法注释数
     * @return
     */
    Map<String, Integer> getLineOfCommentInMethod();

    /**
     * 获取方法圈复杂度
     * @return
     */
    Map<String, Integer> getCyclomaticComplexityOfMethod();
}
