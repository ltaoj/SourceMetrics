package parse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltaoj on 2018/05/23 17:36.
 *
 * @version : 1.0
 */
public class ClassParser extends DiagramParser<ClassDiagram> {

    Logger logger = Logger.getLogger(ClassParser.class);

    public void parse(String file, DataListener<ClassDiagram> listener)  throws ParseException {
        Document document=readXml(file);
        if(document == null)
            throw  new  ParseException("文件不存在，解析失败.");
        Element rootEl=document.getRootElement();
        ClassDiagram classDiagram=new ClassDiagram();
        parseInternal(rootEl, classDiagram, 0);
        listener.onComplete(classDiagram);
    }

    private  void parseInternal(Element rootEl,ClassDiagram classDiagram,int level){

        List<Element>children=rootEl.elements();
        if(level<3){
            parseInternal(children.get(0), classDiagram, level+1);
            return;
        }

        Element dependenciesEl=null;
        Element associationsEl=null;
        Element classesEl=null;
        Element generalizationsEl=null;
        Element interfacesEl=null;
        Element realizationsEl = null;
        for(int i=0;i<children.size();i++)
        {
            Element element =children.get(i);
            if (element.getName().equals(TagsName.DEPENDENCIES))
                dependenciesEl = element;
            else if(element.getName().equals(TagsName.ASSOCIATIONS))
                associationsEl=element;
            else if(element.getName().equals(TagsName.CLASSES))
                classesEl=element;
            else if (element.getName().equals(TagsName.GENERALIZATIONS))
                generalizationsEl=element;
            else if (element.getName().equals((TagsName.INTERFACES)))
                interfacesEl=element;
            else if (element.getName().equals(TagsName.REALIZATIONS))
                realizationsEl = element;
        }

        // parseClasses放到前边，先初始化classes变量
        parseInterfaces(interfacesEl, classDiagram);
        parseClasses(classesEl,classDiagram);
        parseAssociations(associationsEl,classDiagram);
        parseGeneralizations(generalizationsEl,classDiagram);
//        parseOperations(operationsEl,classDiagram);
//        parseParameters(parametersEl,classDiagram);
        parseDependencies(dependenciesEl,classDiagram);
        parseRealizations(realizationsEl, classDiagram);



    }

    private void parseAssociations(Element associationsEl,ClassDiagram classDiagram) {
        if (associationsEl == null) {
            logger.info("没有关联");
            return;
        }

        logger.info("开始解析类之间的关联关系");
        List<CAssociation> associations = new ArrayList<CAssociation>();
        List<Element> associationListEl = associationsEl.elements();
        for (int i = 0; i < associationListEl.size(); i++) {
            //获取id
            String id = associationListEl.get(i).attribute(TagsName.ID).getValue();
            String name = associationListEl.get(i).element(TagsName.NAME).getText();
            String object1Id = associationListEl.get(i).element(TagsName.OBJECT1).element(TagsName.CLASS).attribute(TagsName.REF).getValue();
            String object2Id = associationListEl.get(i).element(TagsName.OBJECT2).element(TagsName.CLASS).attribute(TagsName.REF).getValue();


            CClass object1 = classDiagram.getClass(object1Id);
            CClass object2 = classDiagram.getClass(object2Id);
            logger.info("id:" + id + " -> name:" + name
                    + " -> object1Id:" + object1Id + " -> object2Id:" + object2Id);
            CAssociation association = new CAssociation(id, name, object1, object2);

            associations.add(association);
        }
        classDiagram.setAssociations(associations);
        logger.info("关联关系解析完毕");
    }

    private void parseInterfaces(Element interfacesEl, ClassDiagram classDiagram) {
        if (interfacesEl == null) {
            logger.info("没有接口");
            return;
        }

        logger.info("开始解析接口");
        List<CInterface> interfaces = new ArrayList<CInterface>();

        List<Element> interfaceListEl = interfacesEl.elements();
        for (int i = 0;i < interfaceListEl.size();i++) {
            String id = interfaceListEl.get(i).attribute(TagsName.ID).getValue();
            String name = interfaceListEl.get(i).element(TagsName.NAME).getText();
            Element abstractEl = interfaceListEl.get(i).element(TagsName.ISABSTRACT);
            boolean isAbstract = (abstractEl != null && abstractEl.getText().equals("1")) ? true : false;

            logger.info("接口id:"+ id +"-> name:" + name + " -> isAbstract:" + isAbstract);
            CInterface cInterface = new CInterface(id, name, isAbstract, null);
            Element operationsEl = interfaceListEl.get(i).element(TagsName.OPERATIONS);
            parseOperations(operationsEl, null, cInterface);
            interfaces.add(cInterface);
        }

        classDiagram.setInterfaces(interfaces);
        logger.info("接口解析完毕");
    }

    private void parseClasses(Element classesEl,ClassDiagram classDiagram) {
        if (classesEl == null) {
            logger.info("没有类");
            return;
        }

        logger.info("开始解析类");
        List<CClass> classes = new ArrayList<CClass>();
        List<Element> classListEl = classesEl.elements();
        for (int i = 0; i < classListEl.size(); i++) {
            //获取ID

            String id = classListEl.get(i).attribute(TagsName.ID).getValue();
            String name = classListEl.get(i).element(TagsName.NAME).getText();
            // abstract这个节点oom可能没有定义
            Element abstractEl = classListEl.get(i).element(TagsName.ISABSTRACT);
            boolean isAbstract = (abstractEl != null && abstractEl.getText().equals("1")) ? true : false;
            Element finalEl = classListEl.get(i).element(TagsName.FINAL);
            boolean isFinal = finalEl == null ? false : finalEl.getText().equals("1") ? true : false;
            logger.info("类id:"+ id +"-> name:" + name + " -> isAbstract:" + isAbstract + " -> isFinal:" + isFinal);

            CClass cClass = new CClass(id, name, isAbstract, isFinal, null, null);

            // 解析类方法
            Element operationsEl = classListEl.get(i).element(TagsName.OPERATIONS);
            parseOperations(operationsEl, cClass, null);

            Element attributesEl = classListEl.get(i).element(TagsName.ATTRIBUTES);
            parseAttributes(attributesEl, cClass);
            classes.add(cClass);
            //方法集合？
            //  List<COperation>operations=classListEl.get(i).element(TagsName.OPERATION).getText();
            // logger.info("id" + id + "->name:" + name + "isabstract" + isAbstract +);
            //CClass cClass = new CClass(id, name,isAbstract);
            // classes.add(cClass);
        }

        classDiagram.setClasses(classes);
        logger.info("类解析完毕");

    }

    private void parseAttributes(Element attributesEl, CClass cClass) {
        if (attributesEl == null) {
            logger.info("\t没有属性");
            return;
        }

        List<CAttribute> attributes = new ArrayList<CAttribute>();

        List<Element> attributeListEl = attributesEl.elements();
        for (int i = 0;i < attributeListEl.size();i++) {
            //获取ID
            String id = attributeListEl.get(i).attribute(TagsName.ID).getValue();
            String name = attributeListEl.get(i).element(TagsName.NAME).getText();
            String dataType = attributeListEl.get(i).element(TagsName.DATATYPE).getText();
            Element visibilityEl = attributeListEl.get(i).element(TagsName.ATTRIBUTE_VISIBILITY);
            String visibility = visibilityEl == null ? CAttribute.VISIBILITY.PRIVATE : visibilityEl.getText();
            Element staticEl = attributeListEl.get(i).element(TagsName.STATIC);
            boolean isStatic = staticEl == null ? false : staticEl.getText().equals("1") ? true : false;

            logger.info("\t属性id:" + id + " -> name:" + name + " -> dataType:" + dataType + " -> visibility:" + visibility + " -> isStatic:" + isStatic);
            CAttribute attribute = new CAttribute(id, name, dataType, visibility, isStatic);
            attributes.add(attribute);
        }

        cClass.setAttributes(attributes);
    }

    private void parseOperations(Element operationsEl, CClass cClass, CInterface cInterface) {
        if (operationsEl == null) {
            logger.info("\t没有方法");
            return;
        }

//        logger.info("开始解析类方法");
        List<COperation> operations = new ArrayList<COperation>();

        List<Element> operationListEl = operationsEl.elements();
        for (int i = 0;i < operationListEl.size();i++) {
            //获取ID
            String id = operationListEl.get(i).attribute(TagsName.ID).getValue();
            String name = operationListEl.get(i).element(TagsName.NAME).getText();
            Element returnTypeEl = operationListEl.get(i).element(TagsName.RETURNTYPE);
            String retureType = returnTypeEl == null ? "" : returnTypeEl.getText();
            Element visibilityEl = operationListEl.get(i).element(TagsName.OPERATION_VISIBILITY);
            String visibility = visibilityEl == null ? COperation.VISIBILITY.PUBLIC : visibilityEl.getText();
            Element stereotypeEl = operationListEl.get(i).element(TagsName.STEREOTYPE);
            String stereotype = stereotypeEl == null ? COperation.STEREOTYPE.NONE : stereotypeEl.getText();
            Element influentObjectEl = operationListEl.get(i).element(TagsName.INFLUENTOBJECT);
            COperation influentObject = influentObjectEl == null ? null
                    : influentObjectEl.element(TagsName.OPERATION) == null ? null
                    : new COperation(influentObjectEl.element(TagsName.OPERATION).attribute(TagsName.REF).getValue(), null, null,null,
                    null, null, null, false, false, false, false);
            Element staticEl = operationListEl.get(i).element(TagsName.OPERATION_STATIC);
            boolean isStatic = staticEl == null ? false : staticEl.getText().equals("1") ? true : false;
            Element finalEl = operationListEl.get(i).element(TagsName.OPERATION_FINAL);
            boolean isFinal = finalEl == null ? false : finalEl.getText().equals("1") ? true : false;
            Element automaticEl = operationListEl.get(i).element(TagsName.AUTOMATIC);
            boolean isAutomatic = automaticEl == null ? false : automaticEl.getText().equals("1") ? true : false;
            Element abstractEl = operationListEl.get(i).element(TagsName.OPERATION_ABSTRACT);
            boolean isAbstract = abstractEl == null ? false : abstractEl.getText().equals("1") ? true : false;
            logger.info("\t方法id:"+ id +"-> name:" + name + " -> retureType:" + retureType + " -> visibility:" + visibility
                    + " -> stereotype:" + stereotype + " -> influentObject:" + influentObject + " -> isStatic:" + isStatic
                    + " -> isFinal:" + isFinal + " -> isAutomatic:" + isAutomatic + " -> isAbstract:" + isAbstract);
            COperation operation = new COperation(id, name, retureType, null, stereotype, visibility, influentObject,
                    isStatic, isFinal, isAutomatic, isAbstract);

            // 解析方法参数
            Element parametersEl = operationListEl.get(i).element(TagsName.PARAMETERS);
            parseParameters(parametersEl, operation);

            operations.add(operation);
        }

        if (cClass != null) {
            cClass.setOperations(operations);
        } else if (cInterface != null){
            cInterface.setOperations(operations);
        } else {
            throw new IllegalArgumentException("argument cClass or cInterface can not be both null.");
        }
//        logger.info("类方法解析完毕");
    }

    private void parseParameters(Element parametersEl, COperation operation) {
        if (parametersEl == null) {
            logger.info("\t\t没有参数");
            return;
        }
//        logger.info("开始解析参数");
        List<CParameter> parameters = new ArrayList<CParameter>();

        List<Element> parameterListEl = parametersEl.elements();
        for (int i = 0;i < parameterListEl.size();i++) {
            String id = parameterListEl.get(i).attribute(TagsName.ID).getValue();
            String name = parameterListEl.get(i).element(TagsName.NAME).getText();
            String dataType=parameterListEl.get(i).element(TagsName.PARAMETER_DATATYPE).getText();
            Element objectDataTypeEl = parameterListEl.get(i).element(TagsName.OBJECTDATATYPE);

            CClass objectDataType = null;
            // 如果objectDataTypeEl节点为null，表示参数为基本类型
            if (objectDataTypeEl != null) {
                String refId = objectDataTypeEl.element(TagsName.CLASS).attribute(TagsName.REF).getValue();
                objectDataType = new CClass(refId, dataType, false, null);
            }

            logger.info("\t\t参数id:"+ id +"-> name:" + name + " -> dataType:" + dataType);
            CParameter  parameter=new CParameter(id,name,dataType,objectDataType);
            parameters.add(parameter);
        }
        operation.setParameters(parameters);
//        logger.info("参数解析完毕");
    }

    private void parseDependencies(Element dependenciesEl,ClassDiagram classDiagram){

        if (dependenciesEl == null) {
            logger.info("没有依赖关系");
            return;
        }

        logger.info("开始解析类之间的依赖关系");
        List<CDependency>dependencies=new ArrayList<CDependency>();
        List<Element>dependencyListEl=dependenciesEl.elements();
        for(int i=0;i<dependencyListEl.size();i++){
            //获取id
            String id = dependencyListEl.get(i).attribute(TagsName.ID).getValue();
            String name = dependencyListEl.get(i).element(TagsName.NAME).getText();

            String object1Id = dependencyListEl.get(i).element(TagsName.OBJECT1).element(TagsName.CLASS).attribute(TagsName.REF).getValue();
            String object2Id = dependencyListEl.get(i).element(TagsName.OBJECT2).element(TagsName.CLASS).attribute(TagsName.REF).getValue();

            CClass object1 = classDiagram.getClass(object1Id);
            CClass object2 = classDiagram.getClass(object2Id);
            logger.info("id:" + id + " -> name:" + name + " -> object1Id:" + object1Id + " -> object2Id:" + object2Id);
            CDependency dependency = new CDependency(id, name, object1, object2);

            dependencies.add(dependency);
        }

        classDiagram.setDependencies(dependencies);
        logger.info("类之间依赖关系解析完毕");
    }

    private void  parseGeneralizations(Element generalizationsEl,ClassDiagram classDiagram){

        if (generalizationsEl == null) {
            logger.info("类之间没有继承关系");
            return;
        }

        logger.info("开始解析类之间的继承关系");
        List<CGeneralization> generalizations=new ArrayList<CGeneralization>();
        List<Element> generalizationListEl=generalizationsEl.elements();
        for(int i=0;i<generalizationListEl.size();i++){
            //获取id
            String id = generalizationListEl.get(i).attribute(TagsName.ID).getValue();
            String name = generalizationListEl.get(i).element(TagsName.NAME).getText();

            String object1Id = generalizationListEl.get(i).element(TagsName.OBJECT1).element(TagsName.CLASS).attribute(TagsName.REF).getValue();
            String object2Id = generalizationListEl.get(i).element(TagsName.OBJECT2).element(TagsName.CLASS).attribute(TagsName.REF).getValue();

            CClass object1 = classDiagram.getClass(object1Id);//父类
            CClass object2 = classDiagram.getClass(object2Id);//子类
            logger.info("id:" + id + " -> name:" + name + " -> object1Id:" + object1Id + " -> object2Id:" + object2Id);
            CGeneralization generalization = new CGeneralization(id, name, object1, object2);

            generalizations.add(generalization);
        }

        classDiagram.setGeneralizations(generalizations);
        logger.info("类之间继承关系解析完毕");
    }

    private void parseRealizations(Element realizationsEl, ClassDiagram classDiagram) {

        if (realizationsEl == null) {
            logger.info("类和接口之间没有实现关系");
            return;
        }

        logger.info("开始解析类和接口之间的实现关系");
        List<CRealization> realizations =new ArrayList<CRealization>();
        List<Element> realizationListEl=realizationsEl.elements();
        for(int i=0;i<realizationListEl.size();i++){
            //获取id
            String id = realizationListEl.get(i).attribute(TagsName.ID).getValue();
            String name = realizationListEl.get(i).element(TagsName.NAME).getText();

            String object1Id = realizationListEl.get(i).element(TagsName.OBJECT1).element(TagsName.INTERFACE).attribute(TagsName.REF).getValue();
            String object2Id = realizationListEl.get(i).element(TagsName.OBJECT2).element(TagsName.CLASS).attribute(TagsName.REF).getValue();

            CInterface object1 = classDiagram.getInterface(object1Id);
            CClass object2 = classDiagram.getClass(object2Id);
            logger.info("id:" + id + " -> name:" + name + " -> object1Id:" + object1Id + " -> object2Id:" + object2Id);
            CRealization realization = new CRealization(id, name, object1, object2);

            realizations.add(realization);
        }

        classDiagram.setRealizations(realizations);
        logger.info("类和接口之间实现关系解析完毕");
    }

    /*private void parseOperations(Element operationsEl,ClassDiagram classDiagram){

        logger.info("开始解析方法");
        List<COperation>operations=new ArrayList<COperation>();
        List<Element>operationListEl=operationsEl.elements();

        for (int i = 0;i < operationListEl.size();i++) {
            // 获取id
            String id = operationListEl.get(i).attribute(TagsName.ID).getValue();
            String name = operationListEl.get(i).element(TagsName.NAME).getText();
            String returnType = operationListEl.get(i).element(TagsName.RETURNTYPE).getText();
            //List<CParameter>parameters=operationListEl.get(i).element(TagsName.PARAMETER);
            logger.info("id:" + id + " -> name:" + name+"->returnType:"+ returnType);
            //  COperation operation = new COperation(id, name,returnType,);

            //operations.add(operation);
        }

        classDiagram.setOperations(operations);
        logger.info("方法解析完毕");
    }*/


    /*private  void  parseParameters(Element parametersEl,ClassDiagram classDiagram){

        logger.info("开始解析参数");


        List<CParameter> parameters = new ArrayList<CParameter>();

        List<Element> parameterListEl = parametersEl.elements();
        for (int i = 0;i < parameterListEl.size();i++) {
            String id = parameterListEl.get(i).attribute(TagsName.ID).getValue();
            String name = parameterListEl.get(i).element(TagsName.NAME).getText();
            String dataType=parameterListEl.get(i).element(TagsName.).getText();
            CClass objectDataType;
            logger.info("id:"+ id +"-> name:" + name + " -> dataType" + dataType  );
            CParameter  parameter=new CParameter(id,name,dataType,objectDataType);
            parameters.add(parameter);
        }
        classDiagram.setParameters(parameters);
        logger.info("参数解析完毕");
    }*/
}