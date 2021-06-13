import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

import java.io.Closeable;

public class ReleaseResourceProcessor extends AbstractProcessor<CtVariable> {

    @Override
    public boolean isToBeProcessed(CtVariable candidate) {
        try {
            Class clazz = Class.forName(candidate.getType().toString());
            return Closeable.class.isAssignableFrom(clazz) || AutoCloseable.class.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public void process(CtVariable ctVariable) {
        CtElement ctElement = ctVariable.getParent();
        if (ctElement instanceof CtStatementList) {
            CtStatementList ctStatements = (CtStatementList) ctElement;
            CtStatement closeStatement = this.getFactory().createCodeSnippetStatement(String.format("%s.close()", ctVariable.getSimpleName()));
            ctStatements.addStatement(createTryBlock(this.getFactory().createCtBlock(closeStatement)));
            System.out.println(ctElement);
        }
    }

    private CtTry createTryBlock(CtBlock tryBlock, CtCatch ctCatch, CtBlock finallyBlock) {
        CtTry ctTry = this.getFactory().createTry();
        ctTry.setBody(tryBlock);
        ctTry.addCatcher(ctCatch);
        ctTry.setFinalizer(finallyBlock);
        return ctTry;
    }

    private CtTry createTryBlock(CtBlock tryBlock) {
        return createTryBlock(tryBlock, createCtCatch(), null);
    }

    private CtCatch createCtCatch(Class<? extends Exception> exceptionClass, String exceptionVariableName, CtBlock catchBlock) {
        return this.getFactory().createCtCatch(exceptionVariableName, exceptionClass, catchBlock);
    }

    private CtCatch createCtCatch() {
        CtBlock catchBlock = this.getFactory().createCtBlock(this.getFactory().createCodeSnippetStatement("e.printStackTrace()"));
        return createCtCatch(Exception.class, "e", catchBlock);
    }
}
