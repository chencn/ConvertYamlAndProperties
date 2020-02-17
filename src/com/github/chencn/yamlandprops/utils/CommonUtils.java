package com.github.chencn.yamlandprops.utils;

import com.github.chencn.yamlandprops.consts.Constant;
import com.github.chencn.yamlandprops.consts.MsgConsts;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author xqchen
 */
public class CommonUtils {
    /**
     * 获取文件
     *
     * @param event
     * @param showNotifications
     * @return PsiFile
     */
    @NotNull
    public static PsiFile getSelectedFile(AnActionEvent event, boolean showNotifications) {
        //获取当前打开的文件
        PsiFile selectedFile = event.getData(LangDataKeys.PSI_FILE);
        if (selectedFile == null) {
            if (showNotifications) {
                Notifications.Bus.notify(new Notification(Constant.GROUP_DISPLAY_ID, MsgConsts.NO_FILE_SELECTED,
                        MsgConsts.SELECT_FILE_FIRST, NotificationType.ERROR));
            }
            return null;
        }

        return selectedFile;
    }

    /**
     * 获取打开的文件类型
     *
     * @param event
     * @param showNotifications
     */
    @NotNull
    public static String getFileType(AnActionEvent event, boolean showNotifications) {
        //获取打开的文件
        PsiFile selectedFile = getSelectedFile(event, showNotifications);
        return selectedFile.getFileType().getName();
    }
}
