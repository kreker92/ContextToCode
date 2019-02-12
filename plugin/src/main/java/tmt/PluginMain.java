package tmt;

import tmt.analyze.ContextHelperPanel;
import tmt.attributes.TextAttributes;
import com.intellij.codeInsight.daemon.impl.*;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.InspectionManagerEx;
import com.intellij.codeInspection.ex.LocalInspectionToolWrapper;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationSession;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PluginMain implements ProjectComponent {
    private final static Logger LOG = Logger.getInstance(PluginMain.class);

    private final Project project;

    /**
     * @param project The current project, i.e. the project which was just opened.
     */
    public PluginMain(Project project) {
        this.project = project;
        /*PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
            public void beforeChildrenChange(@NotNull PsiTreeChangeEvent event) {
                runInspectionOnFile(new GenerateInspection(), event.getParent(), event.getFile());
            }
        }, project);*/
    }

    private void runInspectionOnFile(@NotNull LocalInspectionTool inspectionTool, PsiElement el, PsiFile file) {
        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        ContextHelperPanel.getPanel(project);

        if(document != null) {
            AnnotationHolderImpl annotationHolder = new AnnotationHolderImpl(new AnnotationSession(file));

            InspectionManagerEx inspectionManager = (InspectionManagerEx) InspectionManager.getInstance(file.getProject());
            GlobalInspectionContext context = inspectionManager.createNewGlobalContext(false);

            final List<ProblemDescriptor> problemDescriptors = InspectionEngine.runInspectionOnFile(file, new LocalInspectionToolWrapper(inspectionTool), context);

            ArrayList<HighlightInfo> hls = new ArrayList<>();
            for (ProblemDescriptor p_ : problemDescriptors) {
                SuggestGenerate p = (SuggestGenerate) p_;

                int start_line = p.getLineNumber();
                int end_line = p.getLineNumber();

                TextRange range = new TextRange(document.getLineStartOffset(start_line), document.getLineEndOffset(end_line));

//                Annotation annotation = annotationHolder.createWarningAnnotation(range, "Suggest for next line:" + p.getAnnotationMessage());
//                annotation.setHighlightType(ProblemHighlightType.INFORMATION);
//                annotation.setTextAttributes(TextAttributes.CRITICAL);
             //   annotation.registerFix(new QuickFix(p.getFixMessage(), project, p.getLineNumber()));

//                HighlightInfo hl = HighlightInfo.fromAnnotation(annotation);
//                hls.add(hl);
            }

            UpdateHighlightersUtil.setHighlightersToEditor(project, document,
                    0, document.getTextLength(), hls, null, 0);
        }
    }

    public void initComponent() {
        //called before projectOpened()
        System.err.println("Project name: " + project);
    }

    public void projectOpened() {
        LOG.info(String.format("Project '%s' has been opened, base dir '%s'", project.getName(), project.getBasePath()));
    }

    public void projectClosed() {
        LOG.info(String.format("Project '%s' has been closed.", project.getName()));
    }

    public void disposeComponent() {
        //called after projectClosed()
    }

    @NotNull
    public String getComponentName() {
        return "myProjectComponent";
    }
}