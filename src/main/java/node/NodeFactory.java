package node;

/**
 * 抽象工厂，用于实例化Node类
 * 产品族包括原子节点、顺序节点、循环节点、条件节点
 */
public abstract class NodeFactory {

    /**
     * 通过工厂类实例化一个原子节点
     * @return
     */
    public abstract AtomicNode getAtomicNode();

    /**
     * 通过工厂类实例化一个顺序节点
     * @return
     */
    public abstract SequenceNode getSequenceNode();

    /**
     * 通过工厂类实例化一个条件节点
     * @return
     */
    public abstract ConditionalNode getConditionalNode();

    /**
     * 通过工厂类实例化一个循环节点
     * @return
     */
    public abstract LoopNode getLoopNode();
}