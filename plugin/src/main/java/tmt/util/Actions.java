package tmt.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;

public class Actions {

    Editor ed;
    Project project;

    public Actions(Project project) {
        ed = FileEditorManager.getInstance(project).getSelectedTextEditor();
    }

    public void insert(String q, int line_num) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                new WriteCommandAction.Simple(project) {
                    @Override
                    public void run() {
                        // almost the same thing but manipulating plain text of the document instead of PSI
                        final Document document = ed.getDocument();
                        document.insertString(document.getLineEndOffset(line_num), "\n" + q );
                        ed.getCaretModel().getPrimaryCaret().moveToOffset(document.getTextLength());
                    }
                }.execute();
            }
        });
    }

    public String send(String request) throws Exception {
        return Util.sendGet(request);
    }
}
