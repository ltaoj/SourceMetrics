package parse;

import algorithm.ClassMetricsFactors;
import algorithm.FileMetricsFactors;
import algorithm.MethodMetricsFactors;
import algorithm.impl.ClassMetricsFactorsimpl;
import algorithm.impl.FileMetricsFactorsAdapter;
import org.apache.log4j.Logger;
import util.FilePath;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class ShowReport {

    final static Logger logger = Logger.getLogger(ShowReport.class);

    private String javaFile;

    public ShowReport(String javaFile) {
        this.javaFile = javaFile;
    }

    public void show() {
        FilePath filePath = new FilePath();
        FileMetricsFactors fileMetricsFactors = new FileMetricsFactorsAdapter();
        logger.info("***************************************************************************");
        logger.info("java文件分析结果：                                                          *");
        logger.info("***************************************************************************");
        logger.info("java文件中类的个数为 -> " + fileMetricsFactors.getNumberOfClassInFile(javaFile));
        logger.info("java文件中语句的个数为 -> " + fileMetricsFactors.getLineOfStatementInFile(javaFile));
        logger.info("java文件中代码行数为 -> " + fileMetricsFactors.getLocInFile(javaFile));
        logger.info("java文件中注释行数为 -> " + fileMetricsFactors.getLineOfCommentInFile(javaFile));
        logger.info("java文件中圈复杂度为 -> " + fileMetricsFactors.getCyclomaticComplexityOfFile(javaFile));
        logger.info("***************************************************************************");
        logger.info("***************************************************************************");
        logger.info("");
        logger.info("***************************************************************************");
        logger.info("java文件中类的分析结果：                                                     *");
        logger.info("***************************************************************************");
        ClassMetricsFactors classMetricsFactors = new ClassMetricsFactorsimpl();
        ((ClassMetricsFactorsimpl) classMetricsFactors).target(javaFile).parse();
        Map<String, Integer> map = classMetricsFactors.getNumberOfAttrInClass();
        for (String key : map.keySet()) {
            logger.info("类名 -> " + key + " ; 属性个数 -> " + map.get(key));
        }
        logger.info("***************************************************************************");
        map = classMetricsFactors.getNumberOfMethodInClass();
        for (String key : map.keySet()) {
            logger.info("类名 -> " + key + " ; 方法个数 -> " + map.get(key));
        }
        logger.info("***************************************************************************");
        map = classMetricsFactors.getLineOfStatementInClass();
        for (String key : map.keySet()) {
            logger.info("类名 -> " + key + " ; 语句个数 -> " + map.get(key));
        }
        logger.info("***************************************************************************");
        map = classMetricsFactors.getLineOfCommentInClass();
        for (String key : map.keySet()) {
            logger.info("类名 -> " + key + " ; 注释行数 -> " + map.get(key));
        }
        logger.info("***************************************************************************");
        map = classMetricsFactors.getLocInClass();
        for (String key : map.keySet()) {
            logger.info("类名 -> " + key + " ; 代码行数 -> " + map.get(key));
        }
        logger.info("***************************************************************************");
        map = classMetricsFactors.getCyclomaticComplexityOfClass();
        for (String key : map.keySet()) {
            logger.info("类名 -> " + key + " ; 圏复杂度 -> " + map.get(key));
        }
        logger.info("***************************************************************************");
        logger.info("***************************************************************************");
        logger.info("");
        logger.info("***************************************************************************");
        logger.info("java文件中方法的分析结果：                                                   *");
        logger.info("***************************************************************************");
        MethodMetricsFactors methodMetricsFactors = (MethodMetricsFactors) classMetricsFactors;
        CompareByEntry comparator = new CompareByEntry();
        Map.Entry<String, Integer> maxEntry = null;
        int sum = 0;

        map = methodMetricsFactors.getLocInMethod();
        for (String key : map.keySet()) {
            sum += map.get(key);
//            logger.info("方法名 -> " + key + " ; 代码行数 -> " + map.get(key));
        }

        maxEntry = Collections.max(map.entrySet(), comparator);
        logger.info("*方法名 -> " + maxEntry.getKey() + " ; 代码行数 1th -> " + maxEntry.getValue());
        map.remove(maxEntry.getKey());
        maxEntry = Collections.max(map.entrySet(), comparator);
        logger.info("*方法名 -> " + maxEntry.getKey() + " ; 代码行数 2th -> " + maxEntry.getValue());
        map.remove(maxEntry.getKey());
        maxEntry = Collections.max(map.entrySet(), comparator);
        logger.info("*方法名 -> " + maxEntry.getKey() + " ; 代码行数 3th -> " + maxEntry.getValue());
        logger.info("平均方法代码行数 -> " + sum / map.size());


        logger.info("***************************************************************************");
        map = methodMetricsFactors.getLineOfStatementInMethod();
        sum = 0;
        for (String key : map.keySet()) {
//            logger.info("方法名 -> " + key + " ; 语句个数 -> " + map.get(key));
            sum += map.get(key);
        }
        logger.info("平均方法语句个数 -> " + sum / map.size());


        logger.info("***************************************************************************");
        map = methodMetricsFactors.getLineOfCommentInMethod();
        sum = 0;
        for (String key : map.keySet()) {
//            logger.info("方法名 -> " + key + " ; 注释行数 -> " + map.get(key));
            sum += map.get(key);
        }
        logger.info("平均方法注释行数 -> " + sum / map.size());


        logger.info("***************************************************************************");
        map = methodMetricsFactors.getCyclomaticComplexityOfMethod();
        sum = 0;
        for (String key : map.keySet()) {
//            logger.info("方法名 -> " + key + " ; 圏复杂度 -> " + map.get(key));
            sum += map.get(key);
        }
        maxEntry = Collections.max(map.entrySet(), comparator);
        logger.info("*方法名 -> " + maxEntry.getKey() + " ; 圏复杂度 1th -> " + maxEntry.getValue());
        map.remove(maxEntry.getKey());
        maxEntry = Collections.max(map.entrySet(), comparator);
        logger.info("*方法名 -> " + maxEntry.getKey() + " ; 圏复杂度 2th -> " + maxEntry.getValue());
        map.remove(maxEntry.getKey());
        maxEntry = Collections.max(map.entrySet(), comparator);
        logger.info("*方法名 -> " + maxEntry.getKey() + " ; 圏复杂度 3th -> " + maxEntry.getValue());
        logger.info("平均方法圏复杂度 -> " + sum / map.size());
        logger.info("***************************************************************************");
    }

    class CompareByEntry implements Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            return o1.getValue() - o2.getValue();
        }
    }
}