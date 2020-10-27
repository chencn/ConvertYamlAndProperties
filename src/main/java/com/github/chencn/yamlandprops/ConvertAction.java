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
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author xqchen
 */
public class ConvertAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        Presentation presentation = event.getPresentation();
        // get file type
        final String fileType = CommonUtils.getFileType(event, false);
        //根据类型动态控制Action的隐藏显示
        if (!StringUtil.isEmpty(fileType) && presentation.isEnabled()) {
            event.getPresentation().setEnabledAndVisible(Constant.YAML.equals(fileType) || Constant.PROPERTIES.equals(fileType));
        } else {
            event.getPresentation().setEnabledAndVisible(false);
        }

    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        // get file type
        final String fileType = CommonUtils.getFileType(event, true);
        final PsiFile selectedFile = CommonUtils.getSelectedFile(event, true);
        if (StringUtil.isEmpty(fileType) || null == selectedFile) {
            return;
        }
        final VirtualFile file = selectedFile.getVirtualFile();
        if (null == file) {
            Notifications.Bus.notify(new Notification(Constant.GROUP_DISPLAY_ID, MsgConsts.NO_FILE_SELECTED,
                    MsgConsts.SELECT_FILE_FIRST, NotificationType.ERROR));
            return;
        }
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                String content = new String(file.contentsToByteArray());
                if (StringUtil.isEmpty(content)) {
                    Notifications.Bus.notify(new Notification(Constant.GROUP_DISPLAY_ID, MsgConsts.FILE_NOT_EMPTY,
                            MsgConsts.SELECT_FILE_FIRST, NotificationType.ERROR));
                    return;
                }
                //YAML文件处理
                if (Constant.YAML.equals(fileType)) {
                    String yamlContent = Yaml2Props.fromContent(content).convert();
                    file.setCharset(file.getCharset());
                    file.rename(this, file.getNameWithoutExtension() + Constant.PROPERTIES_SUFFIX);
                    file.setBinaryContent(yamlContent.getBytes());

                    Notifications.Bus.notify(new Notification(Constant.GROUP_DISPLAY_ID, MsgConsts.SUCCESS,
                            MsgConsts.YAML2PROPS, NotificationType.INFORMATION));
                }//PROPERTIES文件处理
                else if (Constant.PROPERTIES.equals(fileType)) {
                    String propsContent = Props2Yaml.fromContent(content).convert();
                    file.rename(this, file.getNameWithoutExtension() + Constant.YAML_SUFFIX);
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
        });
    }

}
