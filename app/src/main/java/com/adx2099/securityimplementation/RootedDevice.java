package com.adx2099.securityimplementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class RootedDevice {

    public static boolean isPhoneRooted(){
        return (testKeyVerifier() || findDangerousPaths() || checkSuExists() || checkForBusyBox())? true:false;

    }

    private static boolean checkForBusyBox() {
        String [] binaryPaths = {
                "/ data / local /",
                "/ data / local / bin /",
                "/ data / local / xbin /",
                "/ sbin /",
                "/ su / bin /",
                " / system / bin / ",
                " /system/bin/.ext/ ",
                " / system / bin / failsafe / ",
                " / system / sd / xbin / ",
                " / system / usr / we-need-root / ",
                " / system / xbin / ",
                " /system/app/Superuser.apk ",
                " / cache ",
                " / data ",
                " / dev "
        };
        for (String path : binaryPaths) {
            File f = new File(path, "busybox");
            boolean fileExists = f.exists();
            if (fileExists) {
                return true;
            }
        }
        return false;

    }

    //---------------------------------------------------------------------------------------------
    /* Verificacion de Test-Keys
        Test-Keys tiene que ver con cómo se firma el kernel cuando se compila. De forma predeterminada,
        las Android ROMs de Google se crean con etiquetas release-keys. Test-Keys significa fueron firmadas
        con una key personalizada generada por un desarrollador externo.
     */
    private static boolean testKeyVerifier() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }
    //---------------------------------------------------------------------------------------------
    /*
        Las siguientes rutas son usualmente buscadas en dispositivos rooteados
    */
    private static boolean findDangerousPaths() {
        String[] binaryPaths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su",
                "/su/bin/su"};
        for (String path : binaryPaths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }
    //---------------------------------------------------------------------------------------------
    /*
     Verificar existencia de sudo con respecto al sistema de archivos
     */
    private static boolean checkSuExists() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

}
