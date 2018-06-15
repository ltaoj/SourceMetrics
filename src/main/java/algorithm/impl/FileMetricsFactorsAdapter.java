package algorithm.impl;

import algorithm.ClassMetricsFactors;

import java.util.Map;

/**
 * Created by ltaoj on 2018/06/16 03:26.
 *
 * @version : 1.0
 */
public class FileMetricsFactorsAdapter extends FileMetricsFactorsimpl {

    private ClassMetricsFactors classMetricsFactors;

    public void setClassMetricsFactors(ClassMetricsFactors classMetricsFactors) {
        this.classMetricsFactors = classMetricsFactors;
    }

    @Override
    public int getCyclomaticComplexityOfFile(String javaFile) {
        ClassMetricsFactors classMetricsFactors = new ClassMetricsFactorsimpl();
        ((ClassMetricsFactorsimpl) classMetricsFactors).target(javaFile).parse();
        Map<String, Integer> map = classMetricsFactors.getCyclomaticComplexityOfClass();
        int comp = 0;
        for (String key : map.keySet()) {
            comp += map.get(key);
        }
        return comp;
    }
}
