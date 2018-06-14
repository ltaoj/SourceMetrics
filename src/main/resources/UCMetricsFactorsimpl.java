package algorithm;

import java.util.List;

/**
 * Created by ltaoj on 2018/05/25 01:18.
 *
 * @version : 1.0
 */
public class UCMetricsFactorsimpl extends UCMetricsFactors {

    private static final double KU = 1.4;

    private static final double K1 = 0.1;

    private static final double K2 = 0.1;

    private int c[][];

    private int d[][];

    private int e[][];

    private int actorSize;

    private int useCaseSize;

    public UCMetricsFactorsimpl(UseCaseDiagram diagram) {
        super(diagram);
        prepare();
    }

    public int getUC1() {
        List<UCUseCase> useCases = diagram.getUseCases();
        return useCases != null ? useCases.size() : 0;
    }

    public int getUC2() {
        List<UCUseCaseAssociation> useCaseAssociations = diagram.getUseCaseAssociations();
        return useCaseAssociations != null ? useCaseAssociations.size() : 0;
    }

    public double getUC3() {
        // 检查是否有extend关系
        boolean hasExtend = false;
        for (int i = 0;i < diagram.getDependencies().size();i++) {
            if (diagram.getDependencies().get(i).getStereotype().equals(UCDependency.Stereotype.EXTEND)) {
                hasExtend = true;
                break;
            }
        }
        // 没有extend关系时，UC3 = UC2
        if (!hasExtend)
            return getUC2();

        List<UCActor> actors = diagram.getActors();
        List<UCUseCase> useCases = diagram.getUseCases();
        List<UCUseCaseAssociation> useCaseAssociations = diagram.getUseCaseAssociations();

        // 首先计算矩阵c
        for (int i = 0;i < useCaseAssociations.size();i++) {
            UCUseCase useCase = diagram.getUseCase(useCaseAssociations.get(i).getUseCase().getId());
            UCActor actor = diagram.getActor(useCaseAssociations.get(i).getActor().getId());

            int ucIndex = useCases.indexOf(useCase);
            int atIndex = actors.indexOf(actor);

            c[ucIndex][atIndex] = 1;
        }

        copyCD();

        // 计算v
        int v[][] = new int[useCaseSize][useCaseSize];

        // 在c的基础上计算d,并计算v
        List<UCDependency> dependencies = diagram.getDependencies();
        for (int i = 0;i < dependencies.size();i++) {
            if (dependencies.get(i).getStereotype().equals(UCDependency.Stereotype.EXTEND)) {
                UCUseCase origin = diagram.getUseCase(dependencies.get(i).getObject1().getId());
                UCUseCase extended = diagram.getUseCase(dependencies.get(i).getObject2().getId());

                int ogIndex = useCases.indexOf(origin);
                int etIndex = useCases.indexOf(extended);

                for (int j = 0;j < actors.size();j++)  {
                    if (c[ogIndex][j] == 1) {
                        d[ogIndex][j] = Math.max(0, c[ogIndex][j]-c[etIndex][j]);
                    }
                }
            } else if (dependencies.get(i).getStereotype().equals(UCDependency.Stereotype.INCLUDE)) {
                UCUseCase origin = diagram.getUseCase(dependencies.get(i).getObject2().getId());
                UCUseCase included = diagram.getUseCase(dependencies.get(i).getObject1().getId());

                int ogIndex = useCases.indexOf(origin);
                int icIndex = useCases.indexOf(included);
                v[ogIndex][icIndex] = 1;
            }
        }

        double uc3 = 0;
        // 在d基础上计算e
        for (int i = 0;i < useCaseSize;i++) {
//            int eSum = 0;
            for (int j = 0;j < actorSize;j++) {
                if (d[i][j] == 0)
                    e[i][j] = 0;
                else {
                    // j这里可以看做是k变量，k变量不能大于useCaseSize的最大下标
                    boolean hasK = false;
                    if (j < useCaseSize && v[i][j] == 1) {
                        e[i][j] = 0;
                        hasK = true;
                    }

                    if (!hasK) {
                        e[i][j] = 1;
//                        eSum++;
                    }
                }
            }
//            uc3 += Math.pow(eSum, KU);
        }

        for (int j = 0;j < actorSize;j++) {
            int eSum = 0;
            for (int i = 0;i < useCaseSize;i++) {
                eSum+=e[i][j];
            }
            uc3 += Math.pow(eSum, KU);
        }
        return Math.round(uc3*100)/100.0;
    }

    public double getUC4() {
        int sumC = 0, sumE = 0;
        for (int i = 0;i < useCaseSize;i++) {
            for (int j = 0;j < actorSize;j++) {
                sumC += c[i][j];
                sumE += e[i][j];
            }
        }

        return ((int)(K1*Math.pow(getUC1(), 2) + getUC3() + K2*(sumC-sumE))*100)/100.0;
    }

    public int getNA() {
        List<UCActor> actors = diagram.getActors();
        return actors != null ? actors.size() : 0;
    }

    public int getNOD() {
        List<UCDependency> dependencies = diagram.getDependencies();
        return dependencies != null ? dependencies.size() : 0;
    }

    @Override
    public void setDiagram(UseCaseDiagram diagram) {
        super.setDiagram(diagram);
        prepare();
    }

    /**
     * 将c计算的数据拷贝到d中
     */
    private void copyCD() {
        if (c == null || d == null)
            throw new IllegalStateException("c and d array must not be null.");

        if (c.length != d.length || c[0].length != d[0].length)
            throw new IllegalStateException("c and d array must has the same size.");

        for (int i = 0;i < c.length;i++) {
            for (int j = 0;j < c[0].length;j++) {
                d[i][j] = c[i][j];
            }
        }
    }

    /**
     * 初始化相关数组大小
     * 当调用构造函数或者重新设置diagram变量时，执行此函数
     */
    private void prepare() {
        if (diagram == null)
            throw new IllegalStateException("diagram must be set before prepare.");

        reset();

        int col = diagram.getActors() != null ? diagram.getActors().size() : 1;
        int row = diagram.getUseCases() != null ? diagram.getUseCases().size() : 1;
        c = new int[row][col];
        d = new int[row][col];
        e = new int[row][col];

        // 初始化矩阵元素为0
        for (int i = 0;i < row;i++) {
            for (int j = 0;j < col;j++) {
                c[i][j] = 0;
                d[i][j] = 0;
                e[i][j] = 0;
            }
        }

        useCaseSize = row;
        actorSize = col;
    }

    // for gc
    private void reset() {
        c = null;
        d = null;
        e = null;
        useCaseSize = 0;
        actorSize = 0;
    }
}
