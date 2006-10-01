<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>

    <xsl:template match="buildCommand[name='org.eclipse.jdt.core.javabuilder']">
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