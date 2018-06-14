import analyzer.LexicalAnalyzer;
import org.apache.log4j.Logger;

/**
 * Created by ltaoj on 2018/06/04 19:16.
 *
 * @version : 1.0
 */
public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("hello world!");

        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer("public class Main { int a = 1; }");
        lexicalAnalyzer.scanAll();
        System.out.println(lexicalAnalyzer.getLexicalKVList());

    }
}
