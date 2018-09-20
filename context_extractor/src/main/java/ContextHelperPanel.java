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
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.HorizontalBox;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.prompt.PromptSupport;
/**
 * ContextHelper's side panel.
 */
public class ContextHelperPanel extends JPanel implements Runnable {
    private final int SPLIT_DIVIDER_POSITION = 205;
    private String ID_TOOL_WINDOW = "Context Helper";

//    private final ContextHelperProjectComponent contextHelperProjectComponent;

    private final JProgressBar progressBar;
    private final JTextField queryJTextField;

    private WebView webView;

    private final JBCheckBox checkBox;
    private Object selectedItem;

    private Project project;

    private ToolWindow toolWindow;

    private Actions action;
    private String query;


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
        this.progressBar = new JProgressBar();
        this.queryJTextField = new JTextField();
        this.checkBox = new JBCheckBox("Do you find this item helpful to the problem?");

        configureGui();
    }

    /**
     * Configures the panel's UI.
     */
    private void configureGui() {
        PromptSupport.setPrompt("", queryJTextField);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new VerticalLayout());
        topPanel.add(progressBar);
//    topPanel.add(buildQualityBox());
        topPanel.add(queryJTextField);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.PAGE_START);

        JFXPanel jfxPanel = new JFXPanel();
        Platform.setImplicitExit(false);
//        Platform.runLater(() -> {
//            webView = new WebView();
//            webView.getEngine().setUserStyleSheetLocation(
//                    getClass().getResource("/style.css").toString());
//            webView.addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent e) -> {
//                if (e.getCode() == KeyCode.ADD || e.getCode() == KeyCode.EQUALS
//                        || e.getCode() == KeyCode.PLUS) {
//                    webView.setZoom(webView.getZoom() * 1.1);
//                } else if (e.getCode() == KeyCode.SUBTRACT || e.getCode() == KeyCode.MINUS) {
//                    webView.setZoom(webView.getZoom() / 1.1);
//                }
//            });
//            jfxPanel.setScene(new Scene(webView));
//        });

        checkBox.setVisible(true);
//        checkBox.addItemListener(itemEvent -> {
//            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
//                checkBox.setEnabled(false);
//
//                Long id = null;
//                if (selectedItem instanceof Question) {
//                    Question question = (Question) selectedItem;
//                    id = question.getQuestionId();
//                } else if (selectedItem instanceof Answer) {
//                    Answer answer = (Answer) selectedItem;
//                    id = answer.getAnswerId();
//                }
//                if (id != null) {
//                    contextHelperProjectComponent.sendHelpfulMessage(Long.toString(id));
//                }
//            }
//        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
//    bottomPanel.add(checkBox, BorderLayout.PAGE_START);
        bottomPanel.add(jfxPanel, BorderLayout.CENTER);
        queryJTextField.addActionListener(actionEvent -> {
            action.insert("\n \n" +
                    "            Connection conn = DriverManager.getConnection(\"jdbc:sqlite:\" + tmpFile.getAbsolutePath(), prop);\n" +
                    "\n" +
                    "            try {\n" +
                    "                ResultSet rs;\n" +
                    "                String query = \"\";\n" +
                    "                Statement stat = conn.createStatement();\n" +
                    "                \n" +
                    "                rs = stat.executeQuery(query);\n" +
                    "            } finally {\n" +
                    "                conn.close();\n" +
                    "            }");
        });
//        @SuppressWarnings("SuspiciousNameCombination")
//        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, treeScrollPane, bottomPanel);
//        splitPane.setDividerLocation(SPLIT_DIVIDER_POSITION);
//        add(splitPane, BorderLayout.CENTER);
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

    public void setQueryingStatus(String q_) {
        if (q_.trim().equals("6"))
            queryJTextField.setText("Open DB connection");
        else if (q_.trim().equals("7"))
            queryJTextField.setText("None");
        else if (q_.trim().equals("0"))
            queryJTextField.setText("Loading...");
        showPanel();
//        if (isQuerying) {
//            progressBar.setIndeterminate(true);
         query = q_;
//            treeModel = new StackExchangeThreadsTreeModel(null);
//            tree.setModel(treeModel);
//            renderHtml("");
//        } else {
//            progressBar.setIndeterminate(false);
//        }
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

    private String highlightHtml(String bodyHtml) {
        String[] words = queryJTextField.getText().split("\\s+");
        String highlightedHtml = bodyHtml;
        for (String word : words) {
            highlightedHtml = highlightedHtml.replaceAll(
                    Pattern.quote(word), "<span class='highlight'>" + word + "</span>");
        }
        return highlightedHtml;
    }

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