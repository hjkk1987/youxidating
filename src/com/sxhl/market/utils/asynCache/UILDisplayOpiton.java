package com.sxhl.market.utils.asynCache;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.sxhl.market.R;

public class UILDisplayOpiton {

    public static DisplayImageOptions getDefaultPhotoOption(int loadingRes, int failRes, int emptyUriRes, int cornerRadiusPixels) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .showImageOnLoading(loadingRes)
                .showImageForEmptyUri(failRes)
                .showImageOnFail(emptyUriRes)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true);
        if (cornerRadiusPixels > 0) {
            builder.displayer(new RoundedBitmapDisplayer(cornerRadiusPixels));
        }
        DisplayImageOptions options=builder.build();

        return options;
    }
    
    public static DisplayImageOptions getDefaultPhotoOption(int loadingRes, int failRes, int emptyUriRes) {
        return getDefaultPhotoOption(loadingRes, failRes, emptyUriRes, 0);
    }
    
    public static DisplayImageOptions getDefaultPhotoOption(int loadingRes) {
        return getDefaultPhotoOption(loadingRes, loadingRes, loadingRes);
    }
    
    public static DisplayImageOptions getDefaultPhotoOption(int loadingRes, int cornerRadiusPixels) {
        return getDefaultPhotoOption(loadingRes, loadingRes, loadingRes, cornerRadiusPixels);
    }
    
//    public static DisplayImageOptions getMinPhotoOption() {
//        return getDefaultPhotoOption(R.drawable.atet_min,R.drawable.atet_min,R.drawable.atet_min);
//    }
//    
//    public static DisplayImageOptions getMaxPhotoOption() {
//        return getDefaultPhotoOption(R.drawable.atet_max,R.drawable.atet_max,R.drawable.atet_max);
//    }
//    
//    public static DisplayImageOptions getMiddlePhotoOption() {
//        return getDefaultPhotoOption(R.drawable.atet_mid,R.drawable.atet_mid,R.drawable.atet_mid);
//    }
}
