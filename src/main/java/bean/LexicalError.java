package bean;

/**
 * Created by ltaoj on 2018/06/04 20:26.
 *
 * @version : 1.0
 */
public class LexicalError {

    private String errorCode;

    private String describe;

    private String rowNum;

    public LexicalError(String errorCode, String describe, String rowNum) {
        this.errorCode = errorCode;
        this.describe = describe;
        this.rowNum = rowNum;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getRowNum() {
        return rowNum;
    }

    public void setRowNum(String rowNum) {
        this.rowNum = rowNum;
    }
}
