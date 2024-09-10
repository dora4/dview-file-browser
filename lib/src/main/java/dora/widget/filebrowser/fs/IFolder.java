package dora.widget.filebrowser.fs;

import java.util.List;

/**
 * 文件夹接口。
 */
public interface IFolder extends FNode {

    /**
     * 进入下一级目录。
     */
    List<FNode> enter();

    /**
     * 添加子节点。
     *
     * @param fNode
     */
    void addChild(FNode fNode);

    /**
     * 得到所有的子节点。
     *
     * @return
     */
    List<FNode> getAllChild();

    /**
     * 得到指定位置的子节点。
     *
     * @param position
     * @return
     */
    FNode getChildAt(int position);
}
