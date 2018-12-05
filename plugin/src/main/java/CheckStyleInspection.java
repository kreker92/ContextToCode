import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
//import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.openapi.command.CommandProcessor;


import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileType;

import java.io.FileWriter;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import java.io.File;
import java.nio.file.Files;
import com.intellij.openapi.diagnostic.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Sets;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.module.Module;



public class CheckStyleInspection extends LocalInspectionTool {
    private List<PsiFile> myPsiFiles = new ArrayList<>();
    private PsiFileFactory factory;
    private Project project;
    private Editor ed;

    private static final Logger LOG = Logger.getInstance(CheckStyleInspection.class);


    public ProblemDescriptor[] checkFile(@NotNull final PsiFile psiFile,
                                         @NotNull final InspectionManager manager,
                                         final boolean isOnTheFly) {
        final Module module = moduleOf(psiFile);
        System.err.println("!");
        return null;//asProblemDescriptors(asyncResultOf(() -> inspectFile(psiFile, module, manager), NO_PROBLEMS_FOUND), manager);
    }

    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        System.err.println("#");
        return null;
    }

    private Module moduleOf(@NotNull final PsiFile psiFile) {
        return ModuleUtil.findModuleForPsiElement(psiFile);
    }

    public void actionPerformed(AnActionEvent event) {
        project = event.getProject();
        factory = PsiFileFactory.getInstance(project);
        System.err.print("!1");

        ed = event.getData(PlatformDataKeys.EDITOR);
        Document document = ed.getDocument();

        PsiFile fi = event.getData(LangDataKeys.PSI_FILE);
        Actions act = new Actions(ed);

        ContextHelperPanel helperComponent = new ContextHelperPanel(project, act);

        try {
            // case 1:
            if (false) {
                setUp();
            } else {
                // case 2:

                ArrayList<InnerContext> output_elements = new ArrayList<>();

                char ch = fi.getText().charAt(ed.getSelectionModel().getSelectionEnd() - 1);
                if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n' || ch == '/') {
                    //ed.getCaretModel().moveToLogicalPosition(new LogicalPosition(ed.getCaretModel().getLogicalPosition().line, ed.getCaretModel().getLogicalPosition().column));
                } else {
                    ed.getCaretModel().moveToLogicalPosition(new LogicalPosition(ed.getCaretModel().getLogicalPosition().line + 1, ed.getCaretModel().getLogicalPosition().column));
                    ed.getCaretModel().moveToOffset(document.getLineStartOffset(ed.getCaretModel().getLogicalPosition().line));
                }

//                parseFile(fi, output_elements, fi.getText(), ed.getSelectionModel().getSelectionEnd());

                String request = new Gson().toJson(output_elements);
                System.err.println(request);
//            System.err.print(Eval.sendGet("телефон"))

//            act.insert("\n\n ");

//            TimeUnit.SECONDS.sleep(1);

                helperComponent.setQueryingStatus(act.send(request));

//            CommandProcessor.getInstance().executeCommand(project, () -> getApplication().runWriteAction(() -> {
//                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
//                if (editor != null) {
//                    int offset = editor.getCaretModel().getOffset();
//                    Document document = editor.getDocument();
//                    String key = isXmlFile ?
//                            "@" + element.getTag() + "/" + element.getName()
//                            : "R." + element.getTag() + "." + element.getName();
//                    if (key != null) {
//                        document.insertString(offset, key);
//                        editor.getCaretModel().moveToOffset(offset + key.length());
//                    }
//                }
//            }), "InsertResultToEditor", "", UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);


//            helperComponent.process("{psiElement.text} {elementLanguage.displayName.toLowerCase()}");
            }

        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "Greeting", Messages.getInformationIcon());
            e.printStackTrace();
        }
    }

    private void setUp() throws Exception {
        File myTargetDir = new File("C:\\Users\\user\\Documents\\backup\\data\\raw\\");
        System.err.println(myTargetDir);
        if (!myTargetDir.isDirectory()) throw new Exception(myTargetDir + " is not a directory");

        File checkDir = new File("C:\\Users\\user\\Documents\\backup\\data\\parsed\\");
        System.err.println(checkDir);
        if (!myTargetDir.isDirectory()) throw new Exception(myTargetDir + " is not a directory");

        ArrayList<String> checkFiles = new ArrayList<>();
        for (File f : checkDir.listFiles()) {
            checkFiles.add(f.getName());
        }

        File[] myFiles = myTargetDir.listFiles();
        for (File file : myFiles) {
            if (!checkFiles.contains(file.getName() + ".json")) {// && file.getName().equals("100512211")) {
                System.err.println(file.getName());
                String text_ = "";
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.contains("//"))
                            text_ += line;
                    }
                }
                String text = text_.replaceAll("(//)[^&]*(" + System.getProperty("line.separator") + ")", "")
                        .replace("\n", "").replace(";", ";\n")
                        .replace("{", "{\n").replace("}", "}\n")
                        .replace("*/", "*/\n");
                ArrayList<InnerContext> output_elements = new ArrayList<>();
                Long start = System.currentTimeMillis();

                final String s = "*.java";
                FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName("*.java"); // RegExp plugin is not installed

                final PsiFile psf = PsiFileFactory.getInstance(project).createFileFromText(file.getName(), fileType, text, -1, true);

                //     parseFile(psf, output_elements, text, null);
                System.err.println(((System.currentTimeMillis() - start) / 1000));

                try (Writer writer = new FileWriter("C:\\Users\\user\\Documents\\backup\\data\\parsed\\" + file.getName() + ".json")) {
                    Gson gson = new GsonBuilder().create();
                    gson.toJson(output_elements, writer);
                }
            }
        }
    }


}