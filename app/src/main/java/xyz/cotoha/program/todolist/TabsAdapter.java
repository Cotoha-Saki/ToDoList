package xyz.cotoha.program.todolist;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    public TabsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 指定された位置にあるフラグメントを返す
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        // フラグメントリストのサイズを返す
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        // フラグメントとそのタイトルをリストに追加
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
        notifyItemInserted(fragmentList.size() - 1);
    }

    public void removeFragment(int position) {
        if (position >= 0 && position < fragmentList.size()) {
            fragmentList.remove(position);
            fragmentTitleList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, fragmentList.size());
        }
    }
}
