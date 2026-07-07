import com.helicalinsight.efw.ApplicationProperties
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import com.helicalinsight.efw.utility.JsonUtils;


String path=ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" +
        File.separator + "Static"+File.separator+"exportType.json";
def file = new File(path)
def json = new JsonSlurper().parse(file)
def jsonString =JsonOutput.toJson(json);
def userData=formData.userData
def url = userData.baseUrl.getAsString();
def baseUrl = url.substring(0,url.length()-1)
List<String> allExtension = JsonUtils.getAllVisibleExtension();
def listExtension=JsonOutput.toJson(allExtension)


return """
{
	"jwtToken": ${userData.jwtToken},	
	"adminPaths": {
		"users": "${baseUrl}/admin/users",
		"profiles": "${baseUrl}/admin/profiles",
		"roles": "${baseUrl}/admin/roles",
		"organisations": "${baseUrl}/admin/organisations",
		"services": "${baseUrl}services"
	},
	"services": "${baseUrl}services",
	"optionalReportParams": {},
	
	"recursiveDirectoryLoad": false,
		"HDI": {
		"adhoc": {
			"urls": {
				"services": "${baseUrl}/services",
				"createDataSource": "${baseUrl}/createDataSource",
				"listDataSources": "${baseUrl}/listDataSources",
				"listLocations": "${baseUrl}/listLocations",
				"getResources": "${baseUrl}/getResources",
				"adhocReport": "${baseUrl}/adhocReport"
			}
		}
	},
	experimentalModules:["hwf","hice","dice","cube"],
	fileExtensions: $listExtension,
	experimentalSubModules : {
		"hcr": {
			"disabledComponents": []
		}
	},
	successNotification:true,
	"DashboardGlobals": {
		"solutionLoader": "${baseUrl}/getSolutionResources",
		"resourceLoader": "${baseUrl}/getEFWSolution",
		"updateService": "${baseUrl}/executeDatasource",
		"chartingService": "${baseUrl}/visualizeData",
		"exportData": "${baseUrl}/exportData",
		"reportDownload": "${baseUrl}/downloadReport",
		"productInfo": "${baseUrl}/getProductInformation",
		"productInfoV2": "${baseUrl}/v2/getProductInformation",
		"sendMail": "${baseUrl}/sendMail",
		"updateEFWTemplate": "${baseUrl}/updateEFWTemplate",
		"openHcr ": "${baseUrl}/hcr-report",
		"editHcr ": "${baseUrl}/hcr-report-edit",
		"controllers": {
			"efw": "${baseUrl}/getEFWSolution",
			"efwsr": "${baseUrl}/executeSavedReport",
			"efwfav": "${baseUrl}/executeFavourite",
			"report": "${baseUrl}/hi"
		},
		"saveReport": "${baseUrl}/saveReport",
		"fsop": "${baseUrl}/fileSystemOperations",
		"importFile": "${baseUrl}/importFile",
		"downloadEnableSavedReport": "${baseUrl}/downloadEnableSavedReport",
		"scheduling": {
			"get": "${baseUrl}/getScheduleData",
			"update": "${baseUrl}/updateScheduleData"
		},
		"services": "${baseUrl}/services",
		"designerCreate": "${baseUrl}/designer",
		"designerEdit": "${baseUrl}/designer-edit",
		"ceReportCreate": "${baseUrl}/ce-report-create",
		"ceReportEdit": "${baseUrl}/ce-report-edit",
		"adhocEdit": "${baseUrl}/adhoc/report-edit",
		"datasourceCreate": "${baseUrl}/adhoc/datasources",
		"metadataEdit": "${baseUrl}/adhoc/metadata-edit",
		"metadataCreate": "${baseUrl}/adhoc/metadata-create",
		"adhocReportCreate": "${baseUrl}/adhoc/report-create",
		"helicalReportCreate": "${baseUrl}/adhoc/helical-report",
		"helicalReportEdit": "${baseUrl}/adhoc/helical-report-edit",
		"openAdhoc": "${baseUrl}/hi",
		"openEfw": "${baseUrl}/hi",
		"visualizeAdhoc": "${baseUrl}/visualizeAdhoc"
	},
	"exportType": ${jsonString}

}"""






