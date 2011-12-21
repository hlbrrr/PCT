<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
    <xsl:output encoding="utf-8" omit-xml-declaration="yes" method="xml"/>
    <xsl:template match="/">
		<xsl:choose>
			<xsl:when test="/root/wrap='true'">
				<html>
					<head>
						<title>PCT</title>
						<script type="text/javascript" src="/js/jquery-1.6.3.js">//</script>
						<script type="text/javascript" src="/js/jquery.crypt.js">//</script>
						<script type="text/javascript" src="/js/admin.js">//</script>
					</head>
					<body>
						<xsl:call-template name="body"/>
					</body>
				</html>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="body"/>
			</xsl:otherwise>
		</xsl:choose>
    </xsl:template>
	
	<xsl:template name="body">
		<style>
			.mainHome{
				font-family:sans-serif;
				padding-top:40px;
				width:80%;
				text-align:left;
			}
			.textArea{
				margin-left:20px;
				margin-bottom:30px;
			}
			/*.textArea table tr:nth-child(even){
			}
			.textArea table tr:nth-child(odd){
				background-color:#ccc;
			}*/
			.textArea table tr td{
				padding:5px;
				vertical-align:top;
				border-right:1px solid #ddd;
				border-bottom:1px solid #ddd;
			}
			.withBorder table{
				border-left:1px solid #ddd;
				border-top:1px solid #ddd;
			}
		</style>
		<div align="center">
			<div class="mainHome">
				<xsl:if test="/root/wrap='true' and /root/Description and not(/root/Description='') or not(/root/wrap='true')">
					<h2>Introduction</h2>
					<div class="textArea">
						<xsl:choose>
							<xsl:when test="/root/wrap='true' and /root/Description and not(/root/Description='')">
								<xsl:call-template name="description"/>
							</xsl:when>
							<xsl:when test="not(/root/wrap='true')">
								<textarea style="width:50%" rows="5">
									<xsl:call-template name="description"/>
								</textarea>
							</xsl:when>
						</xsl:choose>
					</div>
				</xsl:if>
				<h2>PCT Configuration</h2>
				<div class="textArea">To get latest PCT configuration click <a href="#" onclick="PCT.getConfiguration()">here</a>.</div>
				<xsl:if test="count(/root/Files/File)>0">
					<h2>Other downloads</h2>
					<div class="textArea withBorder">
						<table border="0" cellspacing="0" cellpadding="0">
							<xsl:for-each select="/root/Files/File">
								<tr>
									<td>
										<a target="_blank">
											<xsl:attribute name="href">
												<xsl:value-of select="/root/path"/>/<xsl:value-of select="Key"/>
											</xsl:attribute>
											<xsl:value-of select="Name"/>
										</a>
									</td>
									<td>
										<xsl:value-of select="Hint"/>&#160;
									</td>
								</tr>
							</xsl:for-each>
						</table>
					</div>
				</xsl:if>
			</div>
		</div>
	</xsl:template>
	<xsl:template name="description">
	</xsl:template>
</xsl:stylesheet>
