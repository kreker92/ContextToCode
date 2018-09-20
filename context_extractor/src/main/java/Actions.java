import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;

public class Actions {

    Editor ed;

    public Actions(Editor ed_) {
        ed = ed_;
    }

    public void insert (String q) {
        int cursorOffset = ed.getCaretModel().getOffset();
        Document document = ed.getDocument();

        CommandProcessor.getInstance().executeCommand(null, new Runnable() {
            @Override
            public void run() {
                Document document = ed.getDocument();
                String templateText = document.getText();
                document.replaceString(0, document.getTextLength(), templateText);
                document.insertString(cursorOffset, q);
            }
        }, null, null);
    }

    public String send(String request) throws Exception {
        return Util.sendGet(request);
    }
}
