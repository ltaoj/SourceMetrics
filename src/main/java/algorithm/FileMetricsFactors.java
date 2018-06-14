package algorithm;

/**
 * Created by ltaoj on 2018/06/13 22:30.
 *
 * @version : 1.0
 */
public interface FileMetricsFactors {

    /**
     * 得到java文件中类的个数
     * @param javaFile
     * @return
     */
    int getNumberOfClassInFile(String javaFile);

    /**
     * 得到java文件总代码行数目
     * @param javaFile
     * @return
     */
    int getLocInFile(String javaFile);

    /**
     * 得到java文件总语句数目
     * @param javaFile
     * @return
     */
    int getLineOfStatementInFile(String javaFile);

    /**
     * 得到java文件总注释行数
     * @param javaFile
     * @return
     */
    int getLineOfCommentInFile(String javaFile);

    /**
     * 得到java文件圈复杂度
     * @param javaFile
     * @return
     */
    int getCyclomaticComplexityOfFile(String javaFile);
}
