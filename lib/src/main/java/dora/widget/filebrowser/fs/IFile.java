package dora.widget.filebrowser.fs;

import android.content.Context;

/**
 * 文件接口。
 */
public interface IFile extends FNode {

    /**
     * 打开文件。
     */
    void open(Context context);
}
