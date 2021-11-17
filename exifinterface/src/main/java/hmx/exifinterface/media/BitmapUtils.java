package hmx.exifinterface.media;

import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;

class BitmapUtils {
    public static PixelMap decodeBitmap(byte[] bytes) {
        ImageSource.SourceOptions srcOps = new ImageSource.SourceOptions();
        ImageSource imageSource = ImageSource.create(bytes, srcOps);
        try {
            ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
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
}
