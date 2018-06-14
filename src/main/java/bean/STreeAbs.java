package bean;

import java.util.List;

/**
 * Created by ltaoj on 2018/06/14 16:28.
 *
 * @version : 1.0
 */
public abstract class STreeAbs implements STree {
    protected CharSequence source;

    protected int startPos;

    protected int endPos;

    protected List<String> modifiers;

    protected String name;

    protected String docComment;

    public STreeAbs(CharSequence source) {
        this.source = source;
    }

    public CharSequence getSource() {
        return source;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<String> modifiers) {
        this.modifiers = modifiers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocComment() {
        return docComment;
    }

    public void setDocComment(String docComment) {
        this.docComment = docComment;
    }
}
