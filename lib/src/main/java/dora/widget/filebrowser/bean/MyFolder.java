package dora.widget.filebrowser.bean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dora.widget.filebrowser.fs.AbsFolder;
import dora.widget.filebrowser.fs.FNode;
import dora.widget.filebrowser.util.PinyinComparator;

public class MyFolder extends AbsFolder {

    public MyFolder(File folder) {
        super(folder);
    }

    @Override
    public List<FNode> enter() {
        List<FNode> subFiles = new ArrayList<>();
        File folder = getFile();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    subFiles.add(new MyFile(f));
                } else if (f.isDirectory()) {
                    subFiles.add(new MyFolder(f));
                }
            }
        }
        // 请判断集合的数据是否为空，而不是判断集合是否为空
        return subFiles;
    }

    public void sort() {
        if (mChildFileable != null && mChildFileable.size() > 0) {
            Collections.sort(mChildFileable, PinyinComparator.get());
        }
    }

    @Override
    public void copy(AbsFolder f) {
        File folder = getFile();
        File[] files = folder.listFiles();
        String folderName = f.getPath() + File.separator + getName();
        File newFolder = new File(folderName); // 新要创建的目录
        if (!newFolder.exists()) {
            newFolder.mkdir(); // 创建目录
        }
        if (files != null) { // 说明文件夹不是空的
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        new MyFile(file).copy(new MyFolder(newFolder));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (file.isDirectory()) {
                    // 如果子目录还是目录
                    new MyFolder(file).copy(new MyFolder(newFolder));
                }
            }
        }
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    @Override
    public void clear() {
        File folder = getFile();
        File[] files = folder.listFiles();
        if (files != null) { // 说明文件夹不是空的
            for (File file : files) {
                if (file.isFile()) {
                    new MyFile(file).clear();
                } else if (file.isDirectory()) {
                    new MyFolder(file).clear();
                    file.delete();
                }
            }
        }
        folder.delete(); // 最后把自己也要删除
    }
}
