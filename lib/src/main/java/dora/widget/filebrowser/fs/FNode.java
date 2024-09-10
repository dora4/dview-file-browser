package dora.widget.filebrowser.fs;

import java.io.File;
import java.io.IOException;

import dora.widget.filebrowser.bean.Sort;

/**
 * 抽象文件的接口，可以代表一个文件，也可以代表一个目录。
 */
public interface FNode extends Sort {

    /**
     * 复制到另一个目录下面。
     *
     * @param f
     */
    void copy(AbsFolder f) throws IOException;

    /**
     * 是否是目录。
     *
     * @return
     */
    boolean isFolder();

    /**
     * 从文件系统中清除掉。
     */
    void clear();

    /**
     * 获取文件对象。
     *
     * @return
     */
    File getFile();

    /**
     * 获取名字。
     *
     * @return
     */
    String getName();

    /**
     * 获取路径。
     *
     * @return
     */
    String getPath();

    /**
     * 获取上次修改的时间。
     *
     * @return
     */
    long lastModified();

    /**
     * 获取父目录的路径。
     *
     * @return
     */
    String getParent();

    /**
     * 获取父目录。
     *
     * @return
     */
    File getParentFile();
}
