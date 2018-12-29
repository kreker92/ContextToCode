package tmt.analyze;

import com.google.gson.internal.LinkedTreeMap;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javafx.application.Platform;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * ContextHelper's side panel.
 */
public class ContextHelperPanel extends JPanel implements Runnable {
    private static ContextHelperPanel instance = null;
    public static ContextHelperPanel getPanel(Project pr) {
        if (instance == null) {
            instance = new ContextHelperPanel(pr);
        }

        return instance;
    }

    private static final int SPLIT_DIVIDER_POSITION = 205;
    private static final String ID_TOOL_WINDOW = "Context Helper";
    private static final int HINT_COUNT = 3;

    private final JTextArea[] descrTextFields;
    private final JTextField[] queryJTextFields;
    //private final JBCheckBox checkBox;

    private Project project;
    private ToolWindow toolWindow;

    private ContextHelperPanel(Project pr) {
        project = pr;
        ToolWindowManager toolWindowMgr = ToolWindowManager.getInstance(project);
        toolWindow = toolWindowMgr.getToolWindow(ID_TOOL_WINDOW);
        if (toolWindow == null) {
            toolWindow = toolWindowMgr.registerToolWindow(ID_TOOL_WINDOW, true, ToolWindowAnchor.RIGHT, true);
        }

        queryJTextFields = new JTextField[HINT_COUNT];
        descrTextFields = new JTextArea[HINT_COUNT];

        for (int i = 0; i < HINT_COUNT; i++) {
            queryJTextFields[i] = new JTextField();
            queryJTextFields[i].setEditable(false);

            descrTextFields[i] = new JTextArea();
            descrTextFields[i].setOpaque(false);
            descrTextFields[i].setLineWrap(true);
            descrTextFields[i].setWrapStyleWord(true);
        }

        //this.checkBox = new JBCheckBox("Do you find this item helpful to the problem?");

        for(int i = 0;  i < HINT_COUNT; i++) {
            PromptSupport.setPrompt("", queryJTextFields[i]);
        }

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new VerticalLayout());

        for (int i = 0; i < HINT_COUNT; i++) {
            topPanel.add(descrTextFields[i]);
            topPanel.add(queryJTextFields[i]);

            queryJTextFields[i].setVisible(false);
            descrTextFields[i].setVisible(false);
        }

        this.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.PAGE_START);

        Platform.setImplicitExit(false);

        toolWindow.show(this);
    }

    private void showPanel() {
        Content content = ContentFactory.SERVICE.getInstance().createContent(this, "", false);
        toolWindow.getContentManager().removeAllContents(true);
        toolWindow.getContentManager().addContent(content);
    }

    public void clearPanel() {
        for(int i = 0; i < HINT_COUNT; i++) {
            queryJTextFields[i].setVisible(false);
            descrTextFields[i].setVisible(false);
            descrTextFields[i].setText("");
            queryJTextFields[i].setText("");
        }
    }

    public void setQueryingStatus(ArrayList<LinkedTreeMap<String, String>> res) {
        clearPanel();

        for(int i = 0; i < HINT_COUNT && i < res.size(); i++) {
            queryJTextFields[i].setVisible(true);
            descrTextFields[i].setVisible(true);
            descrTextFields[i].setText(res.get(i).get("documentation"));
            queryJTextFields[i].setText(res.get(i).get("prediction"));
        }

        showPanel();
    }

    @Override
    public void run() {}

    private void enableCheckBox() {
        //checkBox.setVisible(true);
        //checkBox.setSelected(false);
        //checkBox.setEnabled(true);
    }
}