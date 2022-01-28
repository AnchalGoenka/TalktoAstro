package tta.destinigo.talktoastro.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vencha.ui.profile.ImageViewFragment;

import java.util.List;

public class JobImageAdapter extends FragmentPagerAdapter {
    List<String> imagesArray;

    public JobImageAdapter(FragmentManager fragmentManager, List<String> list) {
        super(fragmentManager);
        this.imagesArray = list;
    }

    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();
        bundle.putString("image", (String) this.imagesArray.get(i));
        ImageViewFragment imageViewFragment = new ImageViewFragment();
        imageViewFragment.setArguments(bundle);
        return imageViewFragment;
    }

    public int getCount() {
        return this.imagesArray.size();
    }
}
