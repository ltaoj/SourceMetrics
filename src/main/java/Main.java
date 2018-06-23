import org.apache.log4j.Logger;
import parse.ShowReport;

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
        Logger.getLogger(Main.class).info("请输入java文件名(相对路径或者绝对路径):");
        javaFile = scanner.next();
        while (javaFile == null || javaFile.trim().equals("")) {
            Logger.getLogger(Main.class).info("请重新输入:");
            javaFile = scanner.next();
        }
        new ShowReport(javaFile).show();
    }
}
