package util;

/**
 * Created by ltaoj on 2018/06/14 12:47.
 *
 * @version : 1.0
 */
public class FilePath {

    public final String CLASSPATH = this.getClass().getResource("/").getPath();

    public final String PATH = CLASSPATH + "Test.java";

}
