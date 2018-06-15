package node;

/**
 * 工厂类用于创建Node，主要通过反射实现
 */
public class ReflectNodeFactory extends NodeFactory {

    @Override
    public AtomicNode getAtomicNode() {
        return new AtomicNode();
    }

    @Override
    public SequenceNode getSequenceNode() {
        return new SequenceNode();
    }

    @Override
    public ConditionalNode getConditionalNode() {
        return new ConditionalNode();
    }

    @Override
    public LoopNode getLoopNode() {
        return new LoopNode();
    }
}