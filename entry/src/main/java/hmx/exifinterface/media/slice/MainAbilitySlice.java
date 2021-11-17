package hmx.exifinterface.media.slice;

import hmx.exifinterface.media.ResourceTable;
import hmx.exifinterface.media.utils.AppUtil;
import hmx.exifinterface.media.utils.BitmapUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.media.image.PixelMap;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.net.Uri;

public class MainAbilitySlice extends AbilitySlice {

    private Image mPreviewImage;
    public static final String ACTION_PICK = "android.intent.action.PICK";
    /**
     * used for third part app
     */
    public static final String KEY_ONLY_LOCAL = "only_local";
    public static final int FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS = 0x00800000;
    private static final int REQUEST_CODE_ALBUM = 0x100;

    private static EventHandler mUIHandler = new EventHandler(EventRunner.getMainEventRunner());

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        mPreviewImage = findComponentById(ResourceTable.Id_image);
        findComponentById(ResourceTable.Id_albumSelect).setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                pickPhoto();
            }
        });
    }

    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("vnd.android.cursor.dir/image");
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName(AppUtil.getAlbumPackageName(getContext()))
                .withFlags(Intent.FLAG_NOT_OHOS_COMPONENT)
                .withAction(ACTION_PICK)
                .build();
        intent.setOperation(operation);
        intent.setParam(KEY_ONLY_LOCAL, true);
        intent.addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startAbilityForResult(intent, REQUEST_CODE_ALBUM);
    }

    @Override
    protected void onAbilityResult(int requestCode, int resultCode, Intent resultData) {
        super.onAbilityResult(requestCode, resultCode, resultData);
        if (resultCode != -1) {
            return;
        }
        if (requestCode == REQUEST_CODE_ALBUM) {
            String chooseImgUri = resultData.getUriString();
            String chooseImgId = null;
            if (chooseImgUri.lastIndexOf("%3A") != -1) {
                chooseImgId = chooseImgUri.substring(chooseImgUri.lastIndexOf("%3A") + 3);
            } else {
                chooseImgId = chooseImgUri.substring(chooseImgUri.lastIndexOf('/') + 1);
            }
            Uri uri = Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, chooseImgId);
            handleAlbumPicture(uri);
        }
    }

    private void handleAlbumPicture(Uri uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PixelMap pixelMap = BitmapUtils.decodeBitmap(getApplicationContext(), uri, 2048, 2048, true, true);
                mUIHandler.postTask(new Runnable() {
                    @Override
                    public void run() {
                        mPreviewImage.setPixelMap(pixelMap);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
