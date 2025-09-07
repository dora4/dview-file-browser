package dora.widget.filebrowser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

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
import dora.widget.filebrowser.databinding.ActivityFileBrowserBinding;
import dora.widget.filebrowser.fs.FNode;
import dora.widget.filebrowser.util.PinyinComparator;

public class FileBrowserActivity extends AppCompatActivity {

    private ActivityFileBrowserBinding binding;
    private List<FNode> mFileTree;
    private FileAdapter mAdapter;

    public static final String ACTION_CHOOSE_FILE = "dora.widget.filebrowser.action.CHOOSE_FILE";
    public static final String ACTION_CHOOSE_FOLDER = "dora.widget.filebrowser.action.CHOOSE_FOLDER";
    public static final String ACTION_CHOOSE_BOTH_FILE_AND_FOLDER = "dora.widget.filebrowser.action.CHOOSE_BOTH_FILE_AND_FOLDER";
    private String mAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFileBrowserBinding.inflate(getLayoutInflater());

        StatusBarUtils.setStatusBar(this, ContextCompat.getColor(this, R.color.colorPrimaryDark), 255);

        mAction = getIntent().getAction();
        if (mAction == null) {
            finish();
            return;
        }

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

    public static class FileAdapter extends BaseAdapter<FNode> implements android.widget.SectionIndexer {

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

        @Override
        protected <VIEW extends View> void onBindViewHolder(int position, FNode fNode, ViewHolder<VIEW> holder) {
            ImageView ivFileType = (ImageView) holder.getView(R.id.iv_file_type);
            android.widget.TextView tvFileName = (android.widget.TextView) holder.getView(R.id.tv_file_name);
            android.widget.TextView tvFileLastModified = (android.widget.TextView) holder.getView(R.id.tv_file_last_modified);

            tvFileName.setText(fNode.getName());
            tvFileLastModified.setText(TimeUtils.getTimeString(fNode.lastModified(), "yyyy-MM-dd HH:mm"));

            setFileIcon(ivFileType, fNode.getName().substring(fNode.getName().lastIndexOf(".") + 1));
        }
    }

    private void initData() {
        binding.tvMainCurrPath.setText(IoUtils.getSdRoot());
        binding.tvMainTotalRom.setText(String.format(getString(R.string.total_space), IoUtils.getRomTotalSize(this)));
        binding.tvMainAvailableRom.setText(String.format(getString(R.string.left_space), IoUtils.getRomAvailableSize(this)));

        final MyFolder myFolder = initFolder(new MyFolder(new File(IoUtils.getSdRoot())));
        mFileTree = myFolder.getAllChild();
        mAdapter = new FileAdapter(this);

        if (mFileTree != null && !mFileTree.isEmpty()) {
            mAdapter.addItems(mFileTree);
        }

        binding.mFileListView.setAdapter(mAdapter);

        binding.mFileListView.setOnItemClickListener((adapterView, view, position, id) -> {
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
                List<FNode> subFiles = ((MyFolder) fNode).enter();
                mAdapter.clear();
                if (subFiles != null && !subFiles.isEmpty()) {
                    mAdapter.addItems(subFiles);
                }
                binding.tvMainCurrPath.setText(fNode.getPath());
            }
        });

        binding.mLetterView.setOnLetterChangeListener(letter -> {
            binding.mTextDialog.setText(letter);
            int positionForSection = mAdapter.getPositionForSection(letter.toUpperCase().charAt(0));
            binding.mFileListView.setSelection(positionForSection);
        });

        binding.mLetterView.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    binding.mTextDialog.setVisibility(View.VISIBLE);
                    break;
                case MotionEvent.ACTION_UP:
                    binding.mTextDialog.setVisibility(View.GONE);
                    break;
            }
            return false;
        });

        binding.tvTitlebarLeft.setText(R.string.back);
        if (mAction.equals(ACTION_CHOOSE_FOLDER) || mAction.equals(ACTION_CHOOSE_BOTH_FILE_AND_FOLDER)) {
            binding.tvTitlebarRight.setText(R.string.select);
        }

        binding.tvTitlebarLeft.setOnClickListener(view -> {
            MyFolder parentFolder = new MyFolder(new File(binding.tvMainCurrPath.getText().toString().trim()).getParentFile());
            MyFolder folder = initFolder(parentFolder);
            if (folder != null) {
                List<FNode> allChild = folder.getAllChild();
                mAdapter.clear();
                if (allChild != null && !allChild.isEmpty()) {
                    mAdapter.addItems(allChild);
                }
                binding.tvMainCurrPath.setText(parentFolder.getPath());
            } else {
                ToastUtils.showShort(FileBrowserActivity.this, getString(R.string.cant_back));
            }
        });

        binding.tvTitlebarRight.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra(FileBrowser.EXTRA_PATH, binding.tvMainCurrPath.getText().toString().trim());
            setResult(Activity.RESULT_OK, intent);
            finish();
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

    private static void setFileIcon(ImageView iconView, String suffix) {
        suffix = suffix.toUpperCase(Locale.CHINA);
        switch (suffix) {
            case "MP3":
            case "AAC":
            case "FLAC":
                iconView.setImageResource(R.drawable.icon_mime_group_music);
                break;
            case "MP4":
            case "AVI":
            case "FLV":
            case "MPEG":
            case "MOV":
                iconView.setImageResource(R.drawable.icon_mime_group_movie);
                break;
            case "JPG":
            case "GIF":
            case "PNG":
            case "JPEG":
            case "BMP":
                iconView.setImageResource(R.drawable.icon_mime_group_photo);
                break;
            case "ZIP":
            case "RAR":
            case "7Z":
                iconView.setImageResource(R.drawable.icon_mime_group_compress);
                break;
            case "APK":
                iconView.setImageResource(R.drawable.icon_mime_apk);
                break;
            case "TXT":
                iconView.setImageResource(R.drawable.icon_mime_txt);
                break;
            case "EPUB":
                iconView.setImageResource(R.drawable.icon_mime_group_text);
                break;
            case "DOC":
            case "DOCX":
            case "WPS":
                iconView.setImageResource(R.drawable.icon_mime_group_doc);
                break;
            case "XLS":
            case "XLSX":
            case "ET":
                iconView.setImageResource(R.drawable.icon_mime_group_excel);
                break;
            case "PPT":
            case "PPTX":
            case "DPS":
                iconView.setImageResource(R.drawable.icon_mime_group_ppt);
                break;
            case "PDF":
                iconView.setImageResource(R.drawable.icon_mime_pdf);
                break;
            case "HTML":
                iconView.setImageResource(R.drawable.icon_mime_html);
                break;
            case "CHM":
                iconView.setImageResource(R.drawable.icon_mime_chm);
                break;
            case "URL":
                iconView.setImageResource(R.drawable.icon_mime_url);
                break;
                }
        }
    }