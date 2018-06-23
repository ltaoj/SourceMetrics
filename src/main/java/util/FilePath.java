package util;

/**
 * Created by ltaoj on 2018/06/14 12:47.
 *
 * @version : 1.0
 */
public class FilePath {

    public final String CLASSPATH = getClass().getResource("").getPath().replace(this.getClass().getPackage().getName()+"/", "");

    public final String PATH = CLASSPATH + "HashMap1.java";

}
