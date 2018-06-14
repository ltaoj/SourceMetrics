package algorithm.impl;

import algorithm.ClassMetricsFactors;
import bean.SClassDef;
import com.sun.source.util.TreeScanner;
import parse.SourceParser;

import java.util.Map;

/**
 * Created by ltaoj on 2018/06/14 14:32.
 *
 * @version : 1.0
 */
public class ClassMetricsFactorsimpl implements ClassMetricsFactors {

    private SourceParser mParser;
    private SClassDef dummy;
    private String javaFile;
    private TreeScanner jFileScanner;

    public ClassMetricsFactorsimpl() {
        this.mParser = new SourceParser();
        dummy = new SClassDef(null);
        jFileScanner = new SourceParser.JFileScanner();
    }

    public ClassMetricsFactorsimpl target(String javaFile) {
        this.javaFile = javaFile;
        mParser.target(javaFile);
        emptyDummy();
        return this;
    }

    public ClassMetricsFactorsimpl parse() {
        mParser.parse(jFileScanner, dummy);
        return this;
    }

    private void emptyDummy() {
        if (dummy.getInnerClasses() != null)
            dummy.getInnerClasses().clear();
    }


    @Override
    public Map<String, Integer> getLocInClass() {
        return dummy.computeClassLocMap();
    }

    @Override
    public Map<String, Integer> getLineOfStatementInClass() {
        return dummy.computeStatementMap();
    }

    @Override
    public Map<String, Integer> getLineOfCommentInClass() {
        return dummy.computeCommentMap();
    }

    @Override
    public Map<String, Integer> getNumberOfMethodInClass() {
        return dummy.computeMethodMap();
    }

    @Override
    public Map<String, Integer> getNumberOfAttrInClass() {
        return dummy.computeAttrsMap();
    }

    @Override
    public Map<String, Integer> getCyclomaticComplexityOfClass() {
        return null;
    }

}
