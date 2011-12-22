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
                <xsl:if test="not(/root/wrap='true')">
                    <textarea id="Description" style="width:50%" class="hidden">
                        <xsl:value-of select="/root/Description"/>
                    </textarea>
                </xsl:if>
                <div id="DescriptionHtml">
                    <xsl:value-of disable-output-escaping="yes" select="/root/Description"/>
                </div>
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
</xsl:stylesheet>
