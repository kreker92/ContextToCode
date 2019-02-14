package tmt.util;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import tmt.analyze.ContextHelperPanel;

public class Actions {

    public static void insert(String q, int line_num, Project project) {
        Editor ed = FileEditorManager.getInstance(project).getSelectedTextEditor();

        CommandProcessor.getInstance().executeCommand(project, () -> {
            final Document document = ed.getDocument();
            document.insertString(document.getLineEndOffset(line_num), "\n" + q );
            ed.getCaretModel().getPrimaryCaret().moveToOffset(document.getLineEndOffset(line_num) + q.length() + 1);
        }, null, null);
//        ContextHelperPanel.getPanel(project).clearPanel();
    }
}
