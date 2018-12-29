package tmt.util;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import tmt.analyze.ContextHelperPanel;

public class Actions {

    private Editor ed;
    private Project project;

    public Actions(Project project) {
        ed = FileEditorManager.getInstance(project).getSelectedTextEditor();
    }

    public void insert(String q, int line_num) {
        CommandProcessor.getInstance().executeCommand(project, () -> {
            final Document document = ed.getDocument();
            document.insertString(document.getLineEndOffset(line_num), "\n" + q );
            ed.getCaretModel().getPrimaryCaret().moveToOffset(document.getLineEndOffset(line_num) + q.length() + 1);
        }, null, null);
        ContextHelperPanel.getPanel(project).clearPanel();
    }

    public String send(String request) throws Exception {
        return Util.sendGet(request);
    }
}
