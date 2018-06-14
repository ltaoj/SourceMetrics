package algorithm;

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
    int getLocInClass();

    /**
     * 获取类的语句数
     * @return
     */
    int getLineOfStatementInClass();

    /**
     * 获取类的注释数
     * @return
     */
    int getLineOfCommentInClass();

    /**
     * 获取类的方法数
     * @return
     */
    int getNumberOfMethodInClass();

    /**
     * 获取类的属性数
     * @return
     */
    int getNumberOfAttrInClass();

    /**
     * 获取类的圈复杂度
     * @return
     */
    int getCyclomaticComplexityOfClass();
}
