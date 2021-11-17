package hmx.exifinterface.media.utils;

import hmx.exifinterface.media.ExifInterface;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.app.Context;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.Size;
import ohos.utils.net.Uri;
import org.jetbrains.annotations.NotNull;

import java.io.FileDescriptor;

public class BitmapUtils {

    public static PixelMap decodeBitmap(Context context, Uri uri, int desireWidth, int desireHeight, boolean keepRatio, boolean adjustAngle) {
        DataAbilityHelper helper = DataAbilityHelper.creator(context);
        FileDescriptor fd = null;
        ImageSource imageSource = null;
        try {
            fd = helper.openFile(uri, "r");

            ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();
            imageSource = ImageSource.create(fd, srcOpts);
            ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
            Size bmpSize = getBitmapSize(context, uri);

            int rotate = 0;
            if (adjustAngle) {
                int orientation = getExifOrientationFromUri(context, uri);
                rotate = getRotateAngleFromOrientation(orientation);
                decodingOptions.rotateDegrees = rotate;
                if (rotate == 90 || rotate == 270) {
                    bmpSize = new Size(bmpSize.height, bmpSize.width);
                }
            }

            float bmpRatio = bmpSize.width / (float) bmpSize.height;
            int width = desireWidth;
            int height = desireHeight;
            if (keepRatio) {
                if (bmpRatio > 1.0f) {
                    width = desireWidth;
                    height = (int) (width / bmpRatio);
                } else {
                    height = desireHeight;
                    width = (int) (height * bmpRatio);
                }
            }
            decodingOptions.desiredSize = new Size(width, height);
            return imageSource.createPixelmap(decodingOptions);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imageSource != null) {
                imageSource.release();
            }
        }
        return null;
    }

    public static int getExifOrientationFromUri(Context context, @NotNull Uri uri) {
        try {
            DataAbilityHelper helper = DataAbilityHelper.creator(context);
            FileDescriptor fd = helper.openFile(uri, "r");
            ExifInterface exifInterface = new ExifInterface(fd);
            return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ExifInterface.ORIENTATION_UNDEFINED;
    }


    public static Size getBitmapSize(Context context, Uri uri) {
        DataAbilityHelper helper = DataAbilityHelper.creator(context);
        FileDescriptor fd = null;
        ImageSource imageSource = null;
        try {
            fd = helper.openFile(uri, "r");
            imageSource = ImageSource.create(fd, null);
            return imageSource.getImageInfo().size;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imageSource != null) {
                imageSource.release();
            }
        }
        return new Size(0, 0);
    }

    public static int getRotateAngleFromOrientation(int orientation) {
        int degree = 0;
        switch (orientation) {
            // 2 表示图像为左右倒镜。
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                degree = 0;
                break;
            // 3 表示图像顺时针旋转 180 度。
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            // 4 表示图像是倒置镜，也可以先水平翻转，顺时针旋转180度来表示。
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                degree = 180;
                break;
            // 5 表示图像绕左上<-->右下轴翻转，也可以先水平翻转，顺时针旋转270度表示。
            case ExifInterface.ORIENTATION_TRANSPOSE:
                degree = 90;
                break;
            // 6 表示图像顺时针旋转 90 度。
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            // 7 表示图像是绕右上<-->左下轴翻转的，也可以先水平翻转，顺时针旋转90度表示。
            case ExifInterface.ORIENTATION_TRANSVERSE:
                degree = -90;
                break;
            // 8 表示图像顺时针旋转 270 度。
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            default:
                degree = 0;
                break;
        }
        return degree;
    }
}
