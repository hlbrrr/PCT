<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
    <xsl:output encoding="utf-8" omit-xml-declaration="yes" method="xml"/>
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>


    <xsl:template match="/root/Regions/Region">
        <xsl:if test="Key = /root/Users/User[CN=/root/cn]/Regions/Region/Key">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>

    <xsl:template match="/root/Users/User">
        <xsl:if test="CN = /root/cn">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>

    <xsl:template match="/root/cn">
    </xsl:template>

    <xsl:template match="/root/Files">
    </xsl:template>

</xsl:stylesheet>
