package org.jamon.eclipse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class GeneratedResource
{
    public GeneratedResource(IFile p_generatedJavaFile)
        throws CoreException, NotAGeneratedResourceException
    {
        TemplateResources resources = TemplateResources.fromGeneratedSource(p_generatedJavaFile);
        if (resources == null) {
            throw new NotAGeneratedResourceException();
        }
        m_isImpl =
            p_generatedJavaFile.getFullPath().removeFileExtension().lastSegment().endsWith("Impl");
        m_templateFile = resources.getTemplate();

        try
        {
            m_locations = JamonUtils.readLineNumberMappings(p_generatedJavaFile);
        }
        catch (IOException e)
        {
            m_locations = Collections.emptyList();
            JamonProjectPlugin.getDefault().logError(e);
        }
    }


    public int getTemplateLineNumber(int p_javaLineNumber)
    {
        if (m_locations.isEmpty())
        {
            return 1;
        }
        else
        {
            return m_locations.get(
                Math.min(p_javaLineNumber, m_locations.size() - 1));
        }
    }

    public boolean isImpl()
    {
        return m_isImpl;
    }

    public IFile getTemplateFile()
    {
        return m_templateFile;
    }

    final IFile m_templateFile;
    final boolean m_isImpl;
    private List<Integer> m_locations;
}