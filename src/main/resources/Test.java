package test.com;
import org.apache.log4j.Logger;
/**
 * Created by ltaoj on 2018/06/12 20:10.
 *
 * @version : 1.0
 */
public class Test {

    private String test;

    public Test(String test) {
        this.test = test;
    }

    /**
     * test
     */
    public void print() {
        System.out.println(test);
        switch (1) {
            case 1:
                //
                int a = 1+3;
                break;
            case 2:
                int a = 1+2;
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        new Test("hello").print();System.out.println("hello");
        System.out.println("测试");
    }

    /**
     * class test2
     */
    public class Test2 {
        public int a;
        private Integer aa;

        /**
         * test setA
         * @param a
         */
        public void setA(int a) {
            ;
            ;
            ;
            this.a = a;
        }
    }

    /**
     * a
     * a
     *
     */
    public class Test3 {
        // var b
        public int b;

        public class Test4 {
            public int c;
        }
    }
}
