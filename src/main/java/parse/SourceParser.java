package parse;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.parser.Parser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.DocCommentTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import org.apache.log4j.Logger;
import util.StringUtil;

import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltaoj on 2018/06/13 19:58.
 *
 * @version : 1.0
 */
public class SourceParser<R, P> {

    private static final Logger logger = Logger.getLogger(SourceParser.class);

    private Context mContext;
    private ParserFactory mParserFactory;
    private JavacFileManager mJavacFileManager;
    private Parser mParser;
    private String mJavaFile;

    public SourceParser() {
        mContext = new Context();
        mJavacFileManager = new JavacFileManager(mContext, true, Charset.forName("UTF-8"));
        mParserFactory = ParserFactory.instance(mContext);
    }


    public SourceParser target(String javaFile) {
        this.mJavaFile = javaFile;
        return this;
    }

    /**
     * 统计java文件代码行
     * 去除空行, 计算注释
     * @return
     */
    public int parseLoc() {
        return parseLoc(false);
    }

    /**
     * 统计java文件代码行
     * @param ignoreDocComment 表示是否忽略多行注释、文档注释
     * @return
     */
    public int parseLoc(boolean ignoreDocComment) {
        JavaFileObject javaFileObject = getJavaFileObject();
        int loc = 0;
        try {
            Reader sourceReader = javaFileObject.openReader(true);
            BufferedReader br = new BufferedReader(sourceReader);
            String line;
            boolean newDocStart = false;
            while ((line = br.readLine()) != null) {
                if (StringUtil.isEmptyString(line))
                    continue;
                // 当需要忽略文档注释时执行此代码块
                if (ignoreDocComment) {
                    if (newDocStart && !StringUtil.hasDocCommentEnd(line)) {
                        System.out.println(line);
                        continue;
                    }

                    if (StringUtil.hasDocCommentStart(line) && !newDocStart) {
                        System.out.println(line);
                        newDocStart = !StringUtil.hasDocCommentEnd(line);
                        continue;
                    }

                    if (StringUtil.hasDocCommentEnd(line)) {
                        System.out.println(line);
                        newDocStart = false;
                        continue;
                    }
                }
                loc++;
            }
        } catch (IOException e) {
            logger.error("文件不存在 " + e.getMessage());
        }
        return loc;
    }

    public List<String> parseDocComment(CountComment scanner) {
        JavaFileObject javaFileObject = getJavaFileObject();
        try {
            CharSequence source = javaFileObject.getCharContent(true);
            mParser = mParserFactory.newParser(source, true, true, true);
            JCTree.JCCompilationUnit structure = mParser.parseCompilationUnit();
            scanner.setTable(structure.docComments);
            scanner.visitTopLevel(structure);
            return scanner.getComments();
        } catch (IOException e) {
            logger.error("文件不存在 " + e.getMessage());
        }
        return null;
    }

    public R parse(TreeScanner<R, P> treeScanner) {
        JavaFileObject javaFileObject = getJavaFileObject();
        try {
            CharSequence source = javaFileObject.getCharContent(true);
            mParser = mParserFactory.newParser(source, true, true, true);
            JCTree.JCCompilationUnit structure = mParser.parseCompilationUnit();
            R res = treeScanner.visitCompilationUnit(structure, null);
            return res;
        } catch (IOException e) {
            logger.error("文件不存在" + e.getMessage());
        }
        return null;
    }

    private JavaFileObject getJavaFileObject() {
        return mJavacFileManager.getJavaFileObjects(mJavaFile).iterator().next();
    }

    /**
     * 计算语句个数
     */
    public static class CountStatement extends TreeScanner<List<String>, List<String>> {

        @Override
        public List<String> visitExpressionStatement(ExpressionStatementTree node, List<String> strings) {
            List<String> str = new ArrayList<String>();
            str.add(String.valueOf(node));
            return str;
        }

        @Override
        public List<String> visitVariable(VariableTree node, List<String> list) {
            List<String> str = new ArrayList<String>();
            str.add(String.valueOf(node));
            return str;
        }

        @Override
        public List<String> visitReturn(ReturnTree node, List<String> list) {
            List<String> str = new ArrayList<String>();
            str.add(String.valueOf(node));
            return str;
        }

        @Override
        public List<String> reduce(List<String> r1, List<String> r2) {
            if (r2 == null)
                r2 = new ArrayList<String>();
            if (r1 != null)
                r2.addAll(r1);
            return r2;
        }
    }

    /**
     * 计算类的个数
     */
    public static class CountClass extends TreeScanner<Integer, Integer> {

        @Override
        public Integer visitClass(ClassTree node, Integer integer) {
            return reduce(scan(node.getMembers(), 0), 1);
        }

        @Override
        public Integer reduce(Integer r1, Integer r2) {
            return (r1 == null ? 0 : r1) + (r2 == null ? 0 : r2);
        }
    }

    public static class CountComment extends com.sun.tools.javac.tree.TreeScanner {

        private DocCommentTable table;

        private List<String> comments;

        public CountComment() {
            this.comments = new ArrayList<String>();
        }

        @Override
        public void visitMethodDef(JCTree.JCMethodDecl tree) {
            hasCommentAdded(tree);
            super.visitMethodDef(tree);
        }

        @Override
        public void visitClassDef(JCTree.JCClassDecl tree) {
            hasCommentAdded(tree);
            super.visitClassDef(tree);
        }

        @Override
        public void visitVarDef(JCTree.JCVariableDecl tree) {
            hasCommentAdded(tree);
            super.visitVarDef(tree);
        }

        private void hasCommentAdded(JCTree tree) {
            if (table.hasComment(tree)) {
                this.comments.add(table.getCommentText(tree));
            }
        }

        public void setTable(DocCommentTable table) {
            this.table = table;
        }

        public List<String> getComments() {
            return comments;
        }
    }
}
