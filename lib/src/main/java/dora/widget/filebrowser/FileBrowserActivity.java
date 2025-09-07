package dora.widget.filebrowser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import dora.util.IoUtils;
import dora.util.PinyinUtils;
import dora.util.StatusBarUtils;
import dora.util.TimeUtils;
import dora.util.ToastUtils;
import dora.widget.filebrowser.base.BaseAdapter;
import dora.widget.filebrowser.bean.MyFile;
import dora.widget.filebrowser.bean.MyFolder;
import dora.widget.filebrowser.fs.FNode;
import dora.widget.filebrowser.util.PinyinComparator;
import dora.widget.filebrowser.view.LetterView;

public class FileBrowserActivity extends AppCompatActivity {

    private TextView tvMainCurrPath;
    private TextView tvMainTotalRom;
    private TextView tvMainAvailableRom;
    private TextView tvTitlebarLeft;
    private TextView tvTitlebarRight;
    private ListView mFileListView;
    private LetterView mLetterView;
    private TextView mTextDialog;
    private List<FNode> mFileTree;
    private FileAdapter mAdapter;
    public static final String ACTION_CHOOSE_FILE = "dora.widget.filebrowser.action.CHOOSE_FILE";
    public static final String ACTION_CHOOSE_FOLDER = "dora.widget.filebrowser.action.CHOOSE_FOLDER";
    public static final String ACTION_CHOOSE_BOTH_FILE_AND_FOLDER = "dora.widget.filebrowser.action.CHOOSE_BOTH_FILE_AND_FOLDER";
    private String mAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);
        StatusBarUtils.setStatusBar(this, ContextCompat.getColor(this, R.color.colorPrimaryDark), 255);
        Intent intent = getIntent();
        mAction = intent.getAction();
        if (mAction == null) {
            finish();
        }
        initViews();
        XXPermissions.with(this)
                .permission(
                        Permission.READ_MEDIA_IMAGES,
                        Permission.READ_MEDIA_VIDEO,
                        Permission.READ_MEDIA_AUDIO
                )
                .request((permissions, allGranted) -> {
                    if (allGranted) {
                        initData();
                    }
                });
    }

    private void initViews() {
        tvMainCurrPath = (TextView) findViewById(R.id.tv_main_curr_path);
        tvMainTotalRom = (TextView) findViewById(R.id.tv_main_total_rom);
        tvMainAvailableRom = (TextView) findViewById(R.id.tv_main_available_rom);
        tvTitlebarLeft = (TextView) findViewById(R.id.tv_titlebar_left);
        tvTitlebarRight = (TextView) findViewById(R.id.tv_titlebar_right);
        mFileListView = (ListView) findViewById(R.id.mFileListView);
        mLetterView = (LetterView) findViewById(R.id.mLetterView);
        mTextDialog = (TextView) findViewById(R.id.mTextDialog);
    }

    public static class FileAdapter extends BaseAdapter<FNode> implements SectionIndexer {

        ImageView ivFileType;
        TextView tvFileName;
        TextView tvFileLastModified;

        public FileAdapter(Context context) {
            super(context);
        }

        @Override
        public void addItems(List<FNode> data) {
            super.addItems(generateLetters(data));
        }

        @Override
        public int getItem() {
            return R.layout.item_file;
        }

        @Override
        public int[] getViewIds() {
            return new int[] {
                    R.id.iv_file_type,
                    R.id.tv_file_name,
                    R.id.tv_file_last_modified
            };
        }

        private List<FNode> generateLetters(List<FNode> fNodes) {
            for (FNode fNode : fNodes) {
                String sortLetter = PinyinUtils.getPinyinFromSentence(fNode.getName().substring(0, 1))
                        .toUpperCase();
                fNode.setSortLetter(sortLetter);
            }
            Collections.sort(fNodes, PinyinComparator.get());
            return fNodes;
        }

        @Override
        public Object[] getSections() {
            return new Object[0];
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            for (int i = 0; i < getCount(); i++) {
                String sortLetter = mBeans.get(i).getSortLetter();
                if (sortLetter != null) {
                    char first = sortLetter.toUpperCase().charAt(0);
                    if (first == sectionIndex) {
                        return i;
                    }
                }
            }
            return -1;
        }

        @Override
        public int getSectionForPosition(int position) {
            return mBeans.get(position).getSortLetter() == null ? -1 : mBeans.get(position).getSortLetter().toUpperCase().charAt(0);
        }

        public int getPositionForSection(char sectionIndex) {
            for (int i = 0; i < getCount(); i++) {
                String sortLetter = mBeans.get(i).getSortLetter();
                if (sortLetter != null) {
                    char first = sortLetter.toUpperCase().charAt(0);
                    if (first == sectionIndex) {
                        return i;
                    }
                }
            }
            return -1;
        }

        @Override
        protected <VIEW extends View> void onBindViewHolder(int position, FNode fileable, ViewHolder<VIEW> holder) {
            ivFileType = (ImageView) holder.getView(R.id.iv_file_type);
            tvFileName = (TextView) holder.getView(R.id.tv_file_name);
            tvFileLastModified = (TextView) holder.getView(R.id.tv_file_last_modified);
            tvFileName.setText(fileable.getName());
            tvFileLastModified.setText(TimeUtils.getTimeString(fileable.lastModified(), "yyyy-MM-dd HH:mm"));
            setFileIcon(ivFileType, fileable.getName().substring(fileable.getName().lastIndexOf(".") + 1));
        }
    }

    /**
     * 设置显示的图标。
     *
     * @param suffix 文件后缀
     */
    private static void setFileIcon(ImageView iconView, String suffix) {
        suffix = suffix.toUpperCase(Locale.CHINA);
        if ((suffix.equals("MP3")) || (suffix.equals("AAC")) || (suffix.equals("FLAC"))) {
            iconView.setImageResource(R.drawable.icon_mime_group_music);
            return;
        }
        if ((suffix.equals("MP4")) || (suffix.equals("AVI")) || (suffix.equals("FLV")) || (suffix.equals("MPEG")) || (suffix.equals("MOV"))) {
            iconView.setImageResource(R.drawable.icon_mime_group_movie);
            return;
        }
        if ((suffix.equals("JPG")) || (suffix.equals("GIF")) || (suffix.equals("PNG")) || (suffix.equals("JPEG")) || (suffix.equals("BMP"))) {
            iconView.setImageResource(R.drawable.icon_mime_group_photo);
            return;
        }
        if ((suffix.equals("ZIP")) || (suffix.equals("RAR")) || (suffix.equals("7Z"))) {
            iconView.setImageResource(R.drawable.icon_mime_group_compress);
            return;
        }
        if (suffix.equals("APK")) {
            iconView.setImageResource(R.drawable.icon_mime_apk);
            return;
        }
        if (suffix.equals("TXT")) {
            iconView.setImageResource(R.drawable.icon_mime_txt);
            return;
        }
        if (suffix.equals("EPUB")) {
            iconView.setImageResource(R.drawable.icon_mime_group_text);
            return;
        }
        if ((suffix.equals("DOC")) || (suffix.equals("DOCX")) || (suffix.equals("WPS"))) {
            iconView.setImageResource(R.drawable.icon_mime_group_doc);
            return;
        }
        if ((suffix.equals("XLS")) || (suffix.equals("XLSX")) || (suffix.equals("ET"))) {
            iconView.setImageResource(R.drawable.icon_mime_group_excel);
            return;
        }
        if ((suffix.equals("PPT")) || (suffix.equals("PPTX")) || (suffix.equals("DPS"))) {
            iconView.setImageResource(R.drawable.icon_mime_group_ppt);
            return;
        }
        if (suffix.equals("PDF")) {
            iconView.setImageResource(R.drawable.icon_mime_pdf);
            return;
        }
        if (suffix.equals("HTML")) {
            iconView.setImageResource(R.drawable.icon_mime_html);
            return;
        }
        if (suffix.equals("CHM")) {
            iconView.setImageResource(R.drawable.icon_mime_chm);
            return;
        }
        if (suffix.equals("URL")) {
            iconView.setImageResource(R.drawable.icon_mime_url);
            return;
        }
    }

    private void initData() {
        tvMainCurrPath.setText(IoUtils.getSdRoot());
        tvMainTotalRom.setText(
                String.format(ContextCompat.getString(this, R.string.total_space), IoUtils.getRomTotalSize(this))
        );
        tvMainAvailableRom.setText(
                String.format(ContextCompat.getString(this, R.string.left_space), IoUtils.getRomAvailableSize(this))
        );
        final MyFolder myFolder = initFolder(new MyFolder(new File(IoUtils.getSdRoot())));
        mFileTree = myFolder.getAllChild();
        mAdapter = new FileAdapter(this);
        if (mFileTree != null && mFileTree.size() > 0) {
            mAdapter.addItems(mFileTree);
        }
        mFileListView.setAdapter(mAdapter);
        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FNode fNode = (FNode) mAdapter.getItem(position);
                if (fNode instanceof MyFile) {
                    if (mAction.equals(ACTION_CHOOSE_FILE) || mAction.equals(ACTION_CHOOSE_BOTH_FILE_AND_FOLDER)) {
                        Intent intent = new Intent();
                        intent.putExtra(FileBrowser.EXTRA_PATH, fNode.getPath());
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        ToastUtils.showShort(FileBrowserActivity.this, getString(R.string.choose_folder));
                    }
                } else if (fNode instanceof MyFolder) {
                    List<FNode> subFiles = ((MyFolder) fNode).enter();//拿到子目录所有文件
                    mAdapter.clear();
                    if (subFiles != null && subFiles.size() > 0) {
                        mAdapter.addItems(subFiles);
                    }
                    tvMainCurrPath.setText(fNode.getPath());
                }
            }
        });
        mLetterView.setOnLetterChangeListener(new LetterView.OnLetterChangeListener() {
            @Override
            public void onChanged(String letter) {
                mTextDialog.setText(letter);
                int positionForSection = mAdapter.getPositionForSection(letter.toUpperCase().charAt(0));
                mFileListView.setSelection(positionForSection);
            }
        });
        mLetterView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTextDialog.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        mTextDialog.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });
        tvTitlebarLeft.setText(R.string.back);
        if (mAction.equals(ACTION_CHOOSE_FOLDER) || mAction.equals(ACTION_CHOOSE_BOTH_FILE_AND_FOLDER)) {
            tvTitlebarRight.setText(R.string.select);
        }
        tvTitlebarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyFolder parentFolder = new MyFolder(new File(tvMainCurrPath.getText().toString().trim()).getParentFile());
                MyFolder myFolder = initFolder(parentFolder);
                if (myFolder != null) {
                    List<FNode> allChild = myFolder.getAllChild();
                    mAdapter.clear();
                    if (allChild != null && allChild.size() > 0) {
                        mAdapter.addItems(allChild);
                    }
                    tvMainCurrPath.setText(parentFolder.getPath());
                } else {
                    ToastUtils.showShort(FileBrowserActivity.this, getString(R.string.cant_back));
                }
            }
        });
        tvTitlebarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(FileBrowser.EXTRA_PATH, tvMainCurrPath.getText().toString().trim());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private MyFolder initFolder(MyFolder rootFolder) {
        if (rootFolder != null) {
            File file = rootFolder.getFile();
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    FNode fNode;
                    if (f.isDirectory()) {
                        fNode = new MyFolder(f);
                    } else {
                        fNode = new MyFile(f);
                    }
                    rootFolder.addChild(fNode);
                }
                rootFolder.sort();
            } else {
                return null;
            }
        }
        return rootFolder;
    }
}
