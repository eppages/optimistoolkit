<?xml version="1.0" encoding="UTF-8" ?>
	<!--

		NUBA Project http://nuba.morfeo-project.org/ Copyright (C) 2010,2011
		Barcelona Supercomputing Center This program is free software: you can
		redistribute it and/or modify it under the terms of the Lesser GNU
		General Public License as published by the Free Software Foundation,
		either version 3 of the License, or (at your option) any later
		version. This program is distributed in the hope that it will be
		useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Lesser
		GNU General Public License for more details. You should have received
		a copy of the Lesser GNU General Public License along with this
		program. If not, see
	-->


<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output method="xml" indent="yes" />
	<xsl:template match="/rdf:RDF">
		<xsl:apply-templates select="./*" />
	</xsl:template>
	<xsl:template match="/rdf:RDF/*">
		<xsl:element name="{local-name()}" namespace="{namespace-uri()}">
			<xsl:apply-templates select="./*" />
		</xsl:element>
	</xsl:template>
	<xsl:template match="/rdf:RDF/*//*[@rdf:about]">
		<xsl:element name="{local-name(..)}" namespace="{namespace-uri(..)}">
			<xsl:attribute name="type"
				namespace="http://www.w3.org/2001/XMLSchema-instance">
				<xsl:value-of select="local-name()" />
			</xsl:attribute>
			<xsl:apply-templates select="./*" />
		</xsl:element>
	</xsl:template>
	<!--
	<xsl:template match="/rdf:RDF/*//*[@rdf:datatype]">
		<xsl:element name="{local-name()}" namespace="{namespace-uri()}">
			<xsl:value-of select="." />
			<xsl:apply-templates select="./*" />
		</xsl:element>
	</xsl:template>
	-->
	<xsl:template match="/rdf:RDF/*//*">
		<xsl:element name="{local-name()}" namespace="{namespace-uri()}">
			<xsl:value-of select="." />
			<xsl:apply-templates select="./*" />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
