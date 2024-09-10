package dora.widget.filebrowser.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public abstract class BaseAdapter<Bean> extends android.widget.BaseAdapter {

    protected List<Bean> mBeans;
    private LayoutInflater mInflater;
    private static final String METHOD_INFLATE = "inflate";
    private View mConvertView;
    private BaseAdapter<Bean>.ViewHolder<?> mViewHolder;

    public BaseAdapter(Context context) {
        this.mBeans = null;
        this.mInflater = null;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public BaseAdapter(Context context, List<Bean> beans) {
        this(context);
        this.bindDataSet(beans);
    }

    public void bindDataSet(List<Bean> beans) {
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

    public void addItem(Bean data) {
        this.mBeans.add(data);
        this.notifyDataSetChanged();
    }

    public void addItem(int position, Bean bean) {
        this.mBeans.add(position, bean);
        this.notifyDataSetChanged();
    }

    public void addItems(List<Bean> beans) {
        this.mBeans.addAll(beans);
        this.notifyDataSetChanged();
    }

    public void addItems(int start, List<Bean> beans) {
        this.mBeans.addAll(start, beans);
        this.notifyDataSetChanged();
    }

    public void replaceItem(int position, Bean bean) {
        this.mBeans.set(position, bean);
        this.notifyDataSetChanged();
    }

    public void replaceItems(int start, List<Bean> beans) {
        for (Iterator<Bean> iterator = beans.iterator(); iterator.hasNext(); ++start) {
            Bean bean = (Bean) iterator.next();
            this.mBeans.set(start, bean);
        }
    }

    public void replace(List<Bean> beans) {
        this.mBeans = beans;
        this.notifyDataSetInvalidated();
    }

    public void removeItem(Bean bean) {
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

    private View inflateView() throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int layoutId = getItem();
        Class<?> inflaterClazz = LayoutInflater.class;
        Method inflateMethod = inflaterClazz.getMethod("inflate", Integer.TYPE, ViewGroup.class);
        return (View) inflateMethod.invoke(this.mInflater, layoutId, null);
    }


    public abstract int getItem();

    public abstract int[] getViewIds();

    protected abstract <T extends View> void onBindViewHolder(int position, Bean bean, ViewHolder<T> holder);

    public List<Bean> getBeans() {
        return this.mBeans;
    }

    public final View getView(int position, View convertView, ViewGroup parent) {
        this.mConvertView = convertView;
        if (this.mConvertView == null) {
            this.mViewHolder = new ViewHolder();

            try {
                this.mConvertView = this.inflateView();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            int[] viewIds = this.getViewIds();
            int[] ids = viewIds;
            int length = viewIds.length;

            for (int i = 0; i < length; ++i) {
                int viewId = ids[i];
                this.mViewHolder.getView(viewId);
            }

            this.mConvertView.setTag(this.mViewHolder);
        } else {
            this.mViewHolder = (ViewHolder) this.mConvertView.getTag();
        }

        this.onBindViewHolder(position, mBeans.get(position), this.mViewHolder);
        return this.mConvertView;
    }

    public class ViewHolder<T extends View> {
        private SparseArray<T> mViews;

        private ViewHolder() {
            this.mViews = new SparseArray();
        }

        public View getView(int viewId) {
            View view = this.mViews.get(viewId);
            if (view == null) {
                view = BaseAdapter.this.mConvertView.findViewById(viewId);
                this.mViews.put(viewId, (T) view);
            }
            return view;
        }
    }
}