package algorithm.impl;

import algorithm.FileMetricsFactors;
import com.sun.source.util.TreeScanner;
import parse.SourceParser;
import util.StringUtil;

import java.util.List;

/**
 * Created by ltaoj on 2018/06/13 22:56.
 *
 * @version : 1.0
 */
public class FileMetricsFactorsimpl implements FileMetricsFactors {

    private SourceParser mParser;

    public FileMetricsFactorsimpl() {
        mParser = new SourceParser();
    }

    /**
     * 得到java文件中类的个数
     * 包含内部类
     * @param javaFile
     * @return
     */
    @Override
    public int getNumberOfClassInFile(String javaFile) {
        TreeScanner scanner = new SourceParser.CountClass();
        Integer count = (Integer) mParser.target(javaFile).parse(scanner);
        return count;
    }

    /**
     * 得到java文件总代码行数目
     * @param javaFile
     * @return
     */
    @Override
    public int getLocInFile(String javaFile) {
        return mParser.target(javaFile).parseLoc();
    }

    /**
     * 得到java文件总语句数目
     * 统计有';'的非空表达式，包含变量声明、表达式语句、返回值
     * @param javaFile
     * @return
     */
    @Override
    public int getLineOfStatementInFile(String javaFile) {
        TreeScanner scanner = new SourceParser.CountStatement();
        List<String> statements = (List<String>) mParser.target(javaFile).parse(scanner);
        return statements != null ? statements.size() : 0;
    }

    /**
     * 得到java文件总注释行数
     * 只统计文档注释
     * @param javaFile
     * @return
     */
    @Override
    public int getLineOfCommentInFile(String javaFile) {
        SourceParser.CountComment scanner = new SourceParser.CountComment();
        List<String> docComments = mParser.target(javaFile).parseDocComment(scanner);
        return StringUtil.getLineOfString(docComments);
    }

    /**
     * 得到java文件圈复杂度
     * @param javaFile
     * @return
     */
    @Override
    public int getCyclomaticComplexityOfFile(String javaFile) {
        return 0;
    }
}
