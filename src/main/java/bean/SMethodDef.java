package bean;

import java.util.List;

/**
 * Created by ltaoj on 2018/06/14 15:41.
 *
 * @version : 1.0
 */
public class SMethodDef extends STreeAbs {

    // 方法返回值类型,null表示构造函数
    private String resType;
    // 方法参数
    private List<SAttributeDef> parameters;
    // 方法体
    private String body;
    // 方法语句数量
    private int statementSize;

    public SMethodDef(CharSequence source) {
        super(source);
    }

    @Override
    public Kind getKind() {
        return Kind.METHOD_TYPE;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public List<SAttributeDef> getParameters() {
        return parameters;
    }

    public String getParametersString() {
        StringBuilder sb = new StringBuilder();
        for (SAttributeDef param : parameters) {
            sb.append(param + ",");
        }
        return sb.length() != 0 ? sb.substring(0, sb.length()-1).toString() : "";
    }

    public void setParameters(List<SAttributeDef> parameters) {
        this.parameters = parameters;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatementSize() {
        return statementSize;
    }

    public void setStatementSize(int statementSize) {
        this.statementSize = statementSize;
    }
}
