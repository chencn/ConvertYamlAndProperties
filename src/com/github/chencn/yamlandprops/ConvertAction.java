package com.github.chencn.yamlandprops;

import com.github.chencn.yamlandprops.consts.Constant;
import com.github.chencn.yamlandprops.consts.MsgConsts;
import com.github.chencn.yamlandprops.props2yaml.Props2Yaml;
import com.github.chencn.yamlandprops.utils.CommonUtils;
import com.github.chencn.yamlandprops.yaml2props.Yaml2Props;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ConvertAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        //获取文件类型
        final String fileType = CommonUtils.getFileType(event, false);
        //根据类型动态控制Action的隐藏显示
        event.getPresentation().setEnabledAndVisible(fileType != null && (Constant.YAML.equals(fileType) || Constant.PROPERTIES.equals(fileType)));
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        //获取文件类型
        final String fileType = CommonUtils.getFileType(event, true);
        final PsiFile selectedFile = CommonUtils.getSelectedFile(event, true);
        if (null == fileType || null == selectedFile) {
            return;
        }

        final VirtualFile file = selectedFile.getVirtualFile();

        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                String content = new String(file.contentsToByteArray());
                //YAML文件处理
                if (Constant.YAML.equals(fileType)) {
                    String yamlContent = Yaml2Props.fromContent(content).convert();
                    file.rename((Object) this, file.getNameWithoutExtension() + Constant.PROPERTIES_SUFFIX);
                    file.setBinaryContent(yamlContent.getBytes());
                    Notifications.Bus.notify(new Notification(Constant.GROUP_DISPLAY_ID, MsgConsts.SUCCESS,
                            MsgConsts.YAML2PROPS, NotificationType.INFORMATION));
                }//PROPERTIES文件处理
                else if (Constant.PROPERTIES.equals(fileType)) {
                    String propsContent = Props2Yaml.fromContent(content).convert();
                    file.rename((Object) this, file.getNameWithoutExtension() + Constant.YAML_SUFFIX);
                    file.setBinaryContent(propsContent.getBytes());
                    Notifications.Bus.notify(new Notification(Constant.GROUP_DISPLAY_ID, MsgConsts.SUCCESS,
                            MsgConsts.PROPS2YAML, NotificationType.INFORMATION));
                } else {
                    Notifications.Bus.notify(new Notification(Constant.GROUP_DISPLAY_ID, MsgConsts.INCORRECT_FILE_SELECTED,
                            MsgConsts.SELECT_PROPS_OR_YAML_FIRST, NotificationType.ERROR));
                }

            } catch (IOException e) {
                Notifications.Bus.notify(new Notification(Constant.GROUP_DISPLAY_ID,
                        MsgConsts.CANNOT_RENAME_FILE, e.getMessage(), NotificationType.ERROR));
            }
            return;
        });
    }

}
