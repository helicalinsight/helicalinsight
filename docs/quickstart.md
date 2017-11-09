# Quick start

It is recommended to install Helical Insight Community Edition from official website.

[Download Helical Insight Community Edition Here](http://www.helicalinsight.com/helical-insight-ce/)

## Product Details

```text
Product Name	  	: Helical Insight
Product Type	  	: Business Intelligence Framework
Product Edition	   : Community
Version	           : 2.0.0.0
Build No.	         : R20171705_6032
License Type	      : Apache Version2
```

## Minimum System Requirement

```text
RAM	            : 4GB
JVM Memory	    : 2GB
Disk Space	    : 10GB
Cores           : 2 Cores   
```

## Supported Platforms

### Supported OS and Browsers

```text
OS Family    : Windows, Linux, Mac

Browser Name : Google Chrome, Firefox, Internet Explorer, Microsoft Edge, etc.


```
## With Eclipse IDE

##### Prerequisites :
* Java version `1.7` or higher
* User should have Maven 3 or higher installed 
* User should have installed jars present in resources folder
* User should have Eclipse IDE.
* User should have any database management system installed like `MySQL`
* User should have any  server like `Tomcat`, `Jboss`, etc. up and running.

	
1. Download ZIP from github and extract project.
2. Open Eclipse IDE and Open the project from file System.
3. After importing project from file system open hi-ce/pom.xml
4. In pom.xml file check for "production" profile.
5. In the said profile configure systemDirectory, logLocation, dbPort, dbServer, dbUser, dbPassword then save the xml file.
6. Run hi-ce module as server. 


## Helical Insight Installation


Please refer to the below tutorial to understand how to install helical insight


## Manual Installation

##### Manual Installation Guide For Binary Helical Insight War File


This Installation Guide will help user to manually configure the necessary settings required to install Helical Insight Application to the user’s system. This configuration is applicable to any Operating System (Windows family, Linux flavours, MacOS), any Databases (Relational, Columnar, Graph, Flatfiles, NOSQL ) user is using.

##### Prerequisites :
* Java version `1.7` or higher
* User should have any database management system installed like `MySQL`
* User should have any  server like `Tomcat`, `Jboss`, etc. up and running.

In order to run Tomcat or any other application server you may need to set `JAVA_HOME` or `JRE_HOME` based on your environment.
[learn to set environment variables ](http://www.helicalinsight.com/technical-guide/configure-java-environment-variables/)

##### The download contains the following directories and files :
* cache
* core
* db-dump
* docs
* export
* externalauth
* hi-ce
* hi-repository
* hwf
* resources
* scheduling
* src
* Third Party Licenses
* validation
* vf-plugin
* LICENSE.md
* pom.xml
* README.md

	![manualinstallation1](_media/Installation/manualinstallation1.png)

##### NOTE :
1. These instructions use the windows file path convention `'\'`
	* For example:  C:\helicalinsight-master\hi-repository  
2. If you are running on linux, please use the convention `'/'` wherever applicable.
	* For example :  /home/helicalinsight-master/hi-repository

##### Steps :
1. Install the sql file `db.sql`, and `SampleTravelData.sql` present in db-dump folder. This contains the database required for `hi-ce` application and sample reports.

	```text
	*\db-dump\SampleData.sql
	*\db-dump\db.sql
	```

2. Edit the file setting.xml present in the hi-repository

	```text
	*\hi-repository\System\Admin\setting.xml
	```	 

    2.1 Find the tag `<efwSolution><\efwSolution>`. By default there is no value.  

     ![manualinstallation2](_media/Installation/manualinstallation2.png)

    2.2 You need to set the value to the `<project-path>\helicalinsight-master\hi-repository` (“C:\\helicalinsight-master\\hi-repository”)  

	 ![manualinstallation3](_media/Installation/manualinstallation3.png)

    2.3 For Windows: If you have copied the `helicalinsight-master` folder in `D:` drive then <efwSolution>D:\\\\helicalinsight-master\\\\hi-repository</efwSolution>

 !>NOTE: Please make sure the path separator is “\\\\” and NOT “\”

   For Linux:   If you have copied the `helicalinsight-master` folder in `/home/user` drive then
   `<efwSolution>/home/user/helicalinsight-master/hi-repository</efwSolution>`

   Find the tag `<BaseUrl><\BaseUrl>`. By default there is no value. You need to set this value to the ip/domain configuration along with the hi path.
	For example, if you have placed the tomcat in www.yourdomain.com/hi then the base URL will be `<BaseUrl>http://www.yourdomain.com/hi-ce/hi.html<\BaseUrl>`
	In case of IP based configuration the above may be your format:   `http://<yourip>:<port>/hi`

	* Example: `<BaseUrl>http://192.168.2.1:8080/hi-ce/hi.html<\BaseUrl>`

3. For Editing the connection details:

	3.1. Go to `*\hi-ce\hi-repository\System\Admin`.

	3.2. Open `globalConnections.xml`

    3.3. Inside the file, find the element `<tomcatJdbcDataSource>`, then find these tags `<url>`, `<username>`,`<password>`.    Also, you can configure the default value for these tags based on your settings.

	 ![manualinstallation4](_media/Installation/manualinstallation4.png)

	Here, you need to set the database URL and port number to make the sample report run.

     ![manualinstallation5](_media/Installation/manualinstallation5.png)

  !>NOTE: This can also be done using hi-ce application by editing the datasource connection.

4. Make sure that Tomcat (or any other application server you have) is up and running, then follow below steps. Copy `hi-ce` war file present in `<project-path>\helicalinsight-master\hi-ce\target` to
    `{TOMCAT-HOME}\webapps` folder. After a few seconds, you can see Tomcat has created one folder with the same name as that of the copied war file. For example, after deploying hi-ce.war file, a folder with name hi-ce would have been created in the same location.		

5. In case of linux environment one needs to have write permissions to change the files that we are going to discuss now. Open              `{TOMCAT_HOME}\webapps\hi-ce\WEB-INF\classes\project.properties` file with any text editor.		
 5.1. Find `settingPath` parameter and replace it with the location of `setting.xml` which is present in System directory.
     Format:    settingPath = {setting.xml Location}
     Example:   settingPath = D:\\helicalinsight-master\\hi-repository\\System\\Admin\\setting.xml

      ![manualinstallation10](_media/Installation/manualinstallation10.png)

     Description: This parameter indicates location of `setting.xml` file, which comes under hi-repository directory. This setting.xml file consists of helical insight server settings.

 5.2. Find schedulerPath parameter and replace the default value with a value where you want the application to store scheduling related data.
     Format:schedulerPath = {scheduling.xml location}
     Example:schedulerPath = D:\\helicalinsight-master\\hi-repository\\System\\scheduling.xml

     ![manualinstallation11](_media/Installation/manualinstallation11.png)

     Description: scheduling.xml file contains information of scheduled reports and related data.

 5.3. The same way as described above, change the value of pluginPath parameter to a directory named Plugins inside System directory.
    Format:pluginPath = {Plugins location}
    Example: pluginPath= D:\\helicalinsight-master\\hi-repository\\System\\Plugins

     ![manualinstallation12](_media/Installation/manualinstallation12.png)

 !>Note: Configuring this parameter is optional.

6. Set Log file location

 6.1 Open `{TOMCAT_HOME}\webapps\hi-ce\WEB-INF\classes\log4j.properties` file in any text editor.

 6.2 Find `log4j.appender.file.File` parameter

 6.3 Replace the default value with `log4j.appender.file.File={Location where you want place the logs of the application}`

    Example: `log4j.appender.file.File=D:\\helicalinsight-master\\hi-repository\\System\\debugLogs.log`

	![manualinstallation13](_media/Installation/manualinstallation13.png)

7. Configure the Database connection and Hibernate dialect.

The configuration shown below configures the Hibernate dialect for the database you are using incase of  MySQL as the database. If you are using some other database, please contact Helical for further assistance. Here configurations are shown for MySQL.

  * Open `{TOMCAT_HOME}\webapps\hi-ce\WEB-INF\application-context.xml` file with any text editor.  

  ![manualinstallation15](_media/Installation/manualinstallation15.png)  

  * Example:

      ```text
    <?xml version="1.0" encoding="UTF-8" standalone="no"?><!--suppress SpringPlaceholdersInspection --><beans xmlns="http://www.springframework.org/schema/beans" xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx" xmlns:util="http://www.springframework.org/schema/util" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.springframework.org/schema/beans        http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/tx        http://www.springframework.org/schema/tx/spring-tx.xsd http://www.springframework.org/schema/context        http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/util        http://www.springframework.org/schema/util/spring-util.xsd">

    <context:component-scan base-package="com.helicalinsight"/>
        <util:properties id="projectProperties" location="classpath:project.properties"/>

        <!-- Root Context: defines shared resources visible to all other web components -->

        <!-- A high performance connection pool -->
        <bean class="com.zaxxer.hikari.HikariDataSource" destroy-method="close" id="dataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="jdbcUrl" value="jdbc:mysql://192.168.2.9:3306/hice"/>
            <property name="username" value="hiuser"/>
            <property name="password" value="hiuser"/>
        </bean>
        <util:properties id="dataSourceProperties" location="classpath:hikari.properties"/>

        <!-- Hibernate 4 SessionFactory Bean definition -->
        <bean class="org.springframework.orm.hibernate4.LocalSessionFactoryBean" id="hibernate4AnnotatedSessionFactory">
            <property name="dataSource" ref="dataSource"/>
            <property name="configLocation" value="classpath:hibernate.cfg.xml"/>
            <property name="hibernateProperties">
                <props>
                    <prop key="hibernate.dialect">org.hibernate.dialect.MySQLDialect</prop>
                    <prop key="hibernate.show_sql">false</prop>
                    <prop key="hibernate.format_sql">false</prop>
                    <prop key="hibernate.format_sql">false</prop>
                    <prop key="hibernate.hbm2ddl.auto">update</prop>
                </props>
            </property>
            </bean>
            <!-- Hibernate Transaction Manager -->
            <bean class="org.springframework.orm.hibernate4.HibernateTransactionManager" id="transactionManager">
            <property name="sessionFactory" ref="hibernate4AnnotatedSessionFactory"/>
            </bean>

            <tx:annotation-driven transaction-manager="transactionManager"/>
            <!--
            The following bean is related to the plug-in for the EFW-Project related to the jrxml execution.
            In case of Java EE full profile application servers, the bean need not be registered explicitly.
            The specific class is already annotated with @Controller annotation. Only in case of Tomcat 6 the class needs to
            wired in the context explicitly, as Tomcat 6 does not scan the WEB-INF/lib for the classes.

            <bean class="com.helicalinsight.efw.controller.EFWPluginController"/>
            -->

            <!-- To prevent browser's back button on displaying secured resource after logout -->
            <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
                <property name="cacheSeconds" value="0"/>
            </bean>

            <bean class="org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler"/>

            <!-- Spring Internationalisation -->

            <bean class="org.springframework.context.support.ResourceBundleMessageSource" id="messageSource">
                <property name="basenames">
                    <list>
                        <value>message</value>
                    </list>
                </property>
            </bean>

            <bean class="org.springframework.web.servlet.i18n.CookieLocaleResolver" id="localeResolver">
              <property name="defaultLocale" value="en"/>
            </bean>

            <bean class="org.springframework.web.servlet.i18n.LocaleChangeInterceptor" id="localeChangeInterceptor">
              <property name="paramName" value="language"/>
            </bean>

            <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping" id="handlerMapping">
                <property name="interceptors" ref="localeChangeInterceptor"/>
            </bean>

            <!--This bean helps to integrate the application with cas service provider-->
            <!--Replace the parameters to work with CAS-->
            <bean class="org.springframework.security.cas.ServiceProperties" id="serviceProperties">
                <property name="service" value="http://{your-ip}:{your-port}/{context}/j_spring_cas_security_check"/>
                <property name="sendRenew" value="false"/>
            </bean>
        </beans>
        ```

	Here the two property values

	```text

    <bean class="com.zaxxer.hikari.HikariDataSource" destroy-method="close" id="dataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="jdbcUrl" value="jdbc:mysql://192.168.2.9:3306/hice"/>
        <property name="username" value="hiuser"/>
        <property name="password" value="hiuser"/>
    </bean>

	```
	and

	```text
	<prop key="hibernate.dialect">org.hibernate.dialect.MySQLDialect</prop>
	```
	need to be changed to the respective database specific values if you are using any other database other than MySQL.

    !> IMPORTANT NOTE: In case of linux environment, you may have to consider the following notes.
     * Tomcat user should have R\W access on the war file.
     * Tomcat user should have R\W access on the hice ‘hi-repository’ directory.

     Following the above steps complete the installation of the helical insight server. Restart your application server(Preferred) or reload the hi application in your application server manager. After that, you should be able to use the application by opening the browser and accessing at the
     ```text
     URL:  http://{Your Server}:{Port}/hi-ce/hi.html
     ```
     * For example: http://192.168.2.9:8080/hi-ce/hi.html

     By default a user base with an organization called “Super Organization” that has two users hiadmin, hiuser is created.

 ##### Default User Credentials :
 ```text
  1.username: hiadmin
    password: hiadmin

  2.username: hiuser
    password: hiuser
 ```

 In case of issue in installing the software, reach out to us at support@helicalinsight.com  
