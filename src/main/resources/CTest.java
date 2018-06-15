import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltaoj on 2018/06/16 00:31.
 *
 * @version : 1.0
 */
public class CTest {

    private int a = 0;

    public void base1() {
        if (a > 0) {
            a--;
        } else {
            a++;
        }

        a = 9;

        if (a < 10) {
            a++;
        } else {
            do {
                a--;
            }while (a > 5);
        }

        a = 0;
    }

    /**
     * @param a
     * @return a * 3
     */
    public void mul(int a) {
        int b = a * 3;
    }
}
