package ru.aristar.jnuget.ui.tree;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * Компонент, отображающий древовидную структуру в виде вложенных списков
 *
 * @author sviridov
 */
@FacesComponent("ru.aristar.jnuget.ui.tree.TreeComponent")
public class TreeComponent extends UIComponentBase {

    /**
     * Имя атрибута, в котором хранится выражение для переменной, в которую
     * биндится текущий узел
     */
    private static final String VAR = "var";
    /**
     * Имя атрибута, содержащего информацию отображать или нет узел
     */
    public static final String SHOW_ROOT = "showRoot";
    /**
     * Имя атрибута, содержащего корневой узел дерева.
     */
    public static final String ROOT_NODE = "rootNode";
    /**
     * Имя атрибута, содержащего имя метода, возвращающего коллекцию дочерних
     * элементов.
     */
    public static final String CHILDREN_METHOD = "childrenMethod";

    @Override
    public String getFamily() {
        return "ru.aristar.components";
    }

    /**
     * @return имя переменной, в которую биндится текущий узел
     */
    public String getVar() {
        return (String) getStateHelper().get(VAR);
    }

    /**
     * @param var имя переменной, в которую биндится текущий узел
     */
    public void setVar(String var) {
        getStateHelper().put(VAR, var);
    }

    /**
     * @return отображать или нет корневой элемент
     */
    private boolean showRootElement() {
        Object value = getAttributes().get(SHOW_ROOT);
        if (value == null) {
            return true;
        }
        return value.equals(true);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        //TODO Создать Renderer
        if ((context == null)) {
            throw new NullPointerException("FacesContext must be not null.");
        }
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("ul", this);
        //Узлы
        Object node = getAttributes().get(ROOT_NODE);
        if (node == null) {
            return;
        }
        if (showRootElement()) {
            writeNode(node, writer, context);
        } else {
            for (Object childNode : getChildren(node)) {
                writeNode(childNode, writer, context);
            }
        }
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        if ((context == null)) {
            throw new NullPointerException("FacesContext must be not null.");
        }
        ResponseWriter writer = context.getResponseWriter();
        writer.endElement("ul");
    }

    /**
     * Отрендерить узел
     *
     * @param node узел
     * @param writer поток, в который записываются данные
     * @param context контекст
     * @throws IOException ошибка записи в поток
     */
    private void writeNode(Object node, ResponseWriter writer, FacesContext context) throws IOException {
        if (node == null) {
            return;
        }
        String var = (String) getStateHelper().get(VAR);
        if (var != null) {
            Map<String, Object> requestMap = getFacesContext().getExternalContext().getRequestMap();
            requestMap.put(var, node);
        }

        writer.startElement("li", this);

        for (UIComponent component : getChildren()) {
            component.encodeAll(context);
        }

        if (var != null) {
            Map<String, Object> requestMap = getFacesContext().getExternalContext().getRequestMap();
            requestMap.remove(var);
        }

        Collection children = getChildren(node);
        if (!children.isEmpty()) {
            writer.startElement("ul", this);
            for (Object childNode : children) {
                writeNode(childNode, writer, context);
            }
            writer.endElement("ul");
        }
        writer.endElement("li");
    }

    /**
     * Возвращает список дочерних объектов
     *
     * @param object родительский объект
     * @return дочерние объекты или пустой список
     */
    private Collection getChildren(Object object) {
        if (object instanceof TreeNode) {
            return ((TreeNode) object).getChildren();
        }
        try {
            Method method = object.getClass().getMethod(getChildMethodName());
            method.setAccessible(true);
            Class<?> returnType = method.getReturnType();
            if (returnType.isArray()) {
                return Arrays.asList((Object[]) method.invoke(object));
            } else if (Collection.class.isAssignableFrom(returnType)) {
                return (Collection) method.invoke(object);
            } else {
                throw new NoSuchMethodException();
            }
        } catch (NoSuchMethodException | SecurityException |
                IllegalAccessException | IllegalArgumentException |
                InvocationTargetException e) {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * @return имя метода, возвращающего дочерние элементы
     */
    private String getChildMethodName() {
        return (String) getAttributes().get(CHILDREN_METHOD);
    }

    /**
     * Узел дерева
     */
    public static interface TreeNode {

        /**
         * @return коллекция дочерних узлов
         */
        Collection<TreeNode> getChildren();
    }
}
