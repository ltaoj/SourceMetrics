package parse;

import algorithm.FileMetricsFactors;
import algorithm.impl.FileMetricsFactorsimpl;
import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.parser.Parser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import util.FilePath;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltaoj on 2018/06/12 19:10.
 *
 * @version : 1.0
 */
public class TestParse {

    private ParserFactory parserFactory;

    public void parse() throws IOException {
        Context context = new Context();
//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        int result = compiler.run(null, null, null, PATH);
//        System.out.println((result == 0) ? "编译成功" : "编译失败");
        Runtime run = Runtime.getRuntime();
        Process p = run.exec("javac Test.java", null, new File(new FilePath().CLASSPATH));
        BufferedInputStream in = new BufferedInputStream(p.getErrorStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String s;
        while ((s = reader.readLine()) != null) {
            System.out.println(s);
        }

        String cmdArray[] = {"/bin/bash", "-c", "java Test"};
        p = run.exec(cmdArray, null, new File(new FilePath().CLASSPATH));
        BufferedInputStream in1 = new BufferedInputStream(p.getErrorStream());
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1));
        while ((s = reader1.readLine()) != null) {
            System.out.println(s);
        }

        JavaFileManager javaFileManager = new JavacFileManager(context, true, Charset.forName("UTF-8"));
    }

    private ParserFactory getJDKParse() {
        Context context = new Context();
        JavacFileManager.preRegister(context);
        ParserFactory factory = ParserFactory.instance(context);
        return factory;
    }

    public TestParse() {
        this.parserFactory = getJDKParse();
    }

    public void parse2() throws IOException {
        Context context = new Context();
        JavaFileManager javaFileManager = new JavacFileManager(context, true, Charset.forName("UTF-8"));
        JavaFileObject javaFileObject = ((JavacFileManager) javaFileManager).getJavaFileObjects(new FilePath().PATH).iterator().next();
        CharSequence source = javaFileObject.getCharContent(true);
        Parser parser = parserFactory.newParser(source, true, true, true);
        JCTree.JCCompilationUnit jcCompilationUnit = parser.parseCompilationUnit();

        System.out.println(jcCompilationUnit.getImports().toString());
        MethodScanner scanner = new MethodScanner();
        List<String> methods = scanner.visitCompilationUnit(jcCompilationUnit, new ArrayList<String>());
        if (methods == null) return;
        for (String method : methods) {
            System.out.println(method);
        }
    }

    //扫描方法时，把方法名加入到一个list中
    static class MethodScanner extends TreeScanner<List<String>, List<String>> {

        @Override
        public List<String> visitImport(ImportTree node, List<String> strings) {
            strings.add(node.toString());
            return strings;
        }

//        @Override
//        public List<String> visitPackage(PackageTree node, List<String> strings) {
//            strings.add(node.getPackageName().toString());
//            return strings;
//        }

        @Override
        public List<String> reduce(List<String> r1, List<String> r2) {
            return super.reduce(r1, r2);
        }
    }

    public static void main(String[] args) throws IOException {
//        TestParse parse;
//        try {
//            new TestParse().parse();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        parse = new TestParse();
//        parse.parse2();
//        SourceParser parser = new SourceParser();
//        TreeScanner countClass = new SourceParser.CountClass();
//        TreeScanner countStatement = new SourceParser.CountStatement();
//        parser.target(parse.PATH)
//                .parse(countStatement);
        FilePath filePath = new FilePath();
        FileMetricsFactors fileMetricsFactors = new FileMetricsFactorsimpl();
        System.out.println("java文件中类的个数为 -> " + fileMetricsFactors.getNumberOfClassInFile(filePath.PATH));
        System.out.println("java文件中语句的个数为 -> " + fileMetricsFactors.getLineOfStatementInFile(filePath.PATH));
        System.out.println("java文件中代码行数为 -> " + fileMetricsFactors.getLocInFile(filePath.PATH));
        System.out.println("java文件中注释行数为 -> " + fileMetricsFactors.getLineOfCommentInFile(filePath.PATH));
    }
}
