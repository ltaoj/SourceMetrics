package bean;

import util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ltaoj on 2018/06/14 15:41.
 *
 * @version : 1.0
 */
public class SClassDef extends STreeAbs {

    // 对象计数器
    private static int counter = 0;

    private int id;

    public SClassDef(CharSequence source) {
        super(source);
        synchronized (SClassDef.class) {
            this.id = counter;
        }
    }

    private SClassDef outerClass;

    private List<SAttributeDef> attributes;

    private List<SMethodDef> methods;

    private List<SClassDef> innerClasses;

    private String extendingClass;

    private List<String> implementingClass;

    // 暂时保存内部类的文档注释
    private String innerTempDocComment;

    // 暂时保存内部类的开始位置
    private int innerTempStartPos;

    // 暂时保存内部类的结束位置
    private int innerTempEndPos;

    public List<SAttributeDef> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<SAttributeDef> attributes) {
        this.attributes = attributes;
    }

    public List<SMethodDef> getMethods() {
        return methods;
    }

    public void setMethods(List<SMethodDef> methods) {
        this.methods = methods;
    }

    public List<SClassDef> getInnerClasses() {
        return innerClasses;
    }

    public void setInnerClasses(List<SClassDef> innerClasses) {
        this.innerClasses = innerClasses;
    }

    public String getExtendingClass() {
        return extendingClass;
    }

    public void setExtendingClass(String extendingClass) {
        this.extendingClass = extendingClass;
    }

    public List<String> getImplementingClass() {
        return implementingClass;
    }

    public void setImplementingClass(List<String> implementingClass) {
        this.implementingClass = implementingClass;
    }

    public SClassDef getOuterClass() {
        return outerClass;
    }

    public void setOuterClass(SClassDef outerClass) {
        this.outerClass = outerClass;
    }

    public int getId() {
        return id;
    }

    public String getInnerTempDocComment() {
        return innerTempDocComment;
    }

    public void setInnerTempDocComment(String innerTempDocComment) {
        this.innerTempDocComment = innerTempDocComment;
    }

    public int getInnerTempStartPos() {
        return innerTempStartPos;
    }

    public void setInnerTempStartPos(int innerTempStartPos) {
        this.innerTempStartPos = innerTempStartPos;
    }

    public int getInnerTempEndPos() {
        return innerTempEndPos;
    }

    public void setInnerTempEndPos(int innerTempEndPos) {
        this.innerTempEndPos = innerTempEndPos;
    }

    @Override
    public Kind getKind() {
        return Kind.CLASS_TYPE;
    }


    /**
     * 计算每个类的属性个数
     * @return
     */
    public Map<String, Integer> computeAttrsMap() {
        final List<SClassDef> innerClasses = this.innerClasses;
        return computeAttrsMap(innerClasses, null);
    }

    private Map<String, Integer> computeAttrsMap(List<SClassDef> innerClasses, Map<String, Integer> map) {
        if (innerClasses == null)
            return map;

        if (map == null) {
            map = new HashMap<String, Integer>();
        }

        for (int i = 0;i < innerClasses.size();i++) {
            String name = innerClasses.get(i).getName();
            List<SAttributeDef> attributes = innerClasses.get(i).getAttributes();
            map.put(name, attributes != null ? attributes.size() : 0);
            computeAttrsMap(innerClasses.get(i).getInnerClasses(), map);
        }

        return map;
    }

    /**
     * 计算每个类的方法个数
     * @return
     */
    public Map<String, Integer> computeMethodMap() {
        final List<SClassDef> innerClasses = this.innerClasses;
        return computeMethodMap(innerClasses, null);
    }

    private Map<String, Integer> computeMethodMap(List<SClassDef> innerClasses, Map<String, Integer> map) {
        if (innerClasses == null)
            return map;

        if (map == null) {
            map = new HashMap<String, Integer>();
        }

        for (int i = 0;i < innerClasses.size();i++) {
            String name = innerClasses.get(i).getName();
            List<SMethodDef> methods = innerClasses.get(i).getMethods();
            map.put(name, methods != null ? methods.size() : 0);
            computeAttrsMap(innerClasses.get(i).getInnerClasses(), map);
        }

        return map;
    }

    /**
     * 计算类的语句数目
     * @return
     */
    public Map<String, Integer> computeStatementMap() {
        final List<SClassDef> innerClasses = this.innerClasses;
        return computeStatementMap(innerClasses, null);
    }

    private Map<String, Integer> computeStatementMap(List<SClassDef> innerClasses, Map<String, Integer> map) {
        if (innerClasses == null)
            return map;

        if (map == null) {
            map = new HashMap<String, Integer>();
        }

        for (int i = 0;i < innerClasses.size();i++) {
            String name = innerClasses.get(i).getName();
            // 一个类的语句包含两部分，属性+方法体中的语句
            int statCount = 0;

            List<SMethodDef> methods = innerClasses.get(i).getMethods();
            for (int j = 0;j < methods.size();j++) {
                statCount += methods.get(j).getStatementSize();
            }

            List<SAttributeDef> attributes = innerClasses.get(i).getAttributes();
            statCount += (attributes != null ? attributes.size() : 0);
            map.put(name, statCount);
            computeStatementMap(innerClasses.get(i).getInnerClasses(), map);
        }

        return map;
    }

    /**
     * 计算类的注释数目，包含类文档注释、属性文档注释、方法文档注释
     * @return
     */
    public Map<String, Integer> computeCommentMap() {
        final List<SClassDef> innerClasses = this.innerClasses;
        return computeCommentMap(innerClasses, null);
    }

    private Map<String, Integer> computeCommentMap(List<SClassDef> innerClasses, Map<String, Integer> map) {
        if (innerClasses == null)
            return map;

        if (map == null) {
            map = new HashMap<String, Integer>();
        }

        for (int i = 0;i < innerClasses.size();i++) {
            String name = innerClasses.get(i).getName();

            int commentCount = StringUtil.getLineOfString(innerClasses.get(i).getDocComment());

            List<SAttributeDef> attributes = innerClasses.get(i).getAttributes();
            for (int j = 0;j < attributes.size();j++) {
                commentCount += StringUtil.getLineOfString(attributes.get(j).getDocComment());
            }

            List<SMethodDef> methods = innerClasses.get(i).getMethods();
            for (int j = 0;j < methods.size();j++) {
                commentCount += StringUtil.getLineOfString(methods.get(j).getDocComment());
            }

            map.put(name, commentCount);
            computeCommentMap(innerClasses.get(i).getInnerClasses(), map);
        }
        return map;
    }

    /**
     * 计算类的loc
     * @return
     */
    public Map<String, Integer> computeClassLocMap() {
        final List<SClassDef> innerClasses = this.innerClasses;
        return computeClassLocMap(innerClasses, null);
    }

    public Map<String, Integer> computeClassLocMap(List<SClassDef> innerClasses, Map<String, Integer> map) {
        if (map == null) {
            map = new HashMap<String, Integer>();
        }

        for (int i = 0;i < innerClasses.size();i++) {
            computeClassLoc(innerClasses.get(i), map);
        }

        return map;
    }

    private Integer computeClassLoc(SClassDef sClassDef, Map<String, Integer> map) {
        if (sClassDef == null)
            return 0;

        String name = sClassDef.getName();
        int startPos = sClassDef.getStartPos();
        int endPos = sClassDef.getEndPos();
        int loc = StringUtil.getLoc(sClassDef.getDocComment() + "\n" + sClassDef.getSource().subSequence(startPos, endPos).toString());
        if (sClassDef.getInnerClasses() == null || sClassDef.getInnerClasses().size() == 0) {
            map.put(name, loc);
            return loc;
        }

        int innerLoc = 0;
        for (int i = 0;i < sClassDef.getInnerClasses().size();i++) {
            innerLoc += computeClassLoc(sClassDef.getInnerClasses().get(i), map);
        }
        loc-=innerLoc;
        map.put(name, loc);

        // 注意返回值还要加上innerLoc
        return loc+innerLoc;
    }

    /**
     * 计算每个方法的代码行数
     * 以map的形式返回结果
     * key=className.methodName
     * value为该方法的代码行数
     * @return
     */
    public Map<String, Integer> computeMethodLocMap() {
        final List<SClassDef> innerClasses = this.innerClasses;
        return computeMethodLocMap(innerClasses, null);
    }

    public Map<String, Integer> computeMethodLocMap(List<SClassDef> innerClasses, Map<String, Integer> map) {
        return computeForMethodMap(innerClasses, map, MCType.LOC);
    }

    /**
     * 计算每个方法的语句数
     * 以map的形式返回结果
     * key=className.methodName
     * value为该方法的语句数
     * @return
     */
    public Map<String, Integer> computeMethodStatementMap() {
        final List<SClassDef> innerClasses = this.innerClasses;
        return computeMethodStatementMap(innerClasses, null);
    }

    public Map<String, Integer> computeMethodStatementMap(List<SClassDef> innerClasses, Map<String, Integer> map) {
        return computeForMethodMap(innerClasses, map, MCType.STATEMENT);
    }

    /**
     * 计算每个方法的注释数
     * 以map的形式返回结果
     * key=className.methodName
     * value为该方法的注释数
     * @return
     */
    public Map<String, Integer> computeMethodCommentMap() {
        final List<SClassDef> innerClasses = this.innerClasses;
        return computeMethodCommentMap(innerClasses, null);
    }

    public Map<String, Integer> computeMethodCommentMap(List<SClassDef> innerClasses, Map<String, Integer> map) {
        return computeForMethodMap(innerClasses, map, MCType.COMMENT);
    }
    /**
     * 以方法作为单位进行计算
     * 可以计算方法loc, 语句数目, 注释数目
     * 通过第三个枚举参数指定具体计算方法
     * @param innerClasses
     * @param map
     * @param type
     * @return
     */
    private Map<String, Integer> computeForMethodMap(List<SClassDef> innerClasses, Map<String, Integer> map, MCType type) {
        if (innerClasses == null)
            return map;

        if (map == null) {
            map = new HashMap<String, Integer>();
        }

        for (int i = 0;i < innerClasses.size();i++) {
            String className = innerClasses.get(i).getName();

            List<SMethodDef> methods = innerClasses.get(i).getMethods();
            for (int j = 0;j < methods.size();j++) {
                String methodName = methods.get(j).getName();
                int startPos = methods.get(j).getStartPos();
                int endPos = methods.get(j).getEndPos();
                String parameters = methods.get(j).getParametersString();
                int count = 0;
                switch (type) {
                    case LOC:
                        count = StringUtil.getLoc(methods.get(j).docComment + "\n"
                                + methods.get(j).getSource().subSequence(startPos, endPos).toString());
                        break;
                    case COMMENT:
                        count = StringUtil.getLineOfString(methods.get(j).docComment);
                        break;
                    case STATEMENT:
                        count = methods.get(j).getStatementSize();
                        break;
                    default:
                        throw new IllegalArgumentException("unsupported type " + type);
                }
                map.put(className + "." + methodName + "(" + parameters  + ")", count);
            }
            computeForMethodMap(innerClasses.get(i).getInnerClasses(), map, type);
        }

        return map;
    }

    /**
     * 表示对于方法的计算类型
     */
    private enum MCType {
        /**
         * 计算方法中代码行数目
         */
        LOC,

        /**
         * 计算方法中语句数目
         */
        STATEMENT,

        /**
         * 计算方法注释数目
         */
        COMMENT,
    }
}
