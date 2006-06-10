package org.jamon.eclipse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class TemplateResources
{
    public static TemplateResources fromGeneratedSource(IFile p_javaFile) throws CoreException {
      IProject project = p_javaFile.getProject();

      JamonNature nature = (JamonNature) project.getNature(JamonNature.natureId());
      if (nature == null) {
        return null;
      }
      return fromGeneratedSource(
        p_javaFile, nature.getTemplateOutputFolder(), nature.getTemplateSourceFolder());

    }

    public static TemplateResources fromGeneratedSource(
        IFile p_javaFile, IFolder p_outFolder, IFolder p_templateFolder) {
        if ("java".equals(p_javaFile.getFileExtension())
            && p_outFolder.getFullPath().isPrefixOf(p_javaFile.getFullPath())) {
            IPath sourcePath = computeRelativePath(p_javaFile, p_outFolder).removeFileExtension();
            //FIXME - check that it really is an impl
            String lastSegment = sourcePath.lastSegment();
            if (lastSegment.endsWith("Impl")) {
                sourcePath = sourcePath
                    .removeLastSegments(1)
                    .append(lastSegment.substring(0, lastSegment.length() - 4));
            }
            return new TemplateResources(sourcePath, p_outFolder, p_templateFolder);
        }
        else {
          return null;
        }
    }

    public TemplateResources(
      IPath p_path, IFolder p_outFolder, IFolder p_templateFolder) {
        m_outFolder = p_outFolder;
        m_templateFolder = p_templateFolder;
        m_path = p_path;
        m_template = p_templateFolder.getFile(m_path.addFileExtension("jamon"));
        m_proxy = p_outFolder.getFile(m_path.addFileExtension("java"));
        m_impl = p_outFolder.getFile(
          m_path.removeLastSegments(1)
              .append(m_path.lastSegment() + "Impl")
              .addFileExtension("java"));
    }

    public TemplateResources(
        IFile p_templateFile, IFolder p_outFolder, IFolder p_templateFolder)
    {
        this(
          computeRelativePath(p_templateFile, p_templateFolder),
          p_outFolder,
          p_templateFolder);
    }

    private static IPath computeRelativePath(IFile p_file, IFolder p_folder) {
      return p_file
          .getFullPath()
          .removeFirstSegments(p_folder.getFullPath().segmentCount())
          .removeFileExtension();
    }

    public void clearGeneratedResources() throws CoreException
    {
        EclipseUtils.delete(m_impl);
        EclipseUtils.delete(m_proxy);
    }

    public IFile getImpl()
    {
        return m_impl;
    }

    public IFile getProxy()
    {
        return m_proxy;
    }

    public IFile getTemplate()
    {
        return m_template;
    }

    public IPath getPath()
    {
        return m_path;
    }

    final IPath m_path;
    final IFile m_impl;
    final IFile m_template;
    final IFile m_proxy;
    final IFolder m_templateFolder;
    final IFolder m_outFolder;
}