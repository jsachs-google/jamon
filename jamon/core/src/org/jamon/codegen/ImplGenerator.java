/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Jamon code, released October, 2002.
 *
 * The Initial Developer of the Original Code is Jay Sachs.  Portions
 * created by Jay Sachs are Copyright (C) 2002 Jay Sachs.  All Rights
 * Reserved.
 *
 * Contributor(s): Ian Robertson
 */

package org.jamon.codegen;

import java.io.Writer;
import java.io.IOException;
import java.util.Iterator;

import org.jamon.util.StringUtils;

public class ImplGenerator extends AbstractGenerator
{
    public ImplGenerator(Writer p_writer,
                         TemplateResolver p_resolver,
                         TemplateDescriber p_describer,
                         ImplAnalyzer p_analyzer)
    {
        super(p_writer, p_resolver);
        m_analyzer = p_analyzer;
        m_describer = p_describer;
    }

    public void generateSource()
        throws IOException
    {
        generatePrologue();
        generateImports();
        generateDeclaration();
        generateConstructor();
        generateInitialize();
        generateRender();
        generateOptionalArgs();
        generateDefs();
        generateEpilogue();
    }

    private final TemplateDescriber m_describer;
    private final ImplAnalyzer m_analyzer;

    private final String getPath()
    {
        return m_analyzer.getPath();
    }

    private String getClassName()
    {
        return getResolver().getImplClassName(getPath());
    }

    private void generateDeclaration()
        throws IOException
    {
        print  ("public class ");
        println(              getClassName());
        print  ("  extends ");
        println(           BASE_TEMPLATE);
        print  ("  implements ");
        print  (getResolver().getFullyQualifiedIntfClassName(getPath()));
        println(".Intf");
        openBlock();
        println(m_analyzer.getClassContent());
    }

    private void generateInitialize()
        throws IOException
    {
        println("protected void initializeDefaultArguments()");
        println("  throws Exception");
        openBlock();
        for (Iterator i = m_analyzer.getOptionalArgNames(); i.hasNext(); /* */)
        {
            String name = (String) i.next();
            print(name);
            print(" = ");
            print(m_analyzer.getDefault(name));
            println(";");
        }
        closeBlock();
        println();
    }

    private void generateConstructor()
        throws IOException
    {
        print("public ");
        print(          getClassName());
        print(                        "(");
        print(TEMPLATE_MANAGER);
        println(" p_templateManager, String p_path)");
        openBlock();
        println("super(p_templateManager, p_path);");
        closeBlock();
        println();
    }

    private void generatePrologue()
        throws IOException
    {
        String pkgName = getResolver().getImplPackageName(getPath());
        if (pkgName.length() > 0)
        {
            print("package ");
            print(pkgName);
            println(";");
            println();
        }
    }


    private void generateDefFargInterface(FargInfo p_fargInfo)
        throws IOException
    {
        print  ("private static interface ");
        println(p_fargInfo.getFargInterfaceName());
        println("  extends org.jamon.AbstractTemplateProxy.Intf");
        openBlock();
        print  ("void render(");

        for (Iterator a = p_fargInfo.getArgumentNames(); a.hasNext(); /* */)
        {
            String argName = (String) a.next();
            print(p_fargInfo.getArgumentType(argName));
            print(" ");
            print(argName);
            if (a.hasNext())
            {
                print(", ");
            }
        }
        println(")");
        println("  throws java.io.IOException;");

        print("public " + RENDERER_CLASS + " makeRenderer(");
        for (Iterator a = p_fargInfo.getArgumentNames(); a.hasNext(); /* */)
        {
            String argName = (String) a.next();
            print(p_fargInfo.getArgumentType(argName));
            print(" ");
            print(argName);
            if (a.hasNext())
            {
                print(", ");
            }
        }
        println(");");
        closeBlock();
        println();
    }


    private void generateDefs()
        throws IOException
    {
        for (Iterator d = m_analyzer.getDefNames().iterator(); d.hasNext(); /* */)
        {
            String name = (String) d.next();
            println();

            for (Iterator f = m_analyzer.getFargNames(name);
                 f.hasNext();
                 /* */)
            {
                generateDefFargInterface(m_analyzer.getFargInfo((String)f.next()));
            }

            print("private void __jamon_def__");
            print(name);
            print("(");
            int argNum = 0;
            for (Iterator a = m_analyzer.getRequiredArgNames(name);
                 a.hasNext();
                 /* */)
            {
                if (argNum++ > 0)
                {
                    print(",");
                }
                String arg = (String) a.next();
                print("final ");
                print(m_analyzer.getArgType(name,arg));
                print(" ");
                print(arg);
            }
            for (Iterator a = m_analyzer.getOptionalArgNames(name);
                 a.hasNext();
                 /* */)
            {
                if (argNum++ > 0)
                {
                    print(",");
                }
                String arg = (String) a.next();
                print(m_analyzer.getArgType(name,arg));
                print(" ");
                print(arg);
            }
            println(")");
            print  ("    throws ");
            println(IOEXCEPTION_CLASS);
            openBlock();
            for (Iterator i = m_analyzer.getStatements(name).iterator();
                 i.hasNext();
                 /* */)
            {
                ((Statement)i.next()).generateSource(getWriter(),
                                                     getResolver(),
                                                     m_describer,
                                                     m_analyzer);
            }
            closeBlock();
            println();
        }
    }

    private static final String TEMPLATE_MANAGER =
        org.jamon.TemplateManager.class.getName();

    private static final String BASE_TEMPLATE =
        org.jamon.AbstractTemplateImpl.class.getName();

    private void generateRender()
        throws IOException
    {
        print("public void render(");
        for (Iterator i = m_analyzer.getRequiredArgNames(); i.hasNext(); /* */)
        {
            String name = (String) i.next();
            print("final ");
            print(m_analyzer.getArgType(name));
            print(" ");
            print(name);
            if (i.hasNext())
            {
                print(", ");
            }
        }
        println(")");

        print  ("  throws ");
        println(IOEXCEPTION_CLASS);
        openBlock();
        for (Iterator i = m_analyzer.getStatements().iterator(); i.hasNext(); /* */)
        {
            ((Statement)i.next()).generateSource(getWriter(),
                                                 getResolver(),
                                                 m_describer,
                                                 m_analyzer);
        }
        closeBlock();
    }

    private void generateOptionalArgs()
        throws IOException
    {
        for (Iterator i = m_analyzer.getOptionalArgNames(); i.hasNext(); /* */)
        {
            println();
            String name = (String) i.next();
            String type = m_analyzer.getArgType(name);
            println("public void set" + StringUtils.capitalize(name)
                    + "(" + type + " p_" + name + ")");
            openBlock();
            println(name + " = p_" + name + ";");
            closeBlock();
            println();
            println("private " + type + " " + name + ";");
        }
    }


    private void generateEpilogue()
        throws IOException
    {
        println();
        closeBlock();
    }

    private void generateImports()
        throws IOException
    {
        for (Iterator i = m_analyzer.getImports(); i.hasNext(); /* */ )
        {
            println("import " + i.next() + ";");
        }
        println();
    }

}