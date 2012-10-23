package ru.aristar.jnuget.ui.tree;

import java.util.Collection;

/**
 *
 * @author sviridov
 */
public interface TreeNode {

    String getName();

    Collection<TreeNode> getChildren();

}
