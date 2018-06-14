package bean;

/**
 * Created by ltaoj on 2018/06/14 15:42.
 *
 * @version : 1.0
 */
public class SAttributeDef extends STreeAbs {

    private String varType;

    public SAttributeDef(CharSequence source) {
        super(source);
    }

    @Override
    public Kind getKind() {
        return Kind.ATTRIBUTE_TYPE;
    }

    public String getVarType() {
        return varType;
    }

    public void setVarType(String varType) {
        this.varType = varType;
    }
}
