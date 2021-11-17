package hmx.exifinterface.media.utils;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.app.AbilityContext;
import ohos.app.Context;
import ohos.bundle.IBundleManager;
import ohos.utils.net.Uri;

public class AppUtil {
    private static final String TAG = "AppUtil";

    //判断包名是否可用
    public static boolean checkApkExist(Context context, String appPkg) {
        if (context == null) {
            return false;
        }
        try {
            IBundleManager bundleManager = context.getBundleManager();
            return bundleManager.isApplicationEnabled(appPkg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 跳转到 应用商城
     */
    public static void startAppMarketDetailPage(AbilityContext context, String appPkg) {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withAction("android.intent.action.VIEW")
                .withUri(Uri.parse("market://details?id=" + appPkg))
                .withBundleName("com.huawei.appmarket")
                .withFlags(Intent.FLAG_NOT_OHOS_COMPONENT | Intent.FLAG_ABILITY_NEW_MISSION | Intent.FLAG_ABILITY_CLEAR_MISSION)
                .build();
        intent.setOperation(operation);
        startAbility(context, intent);
    }

    public static void startAbility(AbilityContext context, Intent intent) {
        if (context instanceof Ability) {
            ((Ability) context).startAbility(intent);
        } else if (context instanceof AbilitySlice) {
            ((AbilitySlice) context).startAbility(intent);
        } else {
            context.startAbility(intent, 0);
        }
    }

    /**
     * ELSA以后的机器，图库的包名是com.huawei.photos
     * LION及以前的机器，图库包名是com.android.gallery3d
     */
    private static final String ALBUM_NAME_1 = "com.huawei.photos";
    private static final String ALBUM_NAME_2 = "com.android.gallery3d";

    public static String getAlbumPackageName(Context context) {
        if (checkApkExist(context, ALBUM_NAME_1)) {
            return ALBUM_NAME_1;
        }
        return ALBUM_NAME_2;
    }

    public static void openDefaultBrowser(AbilityContext context, String url) {
        Intent intent = new Intent();
        Operation mOperation;
        mOperation = new Intent.OperationBuilder()
                .withUri(Uri.parse(url))
                .withFlags(Intent.FLAG_NOT_OHOS_COMPONENT)
                .withAction("android.intent.action.VIEW")
                .build();
        intent.setOperation(mOperation);
        startAbility(context, intent);
    }
}
