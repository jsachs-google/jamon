package org.jamon.eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.jamon.api.TemplateLocation;
import org.jamon.api.TemplateSource;

public class ResourceTemplateSource implements TemplateSource {

	ResourceTemplateSource(IFolder templateFolder) {
		m_templateFolder = templateFolder;
	}

	private final IFolder m_templateFolder;

	private IFile resourceFor(String p_templatePath) {
		return (IFile) m_templateFolder.findMember(new Path(p_templatePath)
            .addFileExtension("jamon"));
	}

	@Override
  public long lastModified(String p_templatePath) {
		return resourceFor(p_templatePath).getLocalTimeStamp();
	}

	@Override
  public boolean available(String p_templatePath) {
		return resourceFor(p_templatePath) != null;
	}

	@Override
  public InputStream getStreamFor(String p_templatePath) throws IOException {
		try {
			return resourceFor(p_templatePath).getContents(true);
		}
		catch (CoreException e) {
			throw new IOException(e.getMessage());
		}
	}

	@Override
  public String getExternalIdentifier(String p_templatePath) {
		return p_templatePath;
	}

    @Override
    public TemplateLocation getTemplateLocation(String p_templatePath) {
        return new ResourceTemplateLocation(resourceFor(p_templatePath));
    }

    @Override
    public void loadProperties(String p_path, Properties p_properties)
      throws IOException {
        IFile resource = (IFile) m_templateFolder.findMember(
            p_path + "/" + "jamon.properties");
        if (resource != null && resource.isAccessible()) {
          InputStream contents = null;
          try {
            contents = resource.getContents();
            p_properties.load(contents);
          }
          catch (CoreException e) {
              throw new IOException(e.getMessage());
          }
          finally {
              if (contents != null) {
                  contents.close();
              }
          }
        }
    }


}
