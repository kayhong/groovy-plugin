<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="../../xmlToHtml/src/main/stylesheet/jes.css"/>
      </head>

      <body>    
        <ul>
          <li>          
           <input id="job_parent" type="checkbox"><xsl:attribute name="checked"/></input>
           <label for="job_parent"><xsl:value-of select="job/name"/></label>
           <label><a target="_blank"><xsl:attribute name="href"><xsl:value-of select="job/url"/></xsl:attribute><xsl:value-of select="job/url"/></a></label>
           <label>#<xsl:value-of select="job/number"/></label>
           <xsl:choose>
            <xsl:when test="job[status='SUCCESS']">
              <label style="background-color:green">
                <xsl:value-of select="job/status"/>
              </label>
            </xsl:when>
            <xsl:when test="job[status='in Progress']">
              <label style="background-color:rgb(0,240,230)">
                <xsl:value-of select="job/status"/>
              </label>
            </xsl:when>
            <xsl:when test="job[status='FAILURE']">
              <label style="background-color:red">
                <xsl:value-of select="job/status"/>
              </label>
            </xsl:when>
            <xsl:otherwise>
              <label style="background-color:orange">
                <xsl:value-of select="job/status"/>
              </label>
            </xsl:otherwise>
          </xsl:choose>

          <ul>
            <xsl:if test="job/parameters">               
              <li>
                <input id="parameters" type="checkbox"><xsl:attribute name="checked"/></input>
                <label for="parameters" style="background-color:rgb(80,80,80)">parameters</label>                       
                <ul>
                  <xsl:for-each select="job/parameters/parameter">
                    <li>                  
                     <label style="background-color:rgb(80,80,80)"><xsl:value-of select="name"/></label> : 
                     <label style="background-color:rgb(80,80,80)"><xsl:value-of select="value"/></label>
                   </li>
                 </xsl:for-each>
               </ul>            
             </li>              
           </xsl:if>

           <xsl:if test="job/job">                                         
            <xsl:for-each select="job/job">
              <xsl:call-template name="subjob"/>
            </xsl:for-each>         
          </xsl:if>
        </ul>

      </li>
    </ul>        
  </body>
</html>
</xsl:template>



<xsl:template name="subjob">  
  <li>
    <input type="checkbox"><xsl:attribute name="checked"/><xsl:attribute name="id"><xsl:value-of select="generate-id(name)"/></xsl:attribute></input>
    <label><xsl:attribute name="for"><xsl:value-of select="generate-id(name)"/></xsl:attribute><xsl:value-of select="name"/></label>
    <label><a target="_blank"><xsl:attribute name="href"><xsl:value-of select="url"/></xsl:attribute><xsl:value-of select="url"/></a></label>
    <label>#<xsl:value-of select="number"/></label>
    <xsl:choose>
      <xsl:when test="status='SUCCESS'">
        <label style="background-color:green">
          <xsl:value-of select="status"/>
        </label>
      </xsl:when>
      <xsl:when test="status='in Progress'">
        <label style="background-color:rgb(0,240,230)">
          <xsl:value-of select="status"/>
        </label>
      </xsl:when>
      <xsl:when test="status='FAILURE'">
        <label style="background-color:red">
          <xsl:value-of select="status"/>
        </label>
      </xsl:when>
      <xsl:otherwise>
        <label style="background-color:orange">
          <xsl:value-of select="status"/>
        </label>
      </xsl:otherwise>
    </xsl:choose>

    <ul>
      <xsl:if test="parameters">         
        <li>
         <input type="checkbox"><xsl:attribute name="checked"/><xsl:attribute name="id"><xsl:value-of select="generate-id(parameters)"/></xsl:attribute></input>
         <label style="background-color:rgb(80,80,80)"><xsl:attribute name="for"><xsl:value-of select="generate-id(parameters)"/></xsl:attribute>parameters</label>                   
         <ul>
          <xsl:for-each select="parameters/parameter">
            <li>                  
              <label style="background-color:rgb(80,80,80)"><xsl:value-of select="name"/></label> : 
              <label style="background-color:rgb(80,80,80)"><xsl:value-of select="value"/></label>
            </li>
          </xsl:for-each>
        </ul>
      </li>
    </xsl:if>

    <xsl:if test="job">
      <xsl:for-each select="job">                        
        <xsl:call-template name="subjob"/>
      </xsl:for-each>  
    </xsl:if>
  </ul>
</li>

</xsl:template>
</xsl:stylesheet>
