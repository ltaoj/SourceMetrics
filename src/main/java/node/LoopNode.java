package node;

/**
 * 循环节点
 */
public class LoopNode extends Node {

    private static final int MAX_NODE = 1;

    protected LoopNode() {
        super();
    }

    /**
     * 循环节点最多只能有一个节点
     * 当调用该方法时，如果子节点个数大于等于限制的最大节点个数，抛出异常
     * @param node
     */
    @Override
    public void addNode(Node node) {
        if (children.size() >= MAX_NODE)
            throw new IllegalStateException("循环节点只能有一个子节点");
        children.add(node);
    }

    @Override
    public int computeNodes() {
        Node m1 = children.get(0);
        return m1.computeNodes() + 1;
    }

    @Override
    public int computeEdges() {
        Node m1 = children.get(0);
        return m1.computeEdges() + 2;
    }

    @Override
    public int computeCyclomaticComplexity() {
        Node m1 = children.get(0);
        return m1.computeCyclomaticComplexity() + 1;
    }
}