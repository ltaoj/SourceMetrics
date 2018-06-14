package parse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltaoj on 2018/05/23 17:12.
 *
 * @version : 1.0
 */
public class UseCaseParser extends DiagramParser<UseCaseDiagram> {

    Logger logger = Logger.getLogger(UseCaseParser.class);

    public void parse(String file, DataListener<UseCaseDiagram> listener) throws ParseException {
        Document document = readXml(file);
        if (document == null)
            throw new ParseException("文件不存在，解析失败.");

        Element rootEl = document.getRootElement();

        UseCaseDiagram useCaseDiagram = new UseCaseDiagram();
        parseInternal(rootEl, useCaseDiagram, 0);

        listener.onComplete(useCaseDiagram);
    }

    private void parseInternal(Element rootEl, UseCaseDiagram useCaseDiagram, int level) {
        List<Element> children = rootEl.elements();
        if (level < 3) {
            parseInternal(children.get(0), useCaseDiagram, level+1);
            return;
        }

        Element dependenciesEl = null;
        Element actorsEl = null;
        Element useCasesEl = null;
        Element useCaseAssociationsEl= null;

        for (int i = 0;i < children.size();i++) {
            Element element = children.get(i);
            if (element.getName().equals(TagsName.DEPENDENCIES))
                dependenciesEl = element;
            else if (element.getName().equals(TagsName.ACTORS))
                actorsEl = element;
            else if (element.getName().equals(TagsName.USECASES))
                useCasesEl = element;
            else if (element.getName().equals(TagsName.USECASEASSOCIATIONS))
                useCaseAssociationsEl = element;
        }

        parseActors(actorsEl, useCaseDiagram);
        parseUseCases(useCasesEl, useCaseDiagram);
        parseDependencies(dependenciesEl, useCaseDiagram);
        parseUseCaseAssociations(useCaseAssociationsEl, useCaseDiagram);
    }

    private void parseActors(Element actorsEl, UseCaseDiagram useCaseDiagram) {
        logger.info("开始解析执行者");
        List<UCActor> actors = new ArrayList<UCActor>();

        List<Element> actorListEl = actorsEl.elements();
        for (int i = 0;i < actorListEl.size();i++) {
            // 获取id
            String id = actorListEl.get(i).attribute(TagsName.ID).getValue();
            String name = actorListEl.get(i).element(TagsName.NAME).getText();
            logger.info("id:" + id + " -> name:" + name);
            UCActor actor = new UCActor(id, name);

            actors.add(actor);
        }

        useCaseDiagram.setActors(actors);
        logger.info("执行者解析完毕");
    }

    private void parseUseCases(Element useCasesEl, UseCaseDiagram useCaseDiagram) {
        logger.info("开始解析用例");
        List<UCUseCase> useCases = new ArrayList<UCUseCase>();

        List<Element> useCaseListEl = useCasesEl.elements();
        for (int i = 0;i < useCaseListEl.size();i++) {
            // 获取id
            String id = useCaseListEl.get(i).attribute(TagsName.ID).getValue();
            String name = useCaseListEl.get(i).element(TagsName.NAME).getText();
            logger.info("id:" + id + " -> name:" + name);
            UCUseCase useCase = new UCUseCase(id, name);

            useCases.add(useCase);
        }

        useCaseDiagram.setUseCases(useCases);
        logger.info("用例解析完毕");
    }

    private void parseDependencies(Element dependenciesEl, UseCaseDiagram useCaseDiagram) {
        logger.info("开始解析依赖关系");
        List<UCDependency> dependencies = new ArrayList<UCDependency>();

        List<Element> dependencyListEl = dependenciesEl.elements();
        for (int i = 0;i < dependencyListEl.size();i++) {
            // 获取id
            String id = dependencyListEl.get(i).attribute(TagsName.ID).getValue();
            String name = dependencyListEl.get(i).element(TagsName.NAME).getText();
            String stereotype = dependencyListEl.get(i).element(TagsName.STEREOTYPE).getText();
            String object1Id = dependencyListEl.get(i).element(TagsName.OBJECT1).element(TagsName.USECASE).attribute(TagsName.REF).getValue();
            String object2Id = dependencyListEl.get(i).element(TagsName.OBJECT2).element(TagsName.USECASE).attribute(TagsName.REF).getValue();

            UCUseCase object1 = useCaseDiagram.getUseCase(object1Id);
            UCUseCase object2 = useCaseDiagram.getUseCase(object2Id);
            logger.info("id:" + id + " -> name:" + name + " -> stereotype:" + stereotype
                    + " -> object1Id:" + object1Id + " -> object2Id:" + object2Id);
            UCDependency dependency;
            try {
                dependency = new UCDependency(id, name, stereotype, object1, object2);
            } catch (Exception e) {
                logger.error(e.getMessage());
                continue;
            }

            dependencies.add(dependency);
        }

        useCaseDiagram.setDependencies(dependencies);
        logger.info("依赖关系解析完毕");
    }

    private void parseUseCaseAssociations(Element useCaseAssociationsEl, UseCaseDiagram useCaseDiagram) {
        logger.info("开始解析关联关系");
        List<UCUseCaseAssociation> useCaseAssociations = new ArrayList<UCUseCaseAssociation>();

        List<Element> useCaseAssociationListEl = useCaseAssociationsEl.elements();
        for (int i = 0;i < useCaseAssociationListEl.size();i++) {
            // 获取id
            String id = useCaseAssociationListEl.get(i).attribute(TagsName.ID).getValue();
            String name = useCaseAssociationListEl.get(i).element(TagsName.NAME).getText();
            String object1Id = useCaseAssociationListEl.get(i).element(TagsName.OBJECT1).element(TagsName.USECASE).attribute(TagsName.REF).getValue();
            String object2Id = useCaseAssociationListEl.get(i).element(TagsName.OBJECT2).element(TagsName.ACTOR).attribute(TagsName.REF).getValue();

            UCUseCase object1 = useCaseDiagram.getUseCase(object1Id);
            UCActor object2 = useCaseDiagram.getActor(object2Id);
            logger.info("id:" + id + " -> name:" + name
                    + " -> object1Id:" + object1Id + " -> object2Id:" + object2Id);
            UCUseCaseAssociation useCaseAssociation = new UCUseCaseAssociation(id, name, object1, object2);

            useCaseAssociations.add(useCaseAssociation);
        }

        useCaseDiagram.setUseCaseAssociations(useCaseAssociations);
        logger.info("关联关系解析完毕");
    }
}
