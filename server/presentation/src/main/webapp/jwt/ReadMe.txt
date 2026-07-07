================================================
JWT
================================================

**JWT By default JWT is enabled in helicalinsight:


**Deploy JWT App in tomcat

IMP :JWT app is designed to connect to https HI Application and then login to HI app using jwt token.
     

1. Make sure that in your local system apache tomcat should be present where you want to have jwt.
2. You should be running your local tomat in https mode. (Please check below steps for enabling https)

3. Add below cookie class inside apache-tomcat/conf/context.xml file where  helicalinsight tomcat is running
   just below </WatchedResource>
	<CookieProcessor className="org.apache.tomcat.util.http.Rfc6265CookieProcessor"/>
4. Restart the hi-ee tomcat 


5. Copy JWT App from helicalinsight /webapps/hi-ee/jwt folder
         to your apache tomcat/webapps 
		 
6. Edit the index.html file present in the jwt folder and 
         replace ${host} with your host ip and
		replace ${port} with the port number
        if port number is not configured then replace :${port} with blank
7. Save the file.  
8.  You can access the JWT App with URL - https://localhost:8443/jwt
   
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
3.  You can access the JWT App with URL - https://localhost:8443/jwt
4. 	If getting 403 error then comment below line in /manager/META_INF/context.xml

  <!--<Valve className="org.apache.catalina.valves.RemoteAddrValve"
         allow="127\.\d+\.\d+\.\d+|::1|0:0:0:0:0:0:0:1" />-->
		 


**Steps for Testing:
1. Access the jwt using  https://localhost:8443/jwt
2. Using the jwt app. you can see the login button  click on the login button.
3. The jwt session will be created.
4. Click on the Reports button and then list of reports file name appears 
5. Then click on any report, for example hreport, desinger or canned report, they  should render the hreport, dashboard etc.                                                
