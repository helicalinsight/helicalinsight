import java.awt.GraphicsEnvironment;
import groovy.json.*;
import  org.springframework.security.saml.metadata.*;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

MetadataManager metadataManager = ApplicationContextAccessor.getBean(MetadataManager.class);
String hostedSPName = metadataManager.getHostedSPName();

        Set<String> spEntityNames = metadataManager.getSPEntityNames();
        Set<String> idpEntityNames = metadataManager.getIDPEntityNames();
        List<ExtendedMetadataDelegate> availableProviders = metadataManager.getAvailableProviders();
		
		
		
     String spList = "";
        for (String s : spEntityNames) {
		    spList = spList+"\"" + s + "\""+",";
        }
		spList=spList+"-"


	String spnList = "";
        for (String s : idpEntityNames) {
		    spnList = spnList+"\"" + s + "\""+",";
        }	
		
		spnList=spnList+"-"
		
		
		
		



	String smList = "";
        for (ExtendedMetadataDelegate s : availableProviders) {
		      smList = smList+"\"" + s + "\""+",";
        }
		smList=smList+"-"










		
		


def jsonObj='''

{
	"defaultLocalServiceProviders": [
		"'''+hostedSPName+'''"
	],
	"serviceProviders": ['''+spList+'''
	],
	"identityProviders": ['''+spnList+'''
	],
	"metadataProviders": ['''+smList+'''
	]
}'''
return jsonObj














