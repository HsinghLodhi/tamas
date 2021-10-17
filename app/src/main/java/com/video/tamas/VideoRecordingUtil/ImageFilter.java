package com.video.tamas.VideoRecordingUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.uvstudio.him.photofilterlibrary.PhotoFilter;
import com.video.tamas.R;

public class ImageFilter {

    public static Bitmap getFilterInstance(Filters filters, Context context) {
        PhotoFilter photoFilter = new PhotoFilter();
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_filter_img_foreground);
        switch (filters) {
            case BILATERAL:
                return photoFilter.one(context, bitmap);
            case BOX_BLUR:
                return photoFilter.two(context, bitmap);
            case BULGE_DISTORTION:
                return photoFilter.three(context, bitmap);
            case CGA_COLOR_SPACE:
                return photoFilter.four(context, bitmap);
            case GAUSSIAN_BLUR:
                return photoFilter.five(context, bitmap);
            case GRAY_SCALE:
                return photoFilter.six(context, bitmap);
            case INVERT:
                return photoFilter.seven(context, bitmap);
            case LOOKUP_TABLE:
                return photoFilter.eight(context, bitmap);
            case SEPIA:
                return photoFilter.nine(context, bitmap);
            case SHARPEN:
                return photoFilter.ten(context, bitmap);
            case SPHERE_REFRACTION:
                return photoFilter.eleven(context, bitmap);
            case TONE_CURVE:
                return photoFilter.twelve(context, bitmap);
            case TONE:
                return photoFilter.thirteen(context, bitmap);
            case WEAKPIXELINCLUSION:
                return photoFilter.fourteen(context, bitmap);
            default:
                return bitmap;
        }

    }

}
