import parse.ShowReport;
import util.FilePath;

/**
 * Created by ltaoj on 2018/06/04 19:16.
 *
 * @version : 1.0
 */
public class Main {

    public static void main(String[] args) {
        new ShowReport(new FilePath().PATH).show();
    }
}
