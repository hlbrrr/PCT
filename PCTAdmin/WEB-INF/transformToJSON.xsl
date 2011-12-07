<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform' xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output encoding="utf-8" omit-xml-declaration="yes" method="text" />
<xsl:template match="*" name="root">{<xsl:call-template name="transform" />}</xsl:template>
<xsl:template name="transform">"<xsl:value-of select="name()" />" :<xsl:if test="not(./*) and not(./@*)">"<xsl:call-template name="val"><xsl:with-param name="str" select="." /></xsl:call-template>"</xsl:if><xsl:if test="not(not(./*) and not(./@*))">{<xsl:for-each select="./@*"><xsl:call-template name="transform" /><xsl:if test="position()!=last() or boolean(../*)">,</xsl:if></xsl:for-each><xsl:for-each select="./*"><xsl:variable name="name" select="name()" /><xsl:if test="count(../*[name()=$name])&gt;1 and position()=1">"<xsl:value-of select="name()" />" :[<xsl:for-each select="../*[name()=$name]"><xsl:if test="not(./*) and not(./@*)">"<xsl:value-of select="." />"<xsl:if test="position()!=last()">,</xsl:if></xsl:if><xsl:if test="not(not(./*) and not(./@*))">{<xsl:for-each select="./@*"><xsl:call-template name="transform" /><xsl:if test="position()!=last() or boolean(../*)">,</xsl:if></xsl:for-each><xsl:for-each select="./*"><xsl:call-template name="transform" /><xsl:if test="position()!=last()">,</xsl:if></xsl:for-each>}<xsl:if test="position()!=last()">,</xsl:if></xsl:if></xsl:for-each>]<xsl:if test="count(../*[name()=$name])!=last() and position()!=last()">,</xsl:if></xsl:if><xsl:if test="count(../*[name()=$name])=1"><xsl:call-template name="transform" /><xsl:if test="position()!=last()">,</xsl:if></xsl:if></xsl:for-each>}</xsl:if></xsl:template>
<xsl:template name="val"><xsl:param name="str" /><xsl:if test="contains($str,'&quot;')"><xsl:value-of select="substring-before($str,'&quot;')" /><xsl:text>\"</xsl:text><xsl:call-template name="val"><xsl:with-param name="str"><xsl:value-of select="substring-after($str,'&quot;')" /></xsl:with-param></xsl:call-template></xsl:if><xsl:if test="not(contains($str,'&quot;'))"><xsl:value-of select="$str" /></xsl:if></xsl:template>

<!--  Formatted
<xsl:template match="*" name="root">
	{
	<xsl:call-template name="transform" />
	}
</xsl:template>
	<xsl:template name="transform">
		"
		<xsl:value-of select="name()" />
		" :
		<xsl:if test="not(./*) and not(./@*)">
			"
			<xsl:call-template name="val">
				<xsl:with-param name="str" select="." />
			</xsl:call-template>
			"
		</xsl:if>
		<xsl:if test="not(not(./*) and not(./@*))">
			{
			<xsl:for-each select="./@*">
				<xsl:call-template name="transform" />
				<xsl:if test="position()!=last() or boolean(../*)">
					,
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="./*">
				<xsl:variable name="name" select="name()" />
				<xsl:if test="count(../*[name()=$name])&gt;1 and position()=1">
					"
					<xsl:value-of select="name()" />
					" :[
					<xsl:for-each select="../*[name()=$name]">
						<xsl:if test="not(./*) and not(./@*)">
							"
							<xsl:value-of select="." />
							"
							<xsl:if test="position()!=last()">
								,
							</xsl:if>
						</xsl:if>
						<xsl:if test="not(not(./*) and not(./@*))">
							{
							<xsl:for-each select="./@*">
								<xsl:call-template name="transform" />
								<xsl:if test="position()!=last() or boolean(../*)">
									,
								</xsl:if>
							</xsl:for-each>
							<xsl:for-each select="./*">
								<xsl:call-template name="transform" />
								<xsl:if test="position()!=last()">
									,
								</xsl:if>
							</xsl:for-each>
							}
							<xsl:if test="position()!=last()">
								,
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
					]
					<xsl:if test="count(../*[name()=$name])!=last() and position()!=last()">
						,
					</xsl:if>
				</xsl:if>
				<xsl:if test="count(../*[name()=$name])=1">
					<xsl:call-template name="transform" />
					<xsl:if test="position()!=last()">
						,
					</xsl:if>
				</xsl:if>
			</xsl:for-each>
			}
		</xsl:if>
	</xsl:template>
	<xsl:template name="val">
		<xsl:param name="str" />
		<xsl:if test="contains($str,'&quot;')">
			<xsl:value-of select="substring-before($str,'&quot;')" />
			<xsl:text>\"</xsl:text>
			<xsl:call-template name="val">
				<xsl:with-param name="str">
					<xsl:value-of select="substring-after($str,'&quot;')" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="not(contains($str,'&quot;'))">
			<xsl:value-of select="$str" />
		</xsl:if>
	</xsl:template>
	-->
</xsl:stylesheet>