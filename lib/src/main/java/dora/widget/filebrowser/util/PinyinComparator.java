package dora.widget.filebrowser.util;

import java.util.Comparator;

import dora.widget.filebrowser.bean.Sort;

/**
 * 通过首字母排序的比较器。
 */
public class PinyinComparator implements Comparator<Sort> {

    private static PinyinComparator sComparator = new PinyinComparator();

    @Override
    public int compare(Sort lhs, Sort rhs) {
        if (lhs.getSortLetter().equals("@")
                || rhs.getSortLetter().equals("#")) {
            return -1;
        } else if (lhs.getSortLetter().equals("#")
                || rhs.getSortLetter().equals("@")) {
            return 1;
        } else {
            return lhs.getSortLetter().compareTo(rhs.getSortLetter());
        }
    }

    public static PinyinComparator get() {
        return sComparator;
    }
}