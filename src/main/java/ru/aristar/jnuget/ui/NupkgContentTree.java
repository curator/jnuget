package ru.aristar.jnuget.ui;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.nio.NupkgFileSystem;
import ru.aristar.jnuget.ui.tree.TreeComponent;
import ru.aristar.jnuget.ui.tree.TreeComponent.TreeNode;

/**
 * Дерево содержимого пакета NuGet
 *
 * @author sviridov
 */
public class NupkgContentTree {

    /**
     * @param nupkg пакет NuGet
     * @throws IOException ошибка чтения пакета
     */
    public NupkgContentTree(Nupkg nupkg) throws IOException {
        fileSystem = new NupkgFileSystem(nupkg);
        Path root = fileSystem.getRootDirectories().iterator().next();
        rootNode = new Node(root);
    }

    /**
     * @return корневой узел дерева пакетов
     */
    public TreeComponent.TreeNode getRootNode() {
        return rootNode;
    }
    /**
     * Файловая система
     */
    private final NupkgFileSystem fileSystem;
    /**
     * Корневой узел дерева пакетов
     */
    private final Node rootNode;

    /**
     * Узел дерева пакетов
     */
    public class Node implements TreeComponent.TreeNode {

        /**
         * @param path путь к узлу дерева
         */
        public Node(Path path) {
            this.path = path;
        }

        /**
         * @return имя узла
         */
        public String getName() {
            return path.toFile().getName();
        }

        @Override
        public Collection<TreeNode> getChildren() {
            if (children == null) {
                children = loadChildren();
            }
            return children;
        }

        /**
         * Загружает список дочерних узлов
         *
         * @return список дочерних узлов
         */
        private Collection<TreeNode> loadChildren() {
            try {
                boolean isDirectory = fileSystem.provider().readAttributes(path, BasicFileAttributes.class).isDirectory();
                if (!isDirectory) {
                    return Collections.EMPTY_LIST;
                }
                DirectoryStream<Path> directoryStream = fileSystem.provider().newDirectoryStream(path, null);
                ArrayList<TreeNode> result = new ArrayList<>();
                for (Path child : directoryStream) {
                    Node node = new Node(child);
                    result.add(node);
                }
                return result;
            } catch (IOException e) {
                return Collections.EMPTY_LIST;
            }
        }
        /**
         * Путь к текущему узлу
         */
        private final Path path;
        /**
         * Дочерние узлы дерева
         */
        private Collection<TreeNode> children;
    }
}
