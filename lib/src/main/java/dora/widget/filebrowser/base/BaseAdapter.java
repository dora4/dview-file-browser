package dora.widget.filebrowser.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    protected List<T> mBeans;
    private LayoutInflater mInflater;
    private static final String METHOD_INFLATE = "inflate";
    private View mConvertView;
    private ViewHolder<View> mViewHolder;

    public BaseAdapter(Context context) {
        this.mBeans = new ArrayList<>();
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public BaseAdapter(Context context, @NonNull List<T> beans) {
        this(context);
        this.bindDataSet(beans);
    }

    public void bindDataSet(@NonNull List<T> beans) {
        if (this.mBeans == null) {
            this.mBeans = beans;
            this.notifyDataSetChanged();
        } else {
            throw new IllegalStateException("Data set is already bound.");
        }
    }

    public int getCount() {
        return this.mBeans != null ? this.mBeans.size() : Integer.MIN_VALUE;
    }

    public Object getItem(int position) {
        return position >= 0 && position < this.mBeans.size() ? this.mBeans.get(position) : null;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public void addItem(@NonNull T bean) {
        this.mBeans.add(bean);
        this.notifyDataSetChanged();
    }

    public void addItem(int position, @NonNull T bean) {
        this.mBeans.add(position, bean);
        this.notifyDataSetChanged();
    }

    public void addItems(@NonNull List<T> beans) {
        this.mBeans.addAll(beans);
        this.notifyDataSetChanged();
    }

    public void addItems(int start, @NonNull List<T> beans) {
        this.mBeans.addAll(start, beans);
        this.notifyDataSetChanged();
    }

    public void replaceItem(int position, @NonNull T bean) {
        this.mBeans.set(position, bean);
        this.notifyDataSetChanged();
    }

    public void replaceItems(int start, @NonNull List<T> beans) {
        for (Iterator<T> iterator = beans.iterator(); iterator.hasNext(); ++start) {
            T bean = (T) iterator.next();
            this.mBeans.set(start, bean);
        }
    }

    public void replace(@NonNull List<T> beans) {
        this.mBeans = beans;
        this.notifyDataSetInvalidated();
    }

    public void removeItem(@NonNull T bean) {
        this.mBeans.remove(bean);
        this.notifyDataSetChanged();
    }

    public void removeItem(int position) {
        this.mBeans.remove(position);
        this.notifyDataSetChanged();
    }

    public void removeItems(int start, int end) {
        for (int i = start; i <= end; ++i) {
            this.mBeans.remove(i);
        }

        this.notifyDataSetChanged();
    }

    public void clear() {
        this.mBeans.clear();
        this.notifyDataSetChanged();
    }

    private View inflateView() throws NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        int layoutId = getItem();
        Class<?> inflaterClazz = LayoutInflater.class;
        Method inflateMethod = inflaterClazz.getMethod("inflate", Integer.TYPE, ViewGroup.class);
        return (View) inflateMethod.invoke(this.mInflater, layoutId, null);
    }


    public abstract int getItem();

    public abstract int[] getViewIds();

    protected abstract <V extends View> void onBindViewHolder(int position, T bean, ViewHolder<V> holder);

    public List<T> getBeans() {
        return this.mBeans;
    }

    public final View getView(int position, View convertView, ViewGroup parent) {
        this.mConvertView = convertView;
        if (this.mConvertView == null) {
            this.mViewHolder = new ViewHolder<>();
            try {
                this.mConvertView = this.inflateView();
            } catch (IllegalArgumentException | NoSuchMethodException | IllegalAccessException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
            int[] viewIds = this.getViewIds();
            for (int viewId : viewIds) {
                this.mViewHolder.getView(viewId);
            }
            this.mConvertView.setTag(this.mViewHolder);
        } else {
            this.mViewHolder = (ViewHolder) this.mConvertView.getTag();
        }
        this.onBindViewHolder(position, mBeans.get(position), this.mViewHolder);
        return this.mConvertView;
    }

    public class ViewHolder<V extends View> {
        private final SparseArray<V> mViews;

        private ViewHolder() {
            this.mViews = new SparseArray<>();
        }

        public V getView(@IdRes int viewId) {
            V view = this.mViews.get(viewId);
            if (view == null) {
                view = BaseAdapter.this.mConvertView.findViewById(viewId);
                this.mViews.put(viewId, view);
            }
            return view;
        }
    }
}