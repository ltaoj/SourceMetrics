package bean;

/**
 * Created by ltaoj on 2018/06/14 15:59.
 *
 * @version : 1.0
 */
public interface STree {

    public enum Kind {

        /**
         * 类树
         */
        CLASS_TYPE(SClassDef.class),

        /**
         * 方法树
         */
        METHOD_TYPE(SMethodDef.class),

        /**
         * 成员变量树
         */
        ATTRIBUTE_TYPE(SAttributeDef.class);

        Kind(Class<? extends STree> inif) {
            this.associatedInterface = inif;
        }

        private final Class<? extends STree> associatedInterface;
    }

    /**
     * 返回树的类型
     * @return
     */
    Kind getKind();
}
