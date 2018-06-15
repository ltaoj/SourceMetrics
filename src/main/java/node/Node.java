package node;

import java.util.LinkedList;
import java.util.List;

/**
 * 用于构建方法结构的抽象节点
 * 节点有四种类型、分别为原子节点、顺序节点、分支节点、循环节点
 * 节点的功能有添加子节点、计算当前节点值、计算当前边值
 */
public abstract class Node {

    /**
     * 用于保存子节点
     */
    protected final List<Node> children;

    /**
     * 初始化子节点容器
     */
    public Node() {
        children = new LinkedList<Node>();
    }

    /**
     * 将节点添加到容器中
     * 具体添加规则由子类定义
     * 比如原子节点不支持添加子节点
     * 顺序节点有且只有两个子节点
     * 分支节点可以有多个子节点
     * 循环节点有一个子节点
     * @param node
     */
    public abstract void addNode(Node node);

    /**
     * 计算当前的节点值
     * 不同类型节点有不同的计算规则
     * 规则一：Fa() = 2
     * 规则二：Fs(m1, m2) = m1 + m2 - 1
     * 规则三：Fc(m1, m2) = m1 + m2
     * 规则四：Fl(m1) = m1 + 1
     * 子类实现各自的计算规则
     * @return
     */
    public abstract int computeNodes();

    /**
     * 计算当前节点的边值
     * 不同类型节点有不同的计算规则
     * 规则一：Fa() = 1
     * 规则二：Fs(m1, m2) = m1 + m2
     * 规则三：Fc(m1, m2) = m1 + m2 + 2
     * 规则四：Fl(m1) = m1 + 2
     * 子类实现各自的计算规则
     * @return
     */
    public abstract int computeEdges();

    /**
     * 计算当前节点在节点树中的圈复杂度
     * 不同类型节点有不同的计算规则
     * 规则一：Fa() = 0
     * 规则二：Fs(m1, m2) = m1 + m2
     * 规则三：Fc(m1, m2) = m1 + m2 + 1
     * 规则四：Fl(m1) = m1 + 1
     * @return
     */
    public abstract int computeCyclomaticComplexity();
}