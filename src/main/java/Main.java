import org.apache.log4j.Logger;
import parse.ShowReport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by ltaoj on 2018/06/04 19:16.
 *
 * @version : 1.0
 */
public class Main {

    private static String javaFile = "";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Logger.getLogger(Main.class).info("请输入项目路径或者单个java路径(相对路径或者绝对路径均可):");
        javaFile = scanner.next();
        while (javaFile == null || javaFile.trim().equals("")) {
            Logger.getLogger(Main.class).info("请重新输入:");
            javaFile = scanner.next();
        }
        List<String> javaFiles = parseJavaFile(javaFile);
        if (javaFiles == null)
            Logger.getLogger(Main.class).info("文件不存在!" + javaFile);

        ShowReport showReport = new ShowReport();
        for (String jpath : javaFiles) {
            showReport.setJavaFile(jpath).show();
        }
    }

    public static List<String> parseJavaFile(String path) {
        Logger.getLogger(Main.class).info("开始扫描java文件");
        List<String> javaFiles = new ArrayList<String>();
        File rootFile = new File(path);
        if (!rootFile.exists())
            return null;

        if (rootFile.isFile() && rootFile.getAbsolutePath().endsWith(".java")) {
            javaFiles.add(rootFile.getAbsolutePath());
            Logger.getLogger(Main.class).info(rootFile.getAbsoluteFile());
        } else if (rootFile.isDirectory()){
            dfsDir(rootFile, javaFiles, 0);
        }

        Logger.getLogger(Main.class).info("扫描结束, java文件个数 -> " + (javaFiles != null ? javaFiles.size() : 0));
        return javaFiles;
    }

    private static void dfsDir(File file, List<String> javaFiles, int level) {
        if (file == null)
            return;

        StringBuilder prefix = new StringBuilder();
        for (int i = 0;i < level;i++) {
            prefix.append("\t");
        }
        prefix.append((level+1) + ". ");

        if (!file.isHidden())
            Logger.getLogger(Main.class).info(prefix.toString() + file.getName());

        if (file.getAbsolutePath().endsWith(".java")) {
            javaFiles.add(file.getAbsolutePath());
        } else if (file.isDirectory() && !file.isHidden()){
            for (File childFile : file.listFiles()) {
                dfsDir(childFile, javaFiles, level+1);
            }
        }
    }
}
