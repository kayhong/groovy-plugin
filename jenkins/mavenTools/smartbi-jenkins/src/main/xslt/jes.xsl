<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <html>
      <head>
        <style>
          ul { list-style-type: none; }
          label{
          background-color: #AAAFAB;
          border-radius: 5px;
          padding: 3px;
          padding-left: 25px;
          color: white; 
          }
          li { 
          margin: 10px;
          padding: 5px;
          border: 1px solid #ABC;
          border-radius: 5px;
          }
          input[type=checkbox] { display: none; }
          input[type=checkbox] ~ ul { 
          max-height: 0;
          max-width: 0;
          opacity: 0;
          overflow: hidden;
          white-space:nowrap;
          -webkit-transition:all 1s ease;  
          -moz-transition:all 1s ease;  
          -o-transition:all 1s ease;  
          transition:all 1s ease;  

          }
          input[type=checkbox]:checked ~ ul { 
          max-height: none;
          max-width: none;
          opacity: 1;
          }
          input[type=checkbox] + label:before{
          transform-origin:25% 50%;
          border: 8px solid transparent;
          border-width: 8px 12px; 
          border-left-color: white;
          margin-left: -20px;
          width: 0;
          height: 0;
          display: inline-block;
          text-align: center;
          content: '';
          color: #AAAFAB;
          -webkit-transition:all .5s ease;  
          -moz-transition:all .5s ease;  
          -o-transition:all .5s ease;  
          transition:all .5s ease; 
          position: absolute;
          margin-top: 1px;
          }
          input[type=checkbox]:checked + label:before {
          transform: rotate(90deg);
          /*margin-top: 6px;
          margin-left: -25px;*/
          }
        </style>
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
