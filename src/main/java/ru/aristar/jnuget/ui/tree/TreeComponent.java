package ru.aristar.jnuget.ui.tree;

import java.io.IOException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 *
 * @author sviridov
 */
@FacesComponent("ru.aristar.jnuget.ui.tree.TreeComponent")
public class TreeComponent extends UIComponentBase {

    @Override
    public String getFamily() {
        return "ru.aristar.components";
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
        Object node = getAttributes().get("rootNode");
        if (node == null || !(node instanceof TreeNode)) {
            return;
        }
        TreeNode rootNode = (TreeNode) node;
        Object showRoot = getAttributes().get("showRoot");
        if (showRoot == null || (showRoot.equals(true))) {
            writeNode(rootNode, writer, context);
        } else {
            for (TreeNode childNode : rootNode.getChildren()) {
                writeNode(childNode, writer, context);
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        if ((context == null)) {
            throw new NullPointerException("FacesContext must be not null.");
        }
        ResponseWriter writer = context.getResponseWriter();
        writer.endElement("ul");
    }

    private void writeNode(TreeNode node, ResponseWriter writer, FacesContext context) throws IOException {
        if (node == null) {
            return;
        }
        writer.startElement("li", this);
        //TODO Обработать вложенные компоненты
        for (UIComponent component : getChildren()) {
            component.encodeAll(context);
        }
        writer.writeText(node.getName(), null);
        if (!node.getChildren().isEmpty()) {
            writer.startElement("ul", this);
            for (TreeNode childNode : node.getChildren()) {
                writeNode(childNode, writer, context);
            }
            writer.endElement("ul");
        }
        writer.endElement("li");
    }
}
