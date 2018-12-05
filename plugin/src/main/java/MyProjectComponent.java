import com.intellij.codeInsight.daemon.impl.*;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.GlobalInspectionContextImpl;
import com.intellij.codeInspection.ex.InspectionManagerEx;
import com.intellij.codeInspection.ex.LocalInspectionToolWrapper;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.Element;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class MyProjectComponent implements ProjectComponent {
    private final static Logger LOG = Logger.getInstance(MyProjectComponent.class);

    private final Project project;
    private Document document;
    private PsiFile file;
    @NotNull
    private final MyApplicationComponent applicationComponent;

    /**
     * @param project The current project, i.e. the project which was just opened.
     */
    public MyProjectComponent(Project project, @NotNull MyApplicationComponent applicationComponent) {
        this.project = project;
        this.applicationComponent = applicationComponent;

        PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
            public void beforeChildrenChange(@NotNull PsiTreeChangeEvent event) {
                file = event.getFile();
                document = PsiDocumentManager
                        .getInstance(project)
                        .getDocument(file);
                runInspectionOnFile (file, new InstanceFieldCountInspection(), event.getParent());
                System.err.println(event);
            }

          /*  public void childRemoved(@NotNull PsiTreeChangeEvent event) {
                update(event);
            }

            public void childAdded(@NotNull PsiTreeChangeEvent event) {
                update(event);
            }

            https://intellij-support.jetbrains.com/hc/en-us/community/posts/206111999-How-to-keep-inspections-created-by-LocalInspectionTool-in-place

            public void childReplaced(@NotNull PsiTreeChangeEvent event) {
                update(event);
            }*/
        }, project);
    }

    private void runInspectionOnFile(@NotNull PsiFile file,
                                     @NotNull LocalInspectionTool inspectionTool, PsiElement el) {
        /*InspectionManagerEx inspectionManager = (InspectionManagerEx) InspectionManager.getInstance(file.getProject());
        GlobalInspectionContext context = inspectionManager.createNewGlobalContext(false);
        final List<ProblemDescriptor> problemDescriptors = InspectionEngine.runInspectionOnFile(file, new LocalInspectionToolWrapper(inspectionTool), context);
        return problemDescriptors;*/

        LocalInspectionsPass localInspectionsPass = new LocalInspectionsPass(file,
                document,
                0,
                document.getTextLength(),
                LocalInspectionsPass.EMPTY_PRIORITY_RANGE,
                true,
                HighlightInfoProcessor.getEmpty());

        LocalInspectionToolWrapper lw = new LocalInspectionToolWrapper(inspectionTool);
        ArrayList lwl = new ArrayList<LocalInspectionToolWrapper>();
        lwl.add(lw);
        InspectionManager inspectionManagerEx = InspectionManager.getInstance(project);

//        ProgressManager.getInstance().runProcess(new Runnable() {
//                                                     @Override
//                                                     public void run() {

                                                         /*System.err.println(1);
                                                         localInspectionsPass.doInspectInBatch((GlobalInspectionContextImpl) inspectionManagerEx.createNewGlobalContext(false),
                                                                 inspectionManagerEx, lwl);*/

        InspectionManagerEx inspectionManager = (InspectionManagerEx) InspectionManager.getInstance(file.getProject());
        GlobalInspectionContext context = inspectionManager.createNewGlobalContext(false);
        final List<ProblemDescriptor> problemDescriptors = InspectionEngine.runInspectionOnFile(file, new LocalInspectionToolWrapper(inspectionTool), context);

        System.err.println(problemDescriptors);
        System.err.println(2);

        ArrayList<HighlightInfo> hl = new ArrayList<>();
        for ( ProblemDescriptor p : problemDescriptors) {
            hl.add(createHighlight(p.getPsiElement(), "huy!!!"));
        }

        UpdateHighlightersUtil.setHighlightersToEditor(project,
                document,
                0,
                document.getTextLength(),
                hl,
                null,
                0);
        System.err.println(localInspectionsPass.getInfos());
//                                                     }
//                                                 },         new EmptyProgressIndicator() );

    }

    private static HighlightInfo createHighlight(PsiElement target, @Nullable String message) {
        HighlightInfo.Builder builder = HighlightInfo.newHighlightInfo(HighlightInfoType.ELEMENT_UNDER_CARET_READ)
                .range(target)
                .severity(HighlightSeverity.ERROR)
                .textAttributes(TextAttributes.CRITICAL);

        if (message != null && !message.isEmpty() && !"...".equals(message)) {
            builder.descriptionAndTooltip("SonarLint: " + message);
        }
        return builder.create();
    }

    public void initComponent() {
        //called before projectOpened()
        System.err.println(project);


    }

    public void projectOpened() {
        LOG.info(String.format("Project '%s' has been opened, base dir '%s'", project.getName(), project.getBaseDir().getCanonicalPath()));
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

    private void parseFile(PsiFile psf, ArrayList<InnerContext> output_elements, String text, Integer end) {
        SelectionContextExtractor contextExtractor = new SelectionContextExtractor(psf);
        String word = "\n";
        ArrayList<Integer> ends = new ArrayList<>();

        for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
            if (end == null || i <= end)
                ends.add(i);
        }

        int previous = 0;
        int line = 0;

        for (int e : ends) {
            SelectionContext context = contextExtractor.extractContext(previous, e+1, line, text.substring(previous, e+1));
          //  SelectionContextQueryBuilder queryBuilder = new SelectionContextQueryBuilder(context);
           // queryBuilder.buildQuery();
            previous = e+1;
            line ++;
            output_elements.add(context.ic);
        }
    }
}

class SelectionContext {

    private final List<PsiElement> psiElements;
    public InnerContext ic = new InnerContext();

    SelectionContext(List<PsiElement> psiElements, int selectionStartOffset, int selectionEndOffset, int line, String text) {
        this.psiElements = psiElements;
        ic.line_num = line;
        ic.start = selectionStartOffset;
        ic.end = selectionEndOffset;
        ic.line_text = text;
    }

    List<PsiElement> getPsiElements() {
        return psiElements;
    }
}

class SelectionContextExtractor {

    private final PsiFile psiFile;

    public SelectionContextExtractor(PsiFile psiFile) {
//            this.selectionStartOffset = editor.getSelectionModel().getSelectionStart();
//            this.selectionEndOffset = editor.getSelectionModel().getSelectionEnd();
        this.psiFile = psiFile;
    }

    public SelectionContext extractContext(int selectionStartOffset, int selectionEndOffset, int line, String text) {
        List<PsiElement> psiElements = new ArrayList<>();
        traversePsiElement(psiFile, psiElements, selectionStartOffset, selectionEndOffset);
        return new SelectionContext(psiElements, selectionStartOffset, selectionEndOffset, line, text);
    }

    private void traversePsiElement(PsiElement element, List<PsiElement> selectedElements, int selectionStartOffset, int selectionEndOffset) {
        int elementStart = element.getTextOffset();
        int elementEnd = elementStart + element.getTextLength();
        if (selectionStartOffset <= elementStart && elementEnd <= selectionEndOffset) {
            selectedElements.add(element);
        }
        for (PsiElement childElement : element.getChildren()) {
            traversePsiElement(childElement, selectedElements, selectionStartOffset, selectionEndOffset);
        }
    }
}
