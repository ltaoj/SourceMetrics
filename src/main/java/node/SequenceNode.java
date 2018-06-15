package node;

/**
 * 顺序节点
 */
public class SequenceNode extends Node {

    private static final int MAX_NODE = 2;

    /**
     * 节点被修改的次数
     */
    private int mod = 0;

    protected SequenceNode() {
        super();
    }

    /**
     * 顺序节点只有两个节点，所以添加的节点个数不能大于两个
     * 当超过两个节点再进行添加时，裂变出新的顺序节点
     * 比如原来的结构是这样的
     * 1.
     *              S
     *            -    -
     *          A       A
     *          当再添加一个L节点时
     *          结构变成2
     * 2.
     *              S
     *            -    -
     *          A       S
     *                -    -
     *              A       L
     *          此时再添加一个C节点
     *          结构变成3
     * 3.
     *              S
     *            -    -
     *          A       S
     *                -    -
     *              A       S
     *                    -    -
     *                  L       C
     * @param node
     */
    @Override
    public void addNode(Node node) throws IllegalStateException{
        if (children.size() >= MAX_NODE) {
            synchronized (SequenceNode.class) {
                Node seqNode = this;
                for (int i = 0;i < mod;i++) {
                    seqNode = seqNode.children.get(MAX_NODE-1);
                }
                Node tailNode = seqNode.children.remove(MAX_NODE-1);
                Node newSeqNode = new SequenceNode();
                seqNode.addNode(newSeqNode);
                newSeqNode.addNode(tailNode);
                newSeqNode.addNode(node);
                mod++;
            }
        }

        children.add(node);
    }

    @Override
    public int computeNodes() {
        Node m1 = children.get(0);
        Node m2 = children.get(1);
        return m1.computeNodes() + m2.computeNodes() - 1;
    }

    @Override
    public int computeEdges() {
        Node m1 = children.get(0);
        Node m2 = children.get(1);
        return m1.computeEdges() + m2.computeEdges();
    }

    @Override
    public int computeCyclomaticComplexity() {
        Node m1 = children.get(0);
        Node m2 = children.get(1);
        return m1.computeCyclomaticComplexity() + m2.computeCyclomaticComplexity();
    }
}