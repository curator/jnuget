package ru.aristar.jnuget.ui.tree;

import java.util.Collection;

/**
 * Узел дерева
 *
 * @author sviridov
 */
public interface TreeNode {

    /**
     * @return имя узла
     */
    String getName();

    /**
     * @return коллекция дочерних узлов
     */
    Collection<TreeNode> getChildren();
}
