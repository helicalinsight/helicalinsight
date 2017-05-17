<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<sec:authentication property="authorities" var="authorities"/>
<c:set var="organizationAdmin" value=""/>

<div class="col-md-10 col-md-offset-2 col-sm-9 col-sm-offset-3 col-xs-12 body-block ">
<div class="col-md-12 col-sm-12 col-xs-12 report-iframe">
    <div class="panel panel-default">
        <div class="panel-body nopad">
            <jsp:include page="/WEB-INF/jsp/userMenu.jsp"/>
            <iframe class="col-md-12 col-sm-12 col-xs-12 nopad" id="dashboard-canvas" name="parent_iframe"></iframe>
        </div>
    </div>
</div>

<div class="col-md-12 col-sm-12 col-xs-12 home-page">
<div class="col-md-12 col-sm-12 col-xs-12">
<div class="col-md-12 col-sm-12 col-xs-12 pad-5 efw-report">
<div class="panel panel-default">
<div class="panel-heading">
    <p> EFW For Creating A Report</p>
</div>
<div class="panel-body">
<div class="col-md-12 col-sm-12 col-xs-12 nopad">
<div class="col-md-5 col-sm-5 col-xs-12">
    <p>Four files are required to visualize a report, –</p>
    <ol>
        <li>EFW (.efw) – Entry point of the report.</li>
        <li>HTML (.html) – Layout of the report.</li>
        <li>EFWVF (.efwvf) – Visualization of the data using javascript.</li>
        <li>EFWD (.efwd) – Data sources are defined here.</li>
    </ol>
</div>
<div class="col-md-7 col-sm-7 col-xs-12">
    <div class="panel panel-default">
        <div class="panel-body">
            <p>EFW is a traditional way of creating a report in Helical Insight by scripting entire report manually, i.e
                – manually scripting the html and javascript. However, for the report to be able to access the data,
                visualize it and display it in the HTML page scripted by the developer, it needs to be in a certain
                format.</p>
        </div>
    </div>
</div>
<div class="col-md-12 col-sm-12 col-xs-12 panel-tab">
<div class="panel panel-default">
<div class="panel-heading">
    <ul class="nav nav-tabs">
        <li class="active"><a href="#efw-tab" data-toggle="tab">
            <span class="chip">Step-1</span> EFW (.efw)</a></li>
        <li><a href="#html-tab" data-toggle="tab">
            <span class="chip">Step-2</span> HTML (.html)</a></li>
        <li><a href="#efwvf-tab" data-toggle="tab">
            <span class="chip">Step-3</span> EFWVF (.efwvf)</a></li>
        <li><a href="#efwd-tab" data-toggle="tab">
            <span class="chip">Step-4</span> EFWD (.efwd)</a></li>
    </ul>
</div>
<div class="panel-body">
<div class="col-md-12 col-sm-12 col-xs-12 nopad">
<div class="tab-content efw-tab">
<div role="tabpanel" class="tab-pane active col-md-12 col-sm-12 col-xs-12 nopad" id="efw-tab">
    <div class="col-md-12 col-sm-12 col-xs-12 nopad">
        <p> The EFW file is an XML file which serves as the entry point for the report. This file contains the
            information required to run the report.</p>

        <div class="col-md-12 col-sm-12 col-xs-12 pad-5">
<pre>&lt;?xml version="1.0" encoding="UTF-8" ?&gt;
&lt;efw&gt;
    &lt;title&gt;Prepare Report&lt;/title&gt;
    &lt;author&gt;name of creator&lt;/author&gt;
    &lt;description&gt;Stage wise detailed information you can get it from this dashboard&lt;/description&gt;
    &lt;icon&gt;images/image.ico&lt;/icon&gt;
    &lt;template&gt;display.html&lt;/template&gt;
    &lt;visible&gt;true&lt;/visible&gt;
    &lt;style&gt;clean&lt;/style&gt;
&lt;/efw&gt;</pre>
        </div>
        <div class="col-md-12 col-sm-12 col-xs-12 pad-5">
            <p><b>Tag Details</b></p>
            <ul>
                <li><strong>&lt;title&gt;</strong> : This tag holds the name of the report as will be visible in the
                    file browser of the application.
                </li>
                <li><strong>&lt;author&gt;</strong> : This tag is used to hold the name of the creator.</li>
                <li><strong>&lt;description&gt;</strong> : This tag holds a brief description of the report.</li>
                <li><strong>&lt;icon&gt;</strong> : Path to the icon to be displayed along with the name of the report.
                </li>
                <li><strong>&lt;template&gt;</strong> : Name of the HTML file which will be invoked by the EFW.</li>
                <li><strong>&lt;visible&gt;</strong> : Boolean value to show or hide the dashboard in the file explorer.
                </li>
            </ul>
        </div>
    </div>
</div>

<div role="tabpanel" class="tab-pane col-md-12 col-sm-12 col-xs-12 nopad" id="html-tab">
    <div class="col-md-12 col-sm-12 col-xs-12 nopad">
        <p> The HTML page is used to display the visualizations on the screen. This file is where the layout of the
            report is defined, the required JS and CSS files are called, CSS is defined, scripts are written and
            charting components are defined. <br> <b>NOTE:</b> &lt;html&gt;, &lt;head&gt; and &lt;body&gt; tags should
            not be added in the HTML file. <br> Charting component:
            The charting components are defined as follow –

        </p>

        <div class="col-md-12 col-sm-12 col-xs-12 pad-5">
<pre>var genderPieChart = {
    name: "genderPieChart",
    type:&nbsp; "chart",
    vf : {
        id : 1,
        file : "MedicalRecords.efwvf"
    },
    htmlElementId : "#genderPieChart",
    executeAtStart: true
};</pre>
        </div>
        <div class="col-md-12 col-sm-12 col-xs-12 pad-5">
            <p>Tag Details</p>
            <ul>
                <li><strong>‘name’ :</strong> Define the name of the component. The component will be accessed by this
                    name.
                </li>
                <li><strong>‘type’ :</strong> By default set to ‘chart’.</li>
                <li><strong>‘vf’ :</strong> Defines the VF file and the VF id in that file is to be used to visualize
                    the data.
                    <br>(i) <strong>‘id’ :</strong> Defines the VF id to be used from the said VF file.
                    <br>(ii) <strong>‘file’ :</strong> Defines the VF file to be used for visualization.
                </li>
                <li><strong>‘htmlElementId’ :</strong> Holds the HTML element ID where the chart will be bound for
                    display.
                </li>
                <li><strong>‘executeAtStart’ :</strong> Boolean value to decide if the component will execute at start
                    or will be triggered on some event.
                </li>
            </ul>
        </div>
        <p>The charting components are initialized as follows :</p>
<pre>var dashboard = Dashboard;
var components = [genderPieChart];
dashboard.init(components);</pre>

        <p>The Dashboard Variables are set retrieved as follows :</p>

        <pre>Set - dashboard.setVariable('gender', 'male'); Get - var gender = dashboard.getVariable('gender');</pre>

        <p>These variables can be used for passing data between different files as parameters or as triggers to be
            listened to for any change in value. </p>

        <div class="col-md-12 col-sm-12 col-xs-12 nopad">
            <a class="btn btn-primary" role="button" data-toggle="collapse" href="#html-example">
                Example
            </a>

            <div class="collapse" id="html-example">
                <div class="col-md-12 col-sm-12 col-xs-12 nopad">
      
<pre>&lt;link rel="stylesheet" type="text/css" href="getExternalResource.html?path=resources/jquery.dataTables.css" /&gt;
&lt;link rel="stylesheet" type="text/css" href="getExternalResource.html?path=resources/dataTables.jqueryui.css" /&gt;
&lt;link rel="stylesheet" type="text/css" href="getExternalResource.html?path=resources/c3.css" /&gt;
&lt;link rel="stylesheet" href="getExternalResource.html?path=resources/pivottbl/pivot.css" type="text/css" /&gt;

&lt;script src="getExternalResource.html?path=resources/c3.js"&gt;&lt;/script&gt;
&lt;script src="getExternalResource.html?path=resources/jquery.dataTables.js"&gt;&lt;/script&gt;
&lt;script src="getExternalResource.html?path=resources/pivottbl/pivot.js"&gt;&lt;/script&gt;
&lt;script src="getExternalResource.html?path=resources/jquery_pivot.js"&gt;&lt;/script&gt;
&lt;script src="getExternalResource.html?path=resources/jquery-ui-1.9.2.custom.min.js"&gt;&lt;/script&gt;

&lt;style type="text/css"&gt;
    .heading {
        text-align: center;
    }
&lt;/style&gt;

&lt;div class="row"&gt;
    &lt;div class=”heading col-sm-12”&gt; &lt;h2&gt;Pie Chart&lt;h2&gt;&lt;/div&gt;
    &lt;div id="genderPieChart col-sm-12"&gt;&lt;/div&gt;
&lt;/div&gt;

&lt;script&gt;
    var dashboard = Dashboard;
    dashboard.resetAll();
    dashboard.setVariable('gender', '');
    var genderPieChart = {
        name: "genderPieChart",
        type:&nbsp; "chart",
        vf : {
            id : 1,
            file : "MedicalRecords.efwvf"
        },
        htmlElementId : "#genderPieChart",
        executeAtStart: true
    };
    var components = [genderPieChart];
    dashboard.init(components);
&lt;/script&gt;</pre>
      
      
<pre>&lt;script&gt;&lt;![CDATA[
var dashboard = Dashboard; 
dashboard.resetAll(); 
dashboard.setVariable('gender', '');
var genderPieChart = { 
    name: "genderPieChart",  
    type: "chart",  
    vf : { 
        id : 1, 
        file : "MedicalRecords.efwvf"
        }, 
    htmlElementId : "#genderPieChart", 
    executeAtStart: true 
}; 
var components = [genderPieChart]; 
dashboard.init(components);
]]&gt;&lt;/script&gt;</pre>

                </div>
            </div>
        </div>


    </div>
</div>

<div role="tabpanel" class="tab-pane col-md-12 col-sm-12 col-xs-12 nopad" id="efwvf-tab">
    <div class="col-md-12 col-sm-12 col-xs-12 nopad">
        <p> The EFWVF (henceforth referred to as ‘VF’) file is the visualization file used for visualising the data in
            the form that is desired. The ‘Charting component’ in the HTML file points to a VF file and a VF id.
            Whenever that component is triggered, the specified VF file is accessed by the application and the specified
            VF id is used to create the visualization.
        </p>

        <div class="col-md-12 col-sm-12 col-xs-12 pad-5">
            <p><b>Template</b></p>
<pre>&lt;Charts&gt; 
    &lt;Chart id="1"&gt; 
        &lt;prop&gt; 
            &lt;name&gt;Pie Chart&lt;/name&gt; 
            &lt;type&gt;Custom&lt;/type&gt; 
            &lt;DataSource&gt;1&lt;/DataSource&gt; 
            &lt;script&gt; 
                &lt;![CDATA[ //Javascript to create visualization console.log(data); ]]&gt; 
            &lt;/script&gt; 
        &lt;/prop&gt; 
    &lt;/Chart&gt;
&lt;/Charts&gt;</pre>
        </div>
        <div class="col-md-12 col-sm-12 col-xs-12 pad-5">
            <p>Tag Details</p>
            <ul>
                <li><strong>&lt;Chart id=”1″&gt; :</strong> This id is used by the ‘Charting Component’ of the HTML file
                    to access the desired VF component.
                </li>
                <li><strong>&lt;name&gt; :</strong> This helps identify what that VF component does. This is for the
                    developers convenience.
                </li>
                <li><strong>&lt;type&gt; :</strong> Default value is ‘custom’.</li>
                <li><strong>&lt;DataSource&gt; :</strong> This tag defines the id of the query in the EFWD file which
                    will be fired to fetch the resultset to be visualized.
                </li>
                <li><strong>&lt;script&gt; :</strong> This tag contains ‘&lt;![CDATA[]]&gt;’ tag, which holds the actual
                    javascript to create the chart.
                </li>
            </ul>
            <p><b>Note</b>The resultset from the query is passed as a JSON array variable named ‘data’.</p>
        </div>
        <div class="col-md-12 col-sm-12 col-xs-12 nopad">
            <a class="btn btn-primary" role="button" data-toggle="collapse" href="#efwvf-example">
                Sample
            </a>

            <div class="collapse" id="efwvf-example">
                <div class="col-md-12 col-sm-12 col-xs-12 nopad">
<pre>&lt;Charts&gt;
    &lt;Chart id="1"&gt;
        &lt;prop&gt;
            &lt;name&gt;Gender Wise Pie Chart&lt;/name&gt;
            &lt;type&gt;Custom&lt;/type&gt;
            &lt;DataSource&gt;1&lt;/DataSource&gt;
            &lt;script&gt;
                &lt;![CDATA[
                    if (data.length == 0) {
                        $('#chart_1').html("
                            &lt;div style='border:1px solid; border-color:#337ab7;'&gt;&lt;h2 style='text-&nbsp;&nbsp;&nbsp;
align:CENTER;color:#927333;'&gt;No Data To Display&lt;/h2&gt;&lt;/div&gt;
                        ");
                         return;
                    } else {
                        var chart1 = c3.generate ({
                            padding: {
                                right: 100,
                                left: 50
                            },
                            bindto: '#chart_1',
                            data: {
                                columns: [
                                    ['Male'].concat(data[0].Male),
                                    ['Female'].concat(data[0].Female),
                                    ['Not Specified'].concat(data[0].NotSpecified)
                                ],
                                type : 'pie',
                                onclick: function (data) {
                                    dashboard.setVariable('gender', data.name);
                                    $('#hiddenPannel').remove();
                                    $('#hiddenPannel1').remove();&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                }
                            },
                            color: {
                                pattern: [ '#de2227','#2ca02c', '#98df8a']
                            }
                        });
                    }
                ]]&gt;
            &lt;/script&gt;
        &lt;/prop&gt;
    &lt;/Chart&gt;
&lt;/Charts&gt;
</pre>
                </div>
            </div>
        </div>


    </div>
</div>


<div role="tabpanel" class="tab-pane col-md-12 col-sm-12 col-xs-12 nopad" id="efwd-tab">
    <div class="col-md-12 col-sm-12 col-xs-12 nopad">
        <p> The EFWD file is the DataSource file, which contains all the queries required to create the report. Each
            query is assigned a unique id, which the VF file uses, to fire a specific query and access its result set.
        </p>

        <div class="col-md-12 col-sm-12 col-xs-12 pad-5">
            <p><b>Template</b></p>
<pre>&lt;EFWD&gt;
    &lt;DataSources&gt;
        &lt;Connection id="1" type="sql.jdbc"&gt;
            &lt;Driver&gt;com.mysql.jdbc.Driver&lt;/Driver&gt;
            &lt;Url&gt;jdbc:mysql://127.0.0.1:3306/efw_db_POC_DM&lt;/Url&gt;
            &lt;User&gt;root&lt;/User&gt;
            &lt;Pass&gt;root&lt;/Pass&gt;
        &lt;/Connection&gt;
    &lt;/DataSources&gt;
    &lt;DataMaps&gt;
        &lt;DataMap id="1" connection="1" type="sql" &gt;
            &lt;Name&gt;Sql Query on SampleData - Jdbc&lt;/Name&gt;
            &lt;Query&gt;
                &lt;![CDATA[
                    SELECT * FROM table WHERE stage_id IN ${stage_id} AND order_date &gt; ${orderDate}
                ]]&gt;
            &lt;/Query&gt;
            &lt;Parameters&gt;
                &lt;Parameter name="stage_id" type="Collection" default="'1','2'"/&gt;
                &lt;Parameter name="orderDate" type="Date" pattern="yyyy-MM-dd" default="2003-03-01"/&gt;
            &lt;/Parameters&gt;
        &lt;/DataMap&gt;
    &lt;/DataMaps&gt;
&lt;/EFWD&gt;
</pre>
        </div>
        <div class="col-md-12 col-sm-12 col-xs-12 pad-5">
            <p>Tag Details</p>
            <ul>
                <li><strong>&lt;DataSources&gt; :</strong> This tag defines the data sources being used for the report.
                </li>
                <li><strong>&lt;Connection id=”1″ type=”sql.jdbc”&gt; :</strong> The connection id is a unique id given
                    to each connection and the type specifies the type of connection it is.
                </li>
                <li><strong>&lt;Driver&gt; :</strong> Specifies the driver of the database being used.</li>
                <li><strong>&lt;Url&gt; :</strong> URL of the database being used.</li>
                <li><strong>&lt;User&gt; :</strong> Username credential for the database.</li>
                <li><strong>&lt;Pass&gt; :</strong> Password credential for the database.</li>
                <li><strong>DataMap id=”1″ connection=”1″ type=”sql” &gt; :</strong><strong>‘id’ :</strong> This Id is
                    used by VF to specify which query to fire.
                    <strong>‘connection’ :</strong> This specifies which datasource connection is to be used.
                </li>
                <li>&nbsp;<strong>&lt;Name&gt; :</strong> This is the label, specifying what the query does.</li>
                <li>&nbsp;<strong>&lt;Query&gt; :</strong> Contains the tag containing the query.</li>
                <li>&nbsp;<strong>&lt;![CDATA[&nbsp;&nbsp; ]]&gt; :</strong> Contains the actual query.</li>
                <li>&nbsp;<strong>&lt;Parameters&gt; :</strong> Contains the parameters to be passed to the query.</li>
                <li>&nbsp;<strong>&lt;Parameter name=”stage_id” type=”Collection” default=”‘1’,’2′”/&gt; :</strong>
                    <strong>‘name’ :</strong> The name of the variable.
                    <strong>‘type’ :</strong> The type of the variable (collection/string/date/etc.).
                    <strong>‘default’ :</strong>&nbsp; The default value of the variable if nothing passed.
                </li>
            </ul>
        </div>
        <div class="col-md-12 col-sm-12 col-xs-12 nopad">
            <a class="btn btn-primary" role="button" data-toggle="collapse" href="#efwd-example">
                Sample
            </a>

            <div class="collapse" id="efwd-example">
                <div class="col-md-12 col-sm-12 col-xs-12 nopad">
<pre>&lt;EFWD&gt;
    &lt;DataSources&gt;
        &lt;Connection id="1" type="sql.jdbc"&gt;
            &lt;Driver&gt;org.postgresql.Driver&lt;/Driver&gt;
            &lt;Url&gt;jdbc:postgresql://192.168.2.9:5432/Global_DataBase&lt;/Url&gt;
            &lt;User&gt;postgres&lt;/User&gt;
            &lt;Pass&gt;postgres&lt;/Pass&gt;
        &lt;/Connection&gt;
    &lt;/DataSources&gt;
&nbsp;
    &lt;DataMaps&gt;
        &lt;DataMap id="1" connection="1" type="sql" &gt;
            &lt;Name&gt;Gender wise classification&lt;/Name&gt;
            &lt;Query&gt;
                &lt;![CDATA[
                    select
                    count(case when gender = 'Male' then 1 end) as "Male",
                    count(case when gender = 'Female' then 1 end) as "Female",
                    count(case when gender is null then 1 end) as "NotSpecified"
                    from medical_records
                ]]&gt;
            &lt;/Query&gt;
        &lt;/DataMap&gt;
    &lt;/DataMaps&gt;
&lt;/EFWD&gt;
</pre>
                </div>
            </div>
        </div>

        <p>For more Info, Contact us at <strong><a href="mailto:demo@helicalinsight.com" target="_blank">demo@helicalinsight.com</a></strong>
        </p>

    </div>
</div>


</div>
</div>


</div>
</div>
</div>


</div>
</div>
</div>
</div>


</div>
</div>

</div>
 