package algorithm.impl;

import algorithm.CodeSmellMetrics;
import bean.SMethodDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ltaoj on 2018/06/18 18:50.
 *
 * @version : 1.0
 */
public class CodeSmellMetricsimpl implements CodeSmellMetrics {

    @Override
    public Map<String, List<SMethodDef>> tooManyParams(Map<String, List<SMethodDef>> map) {

        Map<String, List<SMethodDef>> result = new HashMap<String, List<SMethodDef>>();

        for (String key : map.keySet()) {

            List<SMethodDef> methods = map.get(key);

            List<SMethodDef> resValue = null;

            if (methods == null) continue;
            for (SMethodDef method : map.get(key)) {
                if (method.getParameters() != null && method.getParameters().size() > MAX_PARAMS) {
                    if (resValue == null) {
                        resValue = new ArrayList<SMethodDef>();
                    }

                    resValue.add(method);
                    result.put(key, resValue);
                }
            }
        }
        return result;
    }
}
