package parse;

import bean.SAttributeDef;
import bean.SClassDef;
import bean.SMethodDef;
import com.sun.imageio.plugins.jpeg.JPEG;
import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.parser.Parser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.DocCommentTable;
import com.sun.tools.javac.tree.EndPosTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import node.DummyNode;
import node.Node;
import node.NodeFactory;
import node.ReflectNodeFactory;
import org.apache.log4j.Logger;
import util.StringUtil;

import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by ltaoj on 2018/06/13 19:58.
 *
 * @version : 1.0
 */
public class SourceParser<R, P> {

    private static final Logger logger = Logger.getLogger(SourceParser.class);

    private Context mContext;
    private ParserFactory mParserFactory;
    private JavacFileManager mJavacFileManager;
    private Parser mParser;
    private String mJavaFile;

    public SourceParser() {
        mContext = new Context();
        mJavacFileManager = new JavacFileManager(mContext, true, Charset.forName("UTF-8"));
        mParserFactory = ParserFactory.instance(mContext);
    }


    public SourceParser target(String javaFile) {
        this.mJavaFile = javaFile;
        return this;
    }

    /**
     * 统计java文件代码行
     * 去除空行, 计算注释
     * @return
     */
    public int parseLoc() {
        return parseLoc(false);
    }

    /**
     * 统计java文件代码行
     * @param ignoreDocComment 表示是否忽略多行注释、文档注释
     * @return
     */
    public int parseLoc(boolean ignoreDocComment) {
        JavaFileObject javaFileObject = getJavaFileObject();
        int loc = 0;
        try {
            Reader sourceReader = javaFileObject.openReader(true);
            BufferedReader br = new BufferedReader(sourceReader);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line + "\n");
            }
            loc = StringUtil.getLoc(builder.toString(), ignoreDocComment);
        } catch (IOException e) {
            logger.error("文件不存在 " + e.getMessage());
        }
        return loc;
    }

    public List<String> parseDocComment(CountComment scanner) {
        JavaFileObject javaFileObject = getJavaFileObject();
        try {
            CharSequence source = javaFileObject.getCharContent(true);
            mParser = mParserFactory.newParser(source, true, true, true);
            JCTree.JCCompilationUnit structure = mParser.parseCompilationUnit();
            scanner.setTable(structure.docComments);
            scanner.visitTopLevel(structure);
            return scanner.getComments();
        } catch (IOException e) {
            logger.error("文件不存在 " + e.getMessage());
        }
        return null;
    }

    public R parse(TreeScanner<R, P> treeScanner) {
        return parse(treeScanner, null);
    }

    public R parse(TreeScanner<R, P> treeScanner, P def) {
        JavaFileObject javaFileObject = getJavaFileObject();
        try {
            CharSequence source = javaFileObject.getCharContent(true);
            mParser = mParserFactory.newParser(source, true, true, true);
            JCTree.JCCompilationUnit structure = mParser.parseCompilationUnit();
            if (treeScanner instanceof JFileScanner) {
                ((JFileScanner) treeScanner).setEndPosTable(structure.endPositions);
                ((JFileScanner) treeScanner).setSource(source);
                ((JFileScanner) treeScanner).setDocCommentTable(structure.docComments);
            }
            R res = treeScanner.visitCompilationUnit(structure, def);
            return res;
        } catch (IOException e) {
            logger.error("文件不存在" + e.getMessage());
        }
        return null;
    }

    private JavaFileObject getJavaFileObject() {
        return mJavacFileManager.getJavaFileObjects(mJavaFile).iterator().next();
    }

    /**
     * 计算语句个数
     */
    public static class CountStatement extends TreeScanner<List<String>, List<String>> {

        @Override
        public List<String> visitExpressionStatement(ExpressionStatementTree node, List<String> strings) {
            List<String> str = new ArrayList<String>();
            str.add(String.valueOf(node));
            return str;
        }

        @Override
        public List<String> visitVariable(VariableTree node, List<String> list) {
            List<String> str = new ArrayList<String>();
            str.add(String.valueOf(node));
            return str;
        }

        @Override
        public List<String> visitReturn(ReturnTree node, List<String> list) {
            List<String> str = new ArrayList<String>();
            str.add(String.valueOf(node));
            return str;
        }

        @Override
        public List<String> reduce(List<String> r1, List<String> r2) {
            if (r2 == null)
                r2 = new ArrayList<String>();
            if (r1 != null)
                r2.addAll(r1);
            return r2;
        }
    }

    /**
     * 计算类的个数
     */
    public static class CountClass extends TreeScanner<Integer, Integer> {

        @Override
        public Integer visitClass(ClassTree node, Integer integer) {
            return reduce(scan(node.getMembers(), 0), 1);
        }

        @Override
        public Integer reduce(Integer r1, Integer r2) {
            return (r1 == null ? 0 : r1) + (r2 == null ? 0 : r2);
        }
    }

    /**
     * 计算文档注释个数
     */
    public static class CountComment extends com.sun.tools.javac.tree.TreeScanner {

        /**
         * 文档表
         */
        private DocCommentTable table;

        private List<String> comments;

        public CountComment() {
            this.comments = new ArrayList<String>();
        }

        @Override
        public void visitMethodDef(JCTree.JCMethodDecl tree) {
            hasCommentAdded(tree);
            super.visitMethodDef(tree);
        }

        @Override
        public void visitClassDef(JCTree.JCClassDecl tree) {
            hasCommentAdded(tree);
            super.visitClassDef(tree);
        }

        @Override
        public void visitVarDef(JCTree.JCVariableDecl tree) {
            hasCommentAdded(tree);
            super.visitVarDef(tree);
        }

        private void hasCommentAdded(JCTree tree) {
            if (table.hasComment(tree)) {
                this.comments.add(table.getCommentText(tree));
            }
        }

        public void setTable(DocCommentTable table) {
            this.table = table;
        }

        public List<String> getComments() {
            return comments;
        }
    }

    /**
     * 扫描成Java对象
     */
    public static class JFileScanner extends TreeScanner<Map<String, SClassDef>, SClassDef> {

        private EndPosTable mEndPosTable;
        private CharSequence source;
        private DocCommentTable mDocCommentTable;

        public void setEndPosTable(EndPosTable endPosTable) {
            this.mEndPosTable = endPosTable;
        }

        public void setSource(CharSequence source) {
            this.source = source;
        }

        public void setDocCommentTable(DocCommentTable docCommentTable) {
            this.mDocCommentTable = docCommentTable;
        }

        @Override
        public Map<String, SClassDef> visitCompilationUnit(CompilationUnitTree node, SClassDef sClassDef) {
            List<JCTree> typeDefs = (List<JCTree>) node.getTypeDecls();
            Map<String, SClassDef> map = null;
            for (JCTree jcTree : typeDefs) {
                if (jcTree instanceof JCTree.JCClassDecl) {
                    sClassDef.setInnerTempDocComment(mDocCommentTable.getCommentText(jcTree));
                    sClassDef.setInnerTempStartPos(jcTree.getStartPosition());
                    sClassDef.setInnerTempEndPos(mEndPosTable.getEndPos(jcTree));
                    map = scan(jcTree, sClassDef);
                    break;
                }
            }
            return map;
        }

        @Override
        public Map<String, SClassDef> visitClass(ClassTree node, SClassDef outerClass) {
            SClassDef sClassDef = new SClassDef(source);
            // 解析类修饰符
            JCTree.JCModifiers modifiers = (JCTree.JCModifiers) node.getModifiers();
            sClassDef.setModifiers(getModifiers(modifiers));
            // 解析继承的类名称
            if (node.getExtendsClause() != null){
                sClassDef.setExtendingClass(node.getExtendsClause().toString());
            }
            // 解析实现的接口名称
            if (node.getImplementsClause() != null) {
                sClassDef.setImplementingClass(getImplementsClause((List<JCTree.JCExpression>) node.getImplementsClause()));
            }
            // 解析类名
            sClassDef.setName(node.getSimpleName().toString());
            // 解析外部类
            sClassDef.setOuterClass(outerClass);
            if (outerClass.getInnerClasses() == null) {
                outerClass.setInnerClasses(new ArrayList<SClassDef>());
            }
            outerClass.getInnerClasses().add(sClassDef);
            // 设置类的文档注释
            sClassDef.setDocComment(outerClass.getInnerTempDocComment());
            // 设置类的开始、结束位置
            sClassDef.setStartPos(outerClass.getInnerTempStartPos());
            sClassDef.setEndPos(outerClass.getInnerTempEndPos());

            List<SAttributeDef> attributeDefs = new ArrayList<SAttributeDef>();
            List<SMethodDef> methodDefs = new ArrayList<SMethodDef>();
//            stringListMap.put(node.getSimpleName().toString(), attrs);
            final List<JCTree> members = (List<JCTree>) node.getMembers();
            final List<JCTree> classes = new ArrayList<JCTree>();
            // 解析类的属性
            for (int i = 0;i < members.size();i++) {
                JCTree jcTree = members.get(i);
                if (jcTree instanceof JCTree.JCVariableDecl) {
                    SAttributeDef attrDef = new SAttributeDef(source);
                    attrDef.setStartPos(jcTree.getStartPosition());
                    attrDef.setEndPos(mEndPosTable.getEndPos(jcTree));
                    if (((JCTree.JCVariableDecl) jcTree).getModifiers() != null) {
                        attrDef.setModifiers(getModifiers(((JCTree.JCVariableDecl) jcTree).getModifiers()));
                    }
                    attrDef.setName(((JCTree.JCVariableDecl) jcTree).getName().toString());
                    attrDef.setVarType(((JCTree.JCVariableDecl) jcTree).getType().toString());
                    attrDef.setDocComment(mDocCommentTable.getCommentText(jcTree));
                    attributeDefs.add(attrDef);
                } else if (jcTree instanceof JCTree.JCClassDecl) {
                    classes.add(jcTree);
                } else if (jcTree instanceof JCTree.JCMethodDecl) {
                    SMethodDef methodDef = new SMethodDef(source);
                    methodDef.setStartPos(jcTree.getStartPosition());
                    methodDef.setEndPos(mEndPosTable.getEndPos(jcTree));
                    methodDef.setName(((JCTree.JCMethodDecl) jcTree).getName().toString());
                    // 修饰符
                    if (((JCTree.JCMethodDecl) jcTree).getModifiers() != null) {
                        methodDef.setModifiers(getModifiers(((JCTree.JCMethodDecl) jcTree).getModifiers()));
                    }
                    JCTree resType = ((JCTree.JCMethodDecl) jcTree).getReturnType();
                    // 返回值resType为null,则为构造参数
                    if (resType != null) {
                        methodDef.setResType(resType.toString());
                    }
                    // 方法文档注释
                    if (mDocCommentTable.hasComment(jcTree)) {
                        methodDef.setDocComment(mDocCommentTable.getCommentText(jcTree));
                    }
                    // 方法块内容
                    JCTree body = ((JCTree.JCMethodDecl) jcTree).getBody();
                    if (body != null) {
                        methodDef.setBody(body.toString());
                    }
                    // 方法语句数目以及方法结构*******
                    SClassDef tempClass = new SClassDef(null);
                    scan(jcTree, tempClass);
//                    List<JCTree.JCStatement> statements = ((JCTree.JCBlock) body).getStatements();
                    if (tempClass.getModifiers() != null) {
                        methodDef.setStatementSize(tempClass.getModifiers().size());
                        methodDef.setStrucNode(tempClass.getMethodTempNode());
                        // for gc
                        tempClass.setModifiers(null);
                        tempClass = null;
                    }

                    // 方法参数列表
                    List<JCTree.JCVariableDecl> params = ((JCTree.JCMethodDecl) jcTree).getParameters();
                    if (params != null) {
                        methodDef.setParameters(getParameters(params));
                    }
                    methodDefs.add(methodDef);
                }
            }
            // 解析类的属性、方法完毕
            sClassDef.setAttributes(attributeDefs);
            sClassDef.setMethods(methodDefs);

            Map<String, SClassDef> map = new HashMap<String, SClassDef>();
            map.put(node.getSimpleName().toString(), sClassDef);
            // 解析内部类
            for (int i = 0;i < classes.size();i++) {
                // 暂时设置
                sClassDef.setInnerTempDocComment(mDocCommentTable.getCommentText(classes.get(i)));
                sClassDef.setInnerTempStartPos(classes.get(i).getStartPosition());
                sClassDef.setInnerTempEndPos(mEndPosTable.getEndPos(classes.get(i)));
                scan(classes.get(i), sClassDef);
            }
            return map;
        }

        @Override
        public Map<String, SClassDef> visitMethod(MethodTree node, SClassDef sClassDef) {
            // 对方法进行语句扫描
            CountStatement statementScanner = new CountStatement();
            List<String> statementList = statementScanner.visitMethod(node, null);
            // 对方法结构进行构建
            final DummyNode dummyNode = new DummyNode();
            StrucMethodScanner strucMethodScanner = new StrucMethodScanner();
            strucMethodScanner.visitMethod(node, dummyNode);

            sClassDef.setModifiers(statementList);
            sClassDef.setMethodTempNode(dummyNode);
            return null;
        }

        private List<String> getModifiers(JCTree.JCModifiers jcModifiers) {
            List<String> modifiers = new ArrayList<String>();
            Set<Modifier> modSet = jcModifiers.getFlags();
            Iterator<Modifier> iterator = modSet.iterator();
            while (iterator.hasNext()) {
                modifiers.add(iterator.next().toString());
            }
            return modifiers;
        }

        private List<String> getImplementsClause(List<JCTree.JCExpression> implementsClause) {
            List<String> list = new ArrayList<String>();
            for (JCTree.JCExpression implement : implementsClause) {
                list.add(implement.toString());
            }
            return list;
        }

        private List<SAttributeDef> getParameters(List<JCTree.JCVariableDecl> params) {
            List<SAttributeDef> list = new ArrayList<SAttributeDef>();
            for (JCTree.JCVariableDecl param : params) {
                SAttributeDef attr = new SAttributeDef(source);
                attr.setName(param.getName().toString());
                attr.setModifiers(getModifiers(param.getModifiers()));
                attr.setVarType(param.getType().toString());
                attr.setStartPos(param.getStartPosition());
                attr.setEndPos(mEndPosTable.getEndPos(param));
                list.add(attr);
            }
            return list;
        }
    }

    public static class StrucMethodScanner extends TreeScanner<Void, Node> {

        private NodeFactory mNodeFactory;

        public StrucMethodScanner() {
            this.mNodeFactory = new ReflectNodeFactory();
        }

        @Override
        public Void visitMethod(MethodTree node, Node node2) {
            BlockTree blockTree = node.getBody();

            if (blockTree != null) {
                scan(blockTree, node2);
            }else {
                System.out.println("方法块为空");
            }
            return null;
        }

        /**
         * 当访问代码块的时候，
         * 1.如果只有一个语句，那么直接扫描该语句即可，参数值直接传递给下一级
         * 2.如果只有两个语句，那么需要创建一个顺序节点，将该顺序节点添加到参数值中，然后扫描每一个语句，
         * 并且以新的顺序节点作为参数值传递给下一层
         * @param node
         * @param node2
         * @return
         */
        @Override
        public Void visitBlock(BlockTree node, Node node2) {
            List<JCTree.JCStatement> statements = (List<JCTree.JCStatement>) node.getStatements();

            if (statements != null) {
                if (statements.size() == 1) {
                    scan(statements.get(0), node2);
                } else if (statements.size() > 1) {
                    Node seqNode = mNodeFactory.getSequenceNode();
                    node2.addNode(seqNode);
                    scan(statements, seqNode);
                }
            } else {
                System.out.println("代码块为空,没有语句");
            }

            return null;
        }

        /**
         * 访问表达式语句的结构时
         * 假装认为可以作为一个原子节点
         * @param node
         * @param node2
         * @return
         */
        @Override
        public Void visitExpressionStatement(ExpressionStatementTree node, Node node2) {
            Node atoNode = mNodeFactory.getAtomicNode();
            node2.addNode(atoNode);
            return null;
        }

        /**
         * 访问变量声明的结构时
         * 可以作为原子节点条件如下
         * 1.当变量声明没有初始化
         * 2.变量初始化的值为字面量
         * 3.变量初始化的值为标识符
         * @param node
         * @param node2
         * @return
         */
        @Override
        public Void visitVariable(VariableTree node, Node node2) {
            if (node.getInitializer() == null) {
                Node atoNode = mNodeFactory.getAtomicNode();
                node2.addNode(atoNode);
            } else {
                boolean isAtomic = false;
                if (node.getInitializer() instanceof JCTree.JCLiteral)
                    isAtomic = true;
                else if (node.getInitializer() instanceof JCTree.JCIdent)
                    isAtomic = false;
                else if (node.getInitializer() instanceof JCTree.JCExpression)
                    isAtomic = true;

                if (isAtomic) {
                    Node atoNode = mNodeFactory.getAtomicNode();
                    node2.addNode(atoNode);
                } else {
                    // 这里另外解析
                }
            }
            return null;
        }

        @Override
        public Void visitReturn(ReturnTree node, Node node2) {
            Node atoNode = mNodeFactory.getAtomicNode();
            node2.addNode(atoNode);
            return null;
        }

        /**
         * 当访问while循环结构时
         * 可以作为一个循环节点
         * @param node
         * @param node2
         * @return
         */
        @Override
        public Void visitWhileLoop(WhileLoopTree node, Node node2) {
            Node loopNode = mNodeFactory.getLoopNode();
            node2.addNode(loopNode);
            if (node.getStatement() != null) {
                scan(node.getStatement(), loopNode);
            }
            return null;
        }

        /**
         * 当访问for循环时
         * 可以作为一个循环节点
         * @param node
         * @param node2
         * @return
         */
        @Override
        public Void visitForLoop(ForLoopTree node, Node node2) {
            Node loopNode = mNodeFactory.getLoopNode();
            node2.addNode(loopNode);
            if (node.getStatement() != null) {
                scan(node.getStatement(), loopNode);
            }
            return null;
        }

        /**
         * 当访问do-while循环时
         * 可以作为一个循环节点
         * @param node
         * @param node2
         * @return
         */
        @Override
        public Void visitDoWhileLoop(DoWhileLoopTree node, Node node2) {
            Node loopNode = mNodeFactory.getLoopNode();
            node2.addNode(loopNode);
            if (node.getStatement() != null) {
                scan(node.getStatement(), loopNode);
            }
            return null;
        }

        /**
         * 当访问一个for (String str : list) {}循环时
         * 可以作为一个循环节点
         * @param node
         * @param node2
         * @return
         */
        @Override
        public Void visitEnhancedForLoop(EnhancedForLoopTree node, Node node2) {
            Node loopNode = mNodeFactory.getLoopNode();
            node2.addNode(loopNode);
            if (node.getStatement() != null) {
                scan(node.getStatement(), loopNode);
            }
            return null;
        }

        /**
         * 当访问一个if-else结构时
         * 可以作为一个条件节点
         * @param node
         * @param node2
         * @return
         */
        @Override
        public Void visitIf(IfTree node, Node node2) {
            Node conNode = mNodeFactory.getConditionalNode();
            node2.addNode(conNode);
            scan(node.getThenStatement(), conNode);
            if (node.getElseStatement() != null) {
                scan(node.getElseStatement(), conNode);
            }
            return null;
        }

        /**
         * 当访问到一个switch分支时
         * 为简单起见
         * 这里先作为一个原子节点处理
         * @param node
         * @param node2
         * @return
         */
        @Override
        public Void visitSwitch(SwitchTree node, Node node2) {
            Node atoNode = mNodeFactory.getAtomicNode();
            node2.addNode(atoNode);
            return null;
        }

        /**
         * 当访问到一个throw语句时
         * 可以作为一个原子语句
         * @param node
         * @param node2
         * @return
         */
        @Override
        public Void visitThrow(ThrowTree node, Node node2) {
            Node atoNode = mNodeFactory.getAtomicNode();
            node2.addNode(atoNode);
            return null;
        }

    }

}
