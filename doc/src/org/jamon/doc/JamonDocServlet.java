/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.jamon.doc;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import org.jamon.JamonException;
import org.jamon.JamonRuntimeException;
import org.jamon.RecompilingTemplateManager;
import org.jamon.TemplateInspector;
import org.jamon.TemplateManager;

public class JamonDocServlet
    extends HttpServlet
{
    public void doGet(HttpServletRequest p_request,
                      HttpServletResponse p_response)
        throws IOException, ServletException
    {
        Writer writer = p_response.getWriter();
        expireResponse(p_response);
        p_response.setContentType("text/html");
        String templatePath = p_request.getServletPath();
        templatePath = templatePath.substring(0,templatePath.length()-5);
        try
        {
            new TemplateInspector(m_manager, "/org/jamon/doc" + templatePath)
                .render(writer, m_parameters, true);
        }
        catch (JamonException e)
        {
            dumpException(writer, e, "JamonExcepton!!!");
        }
        writer.close();
    }

    private static void dumpException(Writer p_writer,
                                      Exception p_exception,
                                      String p_message)
        throws IOException
    {
        p_writer.write(p_message);
        p_writer.write("\n");
        p_exception.printStackTrace(new PrintWriter(p_writer));
        p_writer.write("</pre>");
    }

    public void init(ServletConfig p_config)
        throws ServletException
    {
        m_parameters.put("srcTarball", "jamon-src.tgz");
        m_parameters.put("srcZip", "jamon-src.zip");
        m_parameters.put("binTarball", "jamon.tgz");
        m_parameters.put("binZip", "jamon.zip");
        m_parameters.put("version", "3.14");
        m_parameters.put("output", "whatever");

        m_manager = new RecompilingTemplateManager
            (new RecompilingTemplateManager.Data()
                 .setWorkDir(p_config
                              .getServletContext()
                              .getAttribute("javax.servlet.context.tempdir")
                              .toString())
                 .setClassLoader(getClass().getClassLoader())
                 .setSourceDir("templates"));
    }

    private void expireResponse(HttpServletResponse p_response)
        throws IOException,
               ServletException
    {
        long now = System.currentTimeMillis();
        p_response.setDateHeader("Expires", now);
        p_response.setDateHeader("Date", now);
        p_response.setHeader("Pragma", "no-cache");
    }

    private final Map m_parameters = new HashMap();
    private TemplateManager m_manager;
}
