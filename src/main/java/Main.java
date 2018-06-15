import algorithm.ClassMetricsFactors;
import algorithm.FileMetricsFactors;
import algorithm.MethodMetricsFactors;
import algorithm.impl.ClassMetricsFactorsimpl;
import algorithm.impl.FileMetricsFactorsAdapter;
import algorithm.impl.FileMetricsFactorsimpl;
import org.apache.log4j.Logger;
import util.FilePath;

import java.util.Map;

/**
 * Created by ltaoj on 2018/06/04 19:16.
 *
 * @version : 1.0
 */
public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        FilePath filePath = new FilePath();
        FileMetricsFactors fileMetricsFactors = new FileMetricsFactorsAdapter();
        logger.info("***************************************************************************");
        logger.info("java文件分析结果：                                                          *");
        logger.info("***************************************************************************");
        logger.info("java文件中类的个数为 -> " + fileMetricsFactors.getNumberOfClassInFile(filePath.PATH));
        logger.info("java文件中语句的个数为 -> " + fileMetricsFactors.getLineOfStatementInFile(filePath.PATH));
        logger.info("java文件中代码行数为 -> " + fileMetricsFactors.getLocInFile(filePath.PATH));
        logger.info("java文件中注释行数为 -> " + fileMetricsFactors.getLineOfCommentInFile(filePath.PATH));
        logger.info("java文件中圈复杂度为 -> " + fileMetricsFactors.getCyclomaticComplexityOfFile(filePath.PATH));
        logger.info("***************************************************************************");
        logger.info("***************************************************************************");
        logger.info("");
        logger.info("***************************************************************************");
        logger.info("java文件中类的分析结果：                                                     *");
        logger.info("***************************************************************************");
        ClassMetricsFactors classMetricsFactors = new ClassMetricsFactorsimpl();
        ((ClassMetricsFactorsimpl) classMetricsFactors).target(filePath.PATH).parse();
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
        map = methodMetricsFactors.getLocInMethod();
        for (String key : map.keySet()) {
            logger.info("方法名 -> " + key + " ; 代码行数 -> " + map.get(key));
        }
        logger.info("***************************************************************************");
        map = methodMetricsFactors.getLineOfStatementInMethod();
        for (String key : map.keySet()) {
            logger.info("方法名 -> " + key + " ; 语句个数 -> " + map.get(key));
        }
        logger.info("***************************************************************************");
        map = methodMetricsFactors.getLineOfCommentInMethod();
        for (String key : map.keySet()) {
            logger.info("方法名 -> " + key + " ; 注释行数 -> " + map.get(key));
        }
        logger.info("***************************************************************************");
        map = methodMetricsFactors.getCyclomaticComplexityOfMethod();
        for (String key : map.keySet()) {
            logger.info("方法名 -> " + key + " ; 圏复杂度 -> " + map.get(key));
        }
        logger.info("***************************************************************************");
    }
}
