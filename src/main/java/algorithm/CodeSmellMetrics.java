package algorithm;

import bean.SMethodDef;

import java.util.List;
import java.util.Map;

/**
 * Created by ltaoj on 2018/06/18 18:39.
 *
 * @version : 1.0
 */
public interface CodeSmellMetrics {

    /**
     * 方法过长参数列的预设值
     */
    int MAX_PARAMS = 4;

    /**
     * map为类及其方法列表的集合
     * @param map
     * @return 返回那些类中参数过长的方法
     */
    Map<String, List<SMethodDef>> tooManyParams(Map<String, List<SMethodDef>> map);
}
