package node;

/**
 * 条件节点
 */
public class ConditionalNode extends Node {

    protected ConditionalNode() {
        super();
    }

    /**
     * 条件节点可以有至少两个节点，不进行限制
     * @param node
     */
    @Override
    public void addNode(Node node) {
        children.add(node);
    }

    @Override
    public int computeNodes() {
        Node m1 = children.get(0);
        Node m2 = null;
        if (children.size() == 2)
            m2 = children.get(1);
        return m1.computeNodes() + (m2 != null ? m2.computeNodes() : 0);
    }

    @Override
    public int computeEdges() {
        Node m1 = children.get(0);
        Node m2 = null;
        if (children.size() == 2)
            m2 = children.get(1);
        return m1.computeEdges() + (m2 != null ? m2.computeEdges() : 0)  + 2;
    }

    @Override
    public int computeCyclomaticComplexity() {
        Node m1 = children.get(0);
        Node m2 = null;
        if (children.size() == 2)
            m2 = children.get(1);
        return m1.computeCyclomaticComplexity() + (m2 != null ? m2.computeCyclomaticComplexity() : 0)  + 1;
    }
}