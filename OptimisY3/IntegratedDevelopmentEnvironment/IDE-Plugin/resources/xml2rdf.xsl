<?xml version="1.0" encoding="UTF-8"?>
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
		program. If not, see <http://www.gnu.org/licenses/>.
	-->
	<!--
		xml2rdf3.xsl XSLT stylesheet to transform XML into RDF/XML Version 3.0
		(2009-05-28) Changes to V2.5 rdf:value for all text, no attribute
		triples, order predicates, comments as triples Web page
		http://www.gac-grid.org/project-products/Software/XML2RDF.html Usage
		xsltproc xml2rdf3.xsl file.xml Author Frank Breitling (fbreitling at
		aip.de) Copyright 2009 AstroGrid-D (http://www.gac-grid.org/) Licensed
		under the Apache License, Version 2.0 (the "License"); you may not use
		this file except in compliance with the License. You may obtain a copy
		of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless
		required by applicable law or agreed to in writing, software
		distributed under the License is distributed on an "AS IS" BASIS,
		WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
		implied. See the License for the specific language governing
		permissions and limitations under the License.
	-->

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:xs="http://www.w3.org/2001/XMLSchema#">
	<xsl:strip-space elements="*" />
	<xsl:output method="xml" indent="yes" />
	<xsl:variable name="rdf"
		select="string('http://www.w3.org/1999/02/22-rdf-syntax-ns#')" />
	<xsl:variable name="xs"
		select="string('http://www.w3.org/2001/XMLSchema#')" />
	<xsl:variable name="rel" select="string('http://relative#')" />
	<!--
		Begin RDF document
	-->
	<xsl:template match="/">
		<xsl:variable name="base" select="string('http://relative#')" />
		<xsl:element name="rdf:RDF" >
			<!--  xsl:attribute name="xmlns:base">
				<xsl:value-of select="$base" />
			</xsl:attribute-->
			<xsl:apply-templates select="/*|/@*" />
		</xsl:element>
	</xsl:template>
	<!-- Turn XML elements into RDF triples. -->
	<xsl:template match="/*">
		<xsl:param name="subjectname" />
		<!--
			Build URI for subjects resources from acestors elements
		-->
		
		<xsl:variable name="newsubjectname">
			<xsl:if test="$subjectname=''">	
			    <xsl:choose>
			        <xsl:when test="namespace-uri()=''">
						<xsl:value-of select="$rel" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(namespace-uri(),'#')" />
				</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:value-of select="$subjectname" />
			<xsl:value-of select="local-name()" />
			<!--
				Add an ID to sibling element of identical name
			-->
			<xsl:variable name="number">
				<xsl:number />
			</xsl:variable>
			<xsl:if test="$number &gt; 0">
				<xsl:text>_</xsl:text>
				<xsl:number />
			</xsl:if>
		</xsl:variable>
		<rdf:Description>
			<xsl:attribute name="rdf:about">
				<xsl:value-of select="$newsubjectname" />
			</xsl:attribute>
			<xsl:apply-templates select="@*|node()">
				<xsl:with-param name="subjectname" select="concat($newsubjectname,'/')" />
			</xsl:apply-templates>
			<rdf:type>
				<xsl:attribute name="rdf:resource">
					<xsl:choose>
						<xsl:when test="namespace-uri()=''">
							<xsl:value-of select="concat($rel, local-name())" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat(concat(namespace-uri(),'#'),local-name())" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</rdf:type>
		</rdf:Description>
	</xsl:template>
	<xsl:template match="*">
		<xsl:param name="subjectname" />
		<!--
			Build URI for subjects resources from acestors elements
		-->
		<xsl:variable name="newsubjectname">
			<xsl:if test="$subjectname=''">
				<xsl:choose>
					<xsl:when test="namespace-uri()=''">
						<xsl:value-of select="$rel" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat(namespace-uri(),'#')" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:value-of select="$subjectname" />
			<xsl:value-of select="local-name()" />
			<!--
				Add an ID to sibling element of identical name
			-->
			<xsl:variable name="number">
				<xsl:number />
			</xsl:variable>
			<xsl:if test="$number &gt; 0">
				<xsl:text>_</xsl:text>
				<xsl:number />
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="ns">
			<!--
				If attribute doesn't have a namespace use element namespace
			-->
			<xsl:choose>
				<xsl:when test="namespace-uri()=''">
					<xsl:value-of select="$rel" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(namespace-uri(),'#')" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{name()}" namespace="{$ns}">
			<xsl:choose>
				<xsl:when
					test="count(./*)&gt;0 or count(./@*[name() != 'xsi:type'])&gt;0">
					<rdf:Description>
						<xsl:attribute name="rdf:about">
							<xsl:value-of select="$newsubjectname" />
						</xsl:attribute>
						<xsl:apply-templates select="@*|node()">
							<xsl:with-param name="subjectname" select="concat($newsubjectname,'/')" />
						</xsl:apply-templates>
						<rdf:type>
							<xsl:attribute name="rdf:resource">
								<xsl:choose>
									<xsl:when test="count(@*[name() = 'xsi:type']) &gt; 0">
										<xsl:variable name="xsi_type" select="@xsi:type" />
										
										<xsl:choose>
											<xsl:when test="contains($xsi_type,':')">
												<xsl:value-of
								select="concat($rel ,substring-after($xsi_type,':'))" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$xsi_type" />
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when test="namespace-uri()=''">
												<xsl:value-of select="concat($rel ,local-name())" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of
								select="concat(concat(namespace-uri(),'#'),local-name())" />
											</xsl:otherwise>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</rdf:type>
					</rdf:Description>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="rdf:datatype">
						<xsl:choose>
							<xsl:when test="number(.)">
								<xsl:value-of select="concat($xs,'float')" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="concat($xs,'string')" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:value-of select="." />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	<xsl:template match="@*[name()='xsi:type']" />
	<!--
		Create attribute triples.
	-->
	<xsl:template match="@*[name()!='xsi:type']" name="attributes">
		<xsl:variable name="ns2">
			<!--
				If attribute doesn't have a namespace use element namespace
			-->
			<xsl:choose>
				<xsl:when test="namespace-uri()=''">
					<xsl:value-of select="$rel" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(namespace-uri(),'#')" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{name()}" namespace="{$ns2}">
			<xsl:attribute name="rdf:datatype">
				<xsl:choose>
					<xsl:when test="number(.)">
						<xsl:value-of select="concat($xs,'float')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($xs,'string')" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:value-of select="." />
		</xsl:element>
	</xsl:template><!-- Enclose text in an rdf:value element -->
	<xsl:template match="text()">
		<xsl:variable name="ns3">
			<!--
				If attribute doesn't have a namespace use element namespace
			-->
			<xsl:choose>
				<xsl:when test="namespace-uri()=''">
					<xsl:value-of select="$rel" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(namespace-uri(),'#')" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{name()}" namespace="{$ns3}">
			<xsl:attribute name="rdf:datatype">
				<xsl:choose>
					<xsl:when test="number(.)">
						<xsl:value-of select="concat($xs,'float')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($xs,'string')" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:value-of select="." />
		</xsl:element>
	</xsl:template><!-- Add triple to preserve comments -->
	<xsl:template match="comment()">
		<xsl:element name="xs:comment">
			<xsl:value-of select="." />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
