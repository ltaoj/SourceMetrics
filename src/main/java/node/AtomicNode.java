package node;

/**
 * 原子节点
 */
public class AtomicNode extends Node {

    protected AtomicNode() {
        super();
    }

    /**
     * 原子节点没有子节点
     * 当调用方法时，将会抛出异常
     * @param node
     */
    @Override
    public void addNode(Node node) {
        throw new UnsupportedOperationException("原子节点没有子节点");
    }

    @Override
    public int computeNodes() {
        return 2;
    }

    @Override
    public int computeEdges() {
        return 1;
    }

    @Override
    public int computeCyclomaticComplexity() {
        return 0;
    }
}