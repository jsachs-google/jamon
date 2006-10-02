<?xml version="1.0" encoding="UTF-8"?>

<!--
  The contents of this file are subject to the Mozilla Public
  License Version 1.1 (the "License"); you may not use this file
  except in compliance with the License. You may obtain a copy of
  the License at http://www.mozilla.org/MPL/

  Software distributed under the License is distributed on an "AS
  IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  implied. See the License for the specific language governing
  rights and limitations under the License.
 
  The Original Code is Jamon code, released October, 2006.
 
  The Initial Developer of the Original Code is Jay Sachs.  Portions
  created by Jay Sachs are Copyright (C) 2006 Jay Sachs.  All Rights
  Reserved.
 
  Contributor(s):
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>

    <xsl:template match="buildCommand[name='org.eclipse.jdt.core.javabuilder']" xml:space="preserve">
        <buildCommand>
            <name>org.jamon.project.templateBuilder</name>
            <arguments/>
        </buildCommand>
        <buildCommand>
            <name>org.eclipse.jdt.core.javabuilder</name>
            <arguments/>
        </buildCommand>
        <buildCommand>
            <name>org.jamon.project.markerUpdater</name>
            <arguments/>
        </buildCommand>
    </xsl:template>

    <xsl:template match="buildCommand[name='org.jamon.project.templateBuilder']"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>
