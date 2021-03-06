<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
    <xsl:output encoding="utf-8" omit-xml-declaration="yes" method="xml"/>
    <xsl:template match="/">
        <div class="" style="display:table">
            <div class="withLeftPadding backupImages" style="display:table-cell">
                <h2>Backup Images (30 most recent)</h2>
                <table cellspacing="0" cellpadding="0" border="0" class="pctTable">
                    <tr>
                        <td class="head">#</td>
                        <td class="head">Date Modified</td>
                        <td class="head">Saved By</td>
                        <td class="head wider">Comment</td>
                        <td class="head">Backup</td>
                        <td class="head">Config</td>
                        <td class="head">Release</td>
                    </tr>
                    <xsl:for-each select="/root/File">
                        <xsl:sort select="Sort" order="descending"/>
                        <xsl:if test="not(position() > 30)">
                            <tr>
                                <td class="right nowrap">
                                    <xsl:value-of select="position()"/>
                                </td>
                                <td class="nowrap">
                                    <xsl:value-of select="Date"/>
                                </td>
                                <td class="nowrap">
                                    <xsl:value-of select="SavedBy"/>
                                </td>
                                <td>
                                    <xsl:value-of select="Description"/>
                                </td>
                                <td class="nowrap">
                                    <xsl:choose>
                                        <xsl:when test="Current">
                                            <b>current</b>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <a href="#" onclick="PCT.loadConfig(this)" date="{Date}" cfg="{Name}">restore</a>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </td>
                                <td class="nowrap downloadTr">
                                    <xsl:choose>
                                        <xsl:when test="Timestamp">
                                            <a href="#" class="downloadLink" onclick="$('.downloadDiv', $(this).parents('.downloadTr')).show(); $(this).hide()">download</a>
                                            <div class="downloadDiv" style="display:none">
                                                Password:&#160;<input type="password" size="10" id="pwd{Timestamp}"/>
                                                <div style="text-align:right">
                                                    <button date="{Date}" cfg="{Timestamp}" pwdid="pwd{Timestamp}">
                                                        <xsl:attribute name="onclick">
                                                            if(PCT.getConfiguration(this)){
                                                                $('.downloadLink', $(this).parents('.downloadTr')).show();
                                                                $('.downloadDiv', $(this).parents('.downloadTr')).hide();
                                                            }
                                                        </xsl:attribute>
                                                        Download
                                                    </button>
                                                </div>
                                            </div>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            &#160;
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </td>
                                <td class="nowrap">
                                    <xsl:choose>
                                        <xsl:when test="Release">
                                            <b>current</b>
                                        </xsl:when>
                                        <xsl:when test="Timestamp">
                                            <a href="#" onclick="PCT.releaseConfig(this)" date="{Date}" cfg="{Timestamp}">release</a>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            &#160;
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </td>
                            </tr>
                        </xsl:if>
                    </xsl:for-each>
                </table>
            </div>
            <div class="withLeftPadding" style="display:table-cell">
                <h2>Log Files (30 most recent)</h2>
                <table cellspacing="0" cellpadding="0" border="0" class="pctTable">
                    <tr>
                        <td class="head">#</td>
                        <td class="head">Date Modified</td>
                        <td class="head">&#160;</td>
                    </tr>
                    <xsl:for-each select="/root/Log">
                        <xsl:sort select="Sort" order="descending"/>
                        <xsl:if test="not(position() > 30)">
                            <tr>
                                <td class="right nowrap">
                                    <xsl:value-of select="position()"/>
                                </td>
                                <td class="nowrap">
                                    <xsl:value-of select="Date"/>
                                </td>
                                <td class="nowrap">
                                    <!--<xsl:choose>
                                        <xsl:when test="Current">
                                            <b>current</b>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <a href="#" onclick="PCT.loadConfig(this)" date="{Date}" cfg="{Name}">load</a>
                                        </xsl:otherwise>
                                    </xsl:choose>-->
                                    <a href="{Name}" target="_blank">view</a>
                                </td>
                            </tr>
                        </xsl:if>
                    </xsl:for-each>
                </table>
            </div>
        </div>
    </xsl:template>
</xsl:stylesheet>
