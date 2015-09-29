package com.angcyo.zcgetpkgname;

import java.io.File;
import java.util.Set;

import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.androlib.res.data.ResPackage;

/**
 * Created by angcyo on 15-09-29-029.
 */
public class Apk {
    public static String getPkgNameFromApk(String apkPath) {
        String pkgName = "";
        ApkDecoder d = new ApkDecoder();
        d.setApkFile(new File(apkPath));
        try {
            Set<ResPackage> p = d.getResTable().listMainPackages();
            for (ResPackage r : p) {
                pkgName = r.getName();
                break;
            }
        } catch (AndrolibException e) {
            e.printStackTrace();
        }
        return pkgName;
    }

    public static boolean isEmpty(String string) {
        return (string == null || string.trim().length() < 1);
    }
}
