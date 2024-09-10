package dora.widget.filebrowser.fs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsFolder implements IFolder {

    private File folder;
    protected String sortLetter = "";

    /**
     * 保存子节点的集合
     */
    protected List<FNode> mChildFileable;

    public AbsFolder(File folder) {
        this.folder = folder;
    }

    public String getName() {
        return folder.getName();
    }

    public String getPath() {
        return folder.getPath();
    }

    public String getParent() {
        return folder.getParent();
    }

    public long lastModified() {
        return folder.lastModified();
    }

    public int getChildCount() {
        return folder.list().length;
    }

    public File getFile() {
        return folder;
    }

    public File getParentFile() {
        return folder.getParentFile();
    }

    @Override
    public void addChild(FNode fNode) {
        if (mChildFileable == null) {
            mChildFileable = new ArrayList<>();
        }
        mChildFileable.add(fNode);
    }

    @Override
    public List<FNode> getAllChild() {
        return mChildFileable;
    }

    @Override
    public FNode getChildAt(int position) {
        if (mChildFileable == null) {
            return null;
        }
        return mChildFileable.get(position);
    }

    @Override
    public String getSortLetter() {
        return sortLetter;
    }

    @Override
    public void setSortLetter(String sortLetter) {
        this.sortLetter = sortLetter;
    }
}
