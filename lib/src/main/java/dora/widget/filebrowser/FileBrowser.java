package dora.widget.filebrowser;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

public class FileBrowser {

    public static final int REQUEST_CODE_CHOOSE_FILE = 1;
    public static final int REQUEST_CODE_CHOOSE_FOLDER = 2;
    public static final int REQUEST_CODE_CHOOSE_BOTH_FILE_AND_FOLDER = 3;

    /**
     * 读取路径的key。
     */
    public static final String EXTRA_PATH = "path";

    public static void chooseFile(@NonNull Activity activity) {
        Intent intent = new Intent(activity, FileBrowserActivity.class);
        intent.setAction(FileBrowserActivity.ACTION_CHOOSE_FILE);
        activity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_FILE);
    }

    public static void chooseFolder(@NonNull Activity activity) {
        Intent intent = new Intent(activity, FileBrowserActivity.class);
        intent.setAction(FileBrowserActivity.ACTION_CHOOSE_FOLDER);
        activity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_FOLDER);
    }

    public static void chooseFileAndFolder(@NonNull Activity activity) {
        Intent intent = new Intent(activity, FileBrowserActivity.class);
        intent.setAction(FileBrowserActivity.ACTION_CHOOSE_BOTH_FILE_AND_FOLDER);
        activity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_BOTH_FILE_AND_FOLDER);
    }
}
