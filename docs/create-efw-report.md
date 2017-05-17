# Creating  Reports and Dashboards

In this article, you will learn about creating a report using `EFW`. EFW is a traditional way of creating a report in Helical Insight by scripting entire report manually, i.e – manually scripting the html and javascript. However, for the report to be able to access the data, visualize it and display it in the HTML page scripted by the developer, it needs to be in a certain format.

##### Four files are required to visualize a report, –

1. `EFW (.efw)`     – Entry point of the report.
2. `HTML (.html)`   – Layout of the report.
3. `EFWVF (.efwvf)` – Visualization of the data using javascript.
4. `EFWD (.efwd)`   – Data sources are defined here

#### EFW : 
The `EFW` file is an XML file which serves as the entry point for the report. This file contains the information required to run the report.

```text
<?xml version="1.0" encoding="UTF-8" ?>
<efw>
    <title>Prepare Report</title>
    <author>name of creator</author>
    <description>Stage wise detailed information you can get it from this dashboard</description>
    <icon>images/image.ico</icon>
    <template>display.html</template>
    <visible>true</visible>
    <style>clean</style>
</efw>
```

##### Tag Details
* `<title>`	      : This tag holds the name of the report and it  will be visible in the file browser.
* `<author>`      : This tag is used to hold the name of the creator.
* `<description>` : This tag holds a brief description of the report.
* `<icon>`        : Path to the icon to be displayed along with the name of the report.
* `<template>`    : Name of the HTML file which will be invoked by the EFW.
* `<visible>`     : Boolean value to show or hide the dashboard in the file browser.

#### HTML : 
The `HTML` page is used to display the visualizations on the screen. This file is where the layout of the report is defined, the required JS and CSS files are called,  CSS is defined, scripts are written and charting components are defined. 

!> NOTE: <html>, <head> and <body> tags should not be added in the HTML file. 

Charting component: The charting components are defined as follow –

```text
var genderPieChart = {
    name: "genderPieChart",
    type:  "chart",
    vf : {
        id : 1,
        file : "MedicalRecords.efwvf"
    },
    htmlElementId : "#genderPieChart",
    executeAtStart: true
};
```

##### Tag Details
* `name` : Define the name of the component. The component will be accessed by this name.
* `type` : By default set to ‘chart’.
* `vf` : Defines the VF file and the VF id in that file is to be used to visualize the data.                        
 1. `id` : Defines the VF id to be used from the said VF file.
 2. `file` : Defines the VF file to be used for visualization.
* `htmlElementId` : Holds the HTML element ID where the chart will be bound for display.
* `executeAtStart` : Boolean value to decide if the component will execute at start or will be triggered on some event.

The charting components are initialized as follows :

```text
var dashboard = Dashboard;
var components = [genderPieChart];
dashboard.init(components);
```

The Dashboard Variables are set retrieved as follows :

```text
Set - dashboard.setVariable('gender', 'male'); Get - var gender = dashboard.getVariable('gender');
```

These variables can be used for passing data between different files as parameters or as triggers to be listened to for any change in value. 

##### Example :

```text
<link rel="stylesheet" type="text/css" href="getExternalResource.html?path=resources/jquery.dataTables.css" />
<link rel="stylesheet" type="text/css" href="getExternalResource.html?path=resources/dataTables.jqueryui.css" />
<link rel="stylesheet" type="text/css" href="getExternalResource.html?path=resources/c3.css" />
<link rel="stylesheet" href="getExternalResource.html?path=resources/pivottbl/pivot.css" type="text/css" />

<script src="getExternalResource.html?path=resources/c3.js"></script>
<script src="getExternalResource.html?path=resources/jquery.dataTables.js"></script>
<script src="getExternalResource.html?path=resources/pivottbl/pivot.js"></script>
<script src="getExternalResource.html?path=resources/jquery_pivot.js"></script>
<script src="getExternalResource.html?path=resources/jquery-ui-1.9.2.custom.min.js"></script>

<style type="text/css">
    .heading {
        text-align: center;
    }
</style>
<div class="row">
    <div class=”heading col-sm-12”> <h2>Pie Chart<h2></div>
    <div id="genderPieChart col-sm-12"></div>
</div>
<script>
    var dashboard = Dashboard;
    dashboard.resetAll();
    dashboard.setVariable('gender', '');
    var genderPieChart = {
        name: "genderPieChart",
        type:  "chart",
        vf : {
            id : 1,
            file : "MedicalRecords.efwvf"
        },
        htmlElementId : "#genderPieChart",
        executeAtStart: true
    };
    var components = [genderPieChart];
    dashboard.init(components);
</script>
```

```text
<script><![CDATA[
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
]]></script>
```

#### EFWVF : 
The `EFWVF` (henceforth referred to as `VF`) file is the visualization file used for visualising the data in the form that is desired. The ‘Charting component’ in the HTML file points to a VF file and a VF id. Whenever that component is triggered, the specified VF file is accessed by the application and the specified VF id is used to create the visualization.

##### Template

```text
<Charts> 
    <Chart id="1"> 
        <prop> 
            <name>Pie Chart</name> 
            <type>Custom</type> 
            <DataSource>1</DataSource> 
            <script> 
                <![CDATA[ //Javascript to create visualization console.log(data); ]]> 
            </script> 
        </prop> 
    </Chart>
</Charts>
```

##### Tag Details
* `<Chart id=”1″>`  : This id is used by the ‘Charting Component’ of the HTML file to access the desired VF component. 
* `<name>`          : This helps identify what that VF component does. This is for the developers convenience. 
* `<type>`          : Default value is ‘custom’. 
* `<DataSource>`    : This tag defines the id of the query in the EFWD file which will be fired to fetch the resultset to be visualized. 
* `<script>`        : This tag contains ‘<![CDATA[]]>’ tag, which holds the actual javascript to create the chart. 

!> NOTE: The resultset from the query is passed as a JSON array variable named `data`.

##### Sample

```text
<Charts>
    <Chart id="1">
        <prop>
            <name>Gender Wise Pie Chart</name>
            <type>Custom</type>
            <DataSource>1</DataSource>
            <script>
                <![CDATA[
                    if (data.length == 0) {
                        $('#chart_1').html("
                    <div style='border:1px solid; border-color:#337ab7;'><h2 style='text-   
					align:CENTER;color:#927333;'>No Data To Display</h2></div>
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
                                    $('#hiddenPannel1').remove();               
                                }
                            },
                            color: {
                                pattern: [ '#de2227','#2ca02c', '#98df8a']
                            }
                        });
                    }
                ]]>
            </script>
        </prop>
    </Chart>
</Charts>
```

#### EFWD : 
The `EFWD` file is the DataSource file, which contains all the queries required to create the report. Each query is assigned a unique id, which the VF file uses, to fire a specific query and access its result set. 

##### Template

```text
<EFWD>
    <DataSources>
        <Connection id="1" type="sql.jdbc">
            <Driver>com.mysql.jdbc.Driver</Driver>
            <Url>jdbc:mysql://127.0.0.1:3306/efw_db_POC_DM</Url>
            <User>root</User>
            <Pass>root</Pass>
        </Connection>
    </DataSources>
    <DataMaps>
        <DataMap id="1" connection="1" type="sql" >
            <Name>Sql Query on SampleData - Jdbc</Name>
            <Query>
                <![CDATA[
                    SELECT * FROM table WHERE stage_id IN ${stage_id} AND order_date > ${orderDate}
                ]]>
            </Query>
            <Parameters>
                <Parameter name="stage_id" type="Collection" default="'1','2'"/>
                <Parameter name="orderDate" type="Date" pattern="yyyy-MM-dd" default="2003-03-01"/>
            </Parameters>
        </DataMap>
    </DataMaps>
</EFWD>
```

##### Tag Details
*`<DataSources>` : This tag defines the data sources being used for the report.
* `<Connection id=”1″ type=”sql.jdbc”>` : The connection id is a unique id given to each connection and the type specifies the type of connection it is.
* `<Driver>` : Specifies the driver of the database being used.
* `<Url>` : URL of the database being used.
* `<User>` : Username credential for the database.
* `<Pass>` : Password credential for the database.
* `DataMap id=”1″ connection=”1″ type=”sql” >` :
 1. `id` 		 : This Id is used by VF to specify which query to fire. 
 2. `connection` : This specifies which datasource connection is to be used.
* `<Name>` : This is the label, specifying what the query does.
* `<Query>` : Contains the tag containing the query.
* `<![CDATA[   ]]>` : Contains the actual query.
* `<Parameters>` : Contains the parameters to be passed to the query.
* `<Parameter name=”stage_id” type=”Collection” default=”‘1’,’2′”/>` : 
 1. `name`    : The name of the variable. 
 2. `type`    : The type of the variable (collection/string/date/etc.). 
 3. `default` :  The default value of the variable if nothing passed.
 
##### Sample

```text
<EFWD>
    <DataSources>
        <Connection id="1" type="sql.jdbc">
            <Driver>org.postgresql.Driver</Driver>
            <Url>jdbc:postgresql://192.168.2.9:5432/Global_DataBase</Url>
            <User>postgres</User>
            <Pass>postgres</Pass>
        </Connection>
    </DataSources>
 
    <DataMaps>
        <DataMap id="1" connection="1" type="sql" >
            <Name>Gender wise classification</Name>
            <Query>
                <![CDATA[
                    select
                    count(case when gender = 'Male' then 1 end) as "Male",
                    count(case when gender = 'Female' then 1 end) as "Female",
                    count(case when gender is null then 1 end) as "NotSpecified"
                    from medical_records
                ]]>
            </Query>
        </DataMap>
    </DataMaps>
</EFWD>
```

