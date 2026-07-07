================================================
SSO
================================================

**SSO By default SSO is enabled in helicalinsight:


**Deploy SSO App in tomcat

IMP :SSO app is designed to connect to https HI Application and then login to HI app using sso token.
     

1. Make sure that in your local system apache tomcat should be present where you want to have sso.
2. You should be running your local tomat in https mode. (Please check below steps for enabling https)

3. Add below cookie class inside apache-tomcat/conf/context.xml file where  helicalinsight tomcat is running
   just below </WatchedResource>
	<CookieProcessor className="org.apache.tomcat.util.http.Rfc6265CookieProcessor"/>
4. Restart the hi-ee tomcat 


5. Copy sso App from helicalinsight /webapps/hi-ee/sso folder
         to your apache tomcat/webapps 
		 
6. Edit the index.html file present in the sso folder and 
         replace ${host} with your host ip and
		replace ${port} with the port number
        if port number is not configured then replace :${port} with blank
7. Replace {SSO-TOKEN} with  your sso token 
8. Save the file.  
9. Access the app using https://localhost:8443/sso
   
================================================   
Steps to enable https in tomcat   
================================================

1. Enable https in apache tomcat server.xml file  - Add below tag after line number -79 
  <Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true"
           maxThreads="150" scheme="https" secure="true">
    <SSLHostConfig>
        <Certificate
            certificateKeystoreFile="D:/selfsigned.jks"
            certificateKeystorePassword="<password>"
            type="RSA"
        />
    </SSLHostConfig>
</Connector>

   Replace <password> with your password 
   Note : We need to create the keystoreFile using below command-
   
   create self signed .jks file to enable SSL in apache tomcat

	keytool -genkey -keyalg RSA -noprompt -alias tomcat -dname "CN=localhost, OU=NA, O=NA, L=NA, S=NA, C=NA" -keystore selfsigned.jks -validity 9999 -storepass <storepassword> -keypass <password>
	Replace the <storepassword> with your password and <password> with your password
	
2.  Now restart the apache tomcat
3.  You can access the SSO App with URL - https://localhost:8443/sso
4. 	If getting 403 error then comment below line in /manager/META_INF/context.xml

  <!--<Valve className="org.apache.catalina.valves.RemoteAddrValve"
         allow="127\.\d+\.\d+\.\d+|::1|0:0:0:0:0:0:0:1" />-->
		 


**Steps for Testing:
1. Access the sso using  https://localhost:8443/sso
2. Using the sso app. you can see the login button  click on the login button.
3. The sso session will be created.
4. Click on the Reports button and then list of reports file name appears 
5. Then click on any report, for example hreport, desinger or canned report, they  should render the hreport, dashboard etc.                                                
