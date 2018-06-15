package node;

/**
 * Created by ltaoj on 2018/06/15 21:55.
 *
 * @version : 1.0
 */
public class DummyNode extends Node {

    private static final int MAX_NODE = 1;

    public DummyNode() {
        super();
    }
    
    @Override
    public void addNode(Node node) {
        if (children.size() >= 1)
            throw new IllegalStateException("dummy node只能添加一个节点");

        if (node instanceof DummyNode)
            throw new IllegalArgumentException("不支持添加节点类型" + node.getClass().getName());

        children.add(0, node);
    }

    @Override
    public int computeNodes() {
        return children.get(0) != null ? children.get(0).computeNodes() : 0;
    }

    @Override
    public int computeEdges() {
        return children.get(0) != null ? children.get(0).computeEdges() : 0;
    }

    @Override
    public int computeCyclomaticComplexity() {
        return children.get(0) != null ? children.get(0).computeCyclomaticComplexity() : 0;
    }
}
