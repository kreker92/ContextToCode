package tmt.analyze;

import com.google.gson.internal.LinkedTreeMap;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.panels.HorizontalBox;

import java.awt.*;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebView;

import javax.swing.*;

import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.prompt.PromptSupport;
import tmt.util.Actions;

/**
 * ContextHelper's side panel.
 */
public class ContextHelperPanel extends JPanel implements Runnable {
    private final int SPLIT_DIVIDER_POSITION = 205;
    private String ID_TOOL_WINDOW = "Context Helper";

//    private final ContextHelperProjectComponent contextHelperProjectComponent;

    private final JTextArea  descrTextField1;
    private final JTextArea descrTextField2;
    private final JTextArea descrTextField3;
    private final JTextField queryJTextField1;
    private final JTextField queryJTextField2;
    private final JTextField queryJTextField3;



    private WebView webView;

    private final JBCheckBox checkBox;
    private Object selectedItem;

    private Project project;

    private ToolWindow toolWindow;

    private Actions action;
    private String code1;
    private String code2;
    private String code3;


    public ContextHelperPanel(Project pr, Actions act_) {
        project = pr;

        action = act_;

        ToolWindowManager toolWindowMgr = ToolWindowManager.getInstance(project);
        toolWindow = toolWindowMgr.getToolWindow("Context Helper");
        if (toolWindow == null)
        {
            toolWindow = toolWindowMgr.registerToolWindow("Context Helper", true, ToolWindowAnchor.BOTTOM, true);
        }
//            val content = ContentFactory.SERVICE.getInstance().createContent(viewerPanel, "", false)
//            toolWindow.contentManager.addContent(content)
//            toolWindow.icon = IconLoader.getIcon(ICON_PATH_TOOL_WINDOW)
        toolWindow.show(this);

//        this.contextHelperProjectComponent = contextHelperProjectComponent;
        this.queryJTextField1 = new JTextField();
        this.queryJTextField2 = new JTextField();
        this.queryJTextField3 = new JTextField();

        this.descrTextField1 = new JTextArea();
        this.descrTextField1.setOpaque(false);
        this.descrTextField1.setLineWrap(true);
        this.descrTextField1.setWrapStyleWord(true);

        this.descrTextField2 = new JTextArea();
        this.descrTextField2.setOpaque(false);
        this.descrTextField2.setLineWrap(true);
        this.descrTextField2.setWrapStyleWord(true);

        this.descrTextField3 = new JTextArea();
        this.descrTextField3.setOpaque(false);
        this.descrTextField3.setLineWrap(true);
        this.descrTextField3.setWrapStyleWord(true);
        this.checkBox = new JBCheckBox("Do you find this item helpful to the problem?");

        configureGui();
    }

    /**
     * Configures the panel's UI.
     */
    private void configureGui() {
        PromptSupport.setPrompt("", queryJTextField1);
        PromptSupport.setPrompt("", queryJTextField2);
        PromptSupport.setPrompt("", queryJTextField3);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new VerticalLayout());

        topPanel.add(descrTextField1);
        topPanel.add(queryJTextField1);
        topPanel.add(descrTextField2);
        topPanel.add(queryJTextField2);
        topPanel.add(descrTextField3);
        topPanel.add(queryJTextField3);

        queryJTextField1.setVisible(false);
        queryJTextField2.setVisible(false);
        queryJTextField3.setVisible(false);
        descrTextField1.setVisible(false);
        descrTextField2.setVisible(false);
        descrTextField3.setVisible(false);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.PAGE_START);

        JFXPanel jfxPanel = new JFXPanel();
        Platform.setImplicitExit(false);

        checkBox.setVisible(true);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        bottomPanel.add(jfxPanel, BorderLayout.CENTER);
      /*  queryJTextField1.addActionListener(actionEvent -> {
            System.err.println("insert:"+code1);
            action.insert(code1);
        });
        queryJTextField2.addActionListener(actionEvent -> {
            System.err.println("insert:"+code2);
            action.insert(code2);
        });
        queryJTextField3.addActionListener(actionEvent -> {
            System.err.println("insert:"+code3);
            action.insert(code3);
        });*/

    }

    private HorizontalBox buildQualityBox() {
//        final ProcessorMethodEnum[] processorMethods = ProcessorMethodEnum.values();
//        ComboBox<ProcessorMethodEnum> methodComboBox = buildComboBoxFor(processorMethods);
//        methodComboBox.addActionListener(e -> {
//            ProcessorMethodEnum method = (ProcessorMethodEnum) methodComboBox.getSelectedItem();
//            contextHelperProjectComponent.changeProcessorMethodTo(Objects.requireNonNull(method));
//        });
//
//        final DatasetEnum[] datasets = DatasetEnum.values();
//        ComboBox<DatasetEnum> datasetComboBox = buildComboBoxFor(datasets);
//        datasetComboBox.addActionListener(e -> {
//            DatasetEnum dataset = (DatasetEnum) datasetComboBox.getSelectedItem();
//            contextHelperProjectComponent.changeDatasetTo(Objects.requireNonNull(dataset));
//        });
//
//        final LookupClientFactory[] lookupFactories = LookupClientFactory.values();
//        ComboBox<LookupClientFactory> lookupFactoryComboBox = buildComboBoxFor(lookupFactories);
//        lookupFactoryComboBox.addActionListener(e -> {
//            LookupClientFactory factory = (LookupClientFactory) lookupFactoryComboBox.getSelectedItem();
//            contextHelperProjectComponent.changeLookupFactoryTo(Objects.requireNonNull(factory));
//        });

        final JButton evaluateButton = new JButton("Evaluate");
        evaluateButton.addActionListener(action -> {
            Editor editor =
                    FileEditorManager.getInstance(project)
                            .getSelectedTextEditor();
            if (editor != null) {
                DataContext dataContext = DataManager.getInstance().getDataContext(editor.getComponent());
                AnAction testContextsAction =
                        ActionManager.getInstance().getAction("TestContextsAction");
                testContextsAction.actionPerformed(
                        AnActionEvent.createFromAnAction(testContextsAction, null, "", dataContext));
            }
        });

        final HorizontalBox qualityBox = new HorizontalBox();
//        qualityBox.add(methodComboBox);
//        qualityBox.add(datasetComboBox);
//        qualityBox.add(lookupFactoryComboBox);
        qualityBox.add(evaluateButton);
        return qualityBox;
    }

    private <E extends Enum<?>> ComboBox<E> buildComboBoxFor(E[] values) {
        Font plainFont = getFont();
        Font boldFont = new Font(plainFont.getName(), Font.BOLD, (int) (plainFont.getSize() * 0.8));

        ComboBox<E> comboBox = new ComboBox<>(values);
        comboBox.setSelectedIndex(0);
        comboBox.setFont(boldFont);
        comboBox.setRenderer(new ListCellRendererWrapper<E>() {
            @Override
            public void customize(JList list, E value, int index, boolean selected,
                                  boolean hasFocus) {
                setText(value.name());
                setFont(plainFont);
            }
        });
        return comboBox;
    }

    /**
     * Updates the underlying data model and JTree element.
     */
//    public void updatePanelWithQueryResults(StackExchangeQuestionResults queryResults) {
//        contextHelperProjectComponent.sendQuestionsMessage(
//                queryResults.getQueryContent(),
//                queryResults.getQuestions().stream()
//                        .map(Question::getQuestionId)
//                        .collect(Collectors.toList()));
//        queryJTextField.setText(queryResults.getQueryContent());
//        treeModel = new StackExchangeThreadsTreeModel(queryResults.getQuestions());
//        tree.setModel(treeModel);
//        treeScrollPane.getVerticalScrollBar().setValue(0);
//        showPanel();
//    }

    private void showPanel() {
        Content content = ContentFactory.SERVICE.getInstance().createContent(this, "", false);
        toolWindow.getContentManager().removeAllContents(true);
        toolWindow.getContentManager().addContent(content);
    }

    public void clearPanel() {
        Content content = ContentFactory.SERVICE.getInstance().createContent(this, "", false);
        toolWindow.getContentManager().removeAllContents(true);
        toolWindow.getContentManager().addContent(content);
      }

    public void setQueryingStatus(ArrayList<LinkedTreeMap<String, String>> res) {
//        for ( r : res)
        if (res.size() > 0) {
            queryJTextField1.setVisible(true);
            descrTextField1.setVisible(true);
            descrTextField1.setText(res.get(0).get("documentation"));
            code1 = res.get(0).get("prediction");
            queryJTextField1.setText(code1);
        }
        if (res.size() > 1) {
            queryJTextField2.setVisible(true);
            descrTextField2.setVisible(true);
            descrTextField2.setText(res.get(1).get("documentation"));
            code2 = res.get(1).get("prediction");
            queryJTextField2.setText(code2);
        }
        if (res.size() > 2) {
            queryJTextField3.setVisible(true);
            descrTextField3.setVisible(true);
            descrTextField3.setText(res.get(2).get("documentation"));
            code3 = res.get(2).get("prediction");
            queryJTextField2.setText(code3);
        }
        showPanel();
    }

    @Override
    public void run() {
    }

//    @Override
//    public void renderHtml(String bodyHtml) {
//        String highlightedHtml = highlightHtml(bodyHtml);
//        WebEngine engine = webView.getEngine();
//        URL prettifyUrl = this.getClass().getResource("/prettify.js");
//        Platform.runLater(() -> {
//            String content = "<html>\n"
//                    + "<head>\n"
//                    + "<script type=\"text/javascript\" src=\"" + prettifyUrl.toString() + "\"></script>\n"
//                    + "</head>\n"
//                    + "<body>\n"
//                    + highlightedHtml
//                    .replace("<code>", "<pre class=\"prettyprint\">")
//                    .replace("</code>", "</pre>")
//                    + "</body>\n"
//                    + "</html>";
//            engine.loadContent(content, "text/html");
//        });
//    }

   /* private String highlightHtml(String bodyHtml) {
        String[] words = queryJTextField.getText().split("\\s+");
        String highlightedHtml = bodyHtml;
        for (String word : words) {
            highlightedHtml = highlightedHtml.replaceAll(
                    Pattern.quote(word), "<span class='highlight'>" + word + "</span>");
        }
        return highlightedHtml;
    }*/

//    @Override
//    public void questionClicked(Question question) {
//        selectedItem = question;
//        contextHelperProjectComponent.sendClicksMessage(Long.toString(question.getQuestionId()));
//        enableCheckBox();
//    }
//
//    @Override
//    public void answerClicked(Answer answer) {
//        selectedItem = answer;
//        contextHelperProjectComponent.sendClicksMessage(Long.toString(answer.getAnswerId()));
//        enableCheckBox();
//    }

    private void enableCheckBox() {
        checkBox.setVisible(true);
        checkBox.setSelected(false);
        checkBox.setEnabled(true);
    }
}