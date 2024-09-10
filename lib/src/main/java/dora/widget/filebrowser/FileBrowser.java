package dora.widget.filebrowser;

import android.app.Activity;
import android.content.Intent;

public class FileBrowser {

    public static final int REQUEST_CODE_CHOOSE_FILE = 1;
    public static final int REQUEST_CODE_CHOOSE_FOLDER = 2;
    public static final int REQUEST_CODE_CHOOSE_BOTH_FILE_AND_FOLDER = 3;

    public static void chooseFile(Activity activity) {
        Intent intent = new Intent(activity, FileBrowserActivity.class);
        intent.setAction(FileBrowserActivity.ACTION_CHOOSE_FILE);
        activity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_FILE);
    }

    public static void chooseFolder(Activity activity) {
        Intent intent = new Intent(activity, FileBrowserActivity.class);
        intent.setAction(FileBrowserActivity.ACTION_CHOOSE_FOLDER);
        activity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_FOLDER);
    }

    public static void chooseFileAndFolder(Activity activity) {
        Intent intent = new Intent(activity, FileBrowserActivity.class);
        intent.setAction(FileBrowserActivity.ACTION_CHOOSE_BOTH_FILE_AND_FOLDER);
        activity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_BOTH_FILE_AND_FOLDER);
    }
}
