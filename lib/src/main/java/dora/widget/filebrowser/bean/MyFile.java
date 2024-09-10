package dora.widget.filebrowser.bean;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

import dora.widget.filebrowser.fs.AbsFile;
import dora.widget.filebrowser.util.MimeUtils;

public class MyFile extends AbsFile {

    public MyFile(File file) {
        super(file);
    }

    @Override
    public void open(Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String type = MimeUtils.getMimetype(getFile());
        intent.setDataAndType(Uri.fromFile(getFile()), type);
        context.startActivity(intent);
    }

    @Override
    public boolean isFolder() {
        return false;
    }
}
