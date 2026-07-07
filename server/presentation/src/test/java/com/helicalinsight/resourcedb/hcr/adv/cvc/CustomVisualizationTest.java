package com.helicalinsight.resourcedb.hcr.adv.cvc;


import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomVisualizationTest {
	
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private IntegrationTestUtility integrationTestUtility;
	
	
	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	

	@Before
	@Transactional
	public void setup() {
		ServletContext servletContext =  context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}

	static String tempEfwdUUID = "";
	
	@Test
	public void hcr_cvc_a1_save_datasource_state() throws Exception {
		String formData = "{\"name\":\"_temp_filename\",\"version\":5,\"efwd\":{\"dataSources\":{\"connections\":[{\"connection\":{\"id\":\"1\",\"type\":\"global.jdbc\",\"connDetails\":{\"globalId\":\"1\"}}}]},\"dataMaps\":[{\"dataMap\":{\"name\":\"Query1\",\"id\":1,\"connection\":\"1\",\"type\":\"sql\",\"query\":\"select * from \\\"HIUSER\\\".\\\"dimdate\\\" fetch first 10 rows only\",\"parameters\":[]}}]}}";
		tempEfwdUUID = saveDatasourceState(formData);
	}
	
	@Test
	public void hcr_cvc_a2_adv_generateCvc_with_sample_data() throws Exception {
		
		
		String formData3 = "{\"format\":\"html\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyle\":[],\"parameters\":[{\"name\":\"connectionDetails\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}}],\"variables\":[],\"pageWidth\":1400,\"pageHeight\":1000,\"orientation\":\"Portrait\",\"columnCount\":1,\"summary\":{\"bandHeight\":500,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"table\":[],\"customVisualization\":[{\"type\":\"chart\",\"componentElementProperties\":{\"X\":0,\"Y\":0,\"height\":500,\"width\":900,\"stretchType\":\"NoStretch\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\",\"positionType\":\"FixRelativeTop\"},\"itemData\":{\"data\":[{\"province\":\"Beijing\",\"population\":19612368},{\"province\":\"Tianjin\",\"population\":12938693},{\"province\":\"Hebei\",\"population\":71854210},{\"province\":\"Shanxi\",\"population\":27500000},{\"province\":\"Inner Mongolia\",\"population\":24706291},{\"province\":\"Liaoning\",\"population\":43746323},{\"province\":\"Jilin\",\"population\":27452815},{\"province\":\"Heilongjiang\",\"population\":38313991},{\"province\":\"Shanghai\",\"population\":23019196},{\"province\":\"Jiangsu\",\"population\":78660941},{\"province\":\"Zhejiang\",\"population\":54426891},{\"province\":\"Anhui\",\"population\":59500468},{\"province\":\"Fujian\",\"population\":36894217},{\"province\":\"Jiangxi\",\"population\":44567797},{\"province\":\"Shandong\",\"population\":95792719},{\"province\":\"Henan\",\"population\":94029939},{\"province\":\"Hubei\",\"population\":57237727},{\"province\":\"Hunan\",\"population\":65700762},{\"province\":\"Guangdong\",\"population\":104320459},{\"province\":\"Guangxi\",\"population\":46023761},{\"province\":\"Hainan\",\"population\":8671485},{\"province\":\"Chongqing\",\"population\":28846170},{\"province\":\"Sichuan\",\"population\":80417528},{\"province\":\"Guizhou\",\"population\":34748556},{\"province\":\"Yunnan\",\"population\":45966766},{\"province\":\"Tibet\",\"population\":3002165},{\"province\":\"Shaanxi\",\"population\":37327379},{\"province\":\"Gansu\",\"population\":25575263},{\"province\":\"Qinghai\",\"population\":5626723}]},\"itemProperties\":{\"type\":\"interval\",\"width\":640,\"height\":480,\"depth\":0,\"padding\":0,\"xAxis\":\"province\",\"yAxis\":\"population\",\"color\":\"province\",\"shapre\":\"rect\",\"interaction\":[{\"type\":\"elementHighlight\",\"background\":true,\"region\":true}]}}]},\"dataSets\":[{\"name\":\"MainDataset\",\"dataSetExpression\":\"$P{REPORT_CONNECTION}\",\"isMainDataset\":false,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"fields\":[{\"name\":\"dim_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"date_key\",\"clazz\":\"java.lang.String\"},{\"name\":\"day_number\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_year\",\"clazz\":\"java.sql.Date\"}],\"parameters\":[]}],\"query\":\"select * from \\\"HIUSER\\\".\\\"dimdate\\\"\"},\"type\":\"html\",\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"generateXML\":true,\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		
		String resp = integrationTestUtility.generateHCRReport(formData3);
		JSONObject jsonObject = JSONObject.fromObject(resp);
		int status = jsonObject.getInt("status");
		Assert.assertEquals(1, status);
	}
	
	@Test
	public void hcr_cvc_a3_adv_generateCvc_with_dataset() throws Exception {
		String formData3 = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[{\"name\":\"dim_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"date_key\",\"clazz\":\"java.lang.String\"},{\"name\":\"day_number\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_year\",\"clazz\":\"java.sql.Date\"}],\"designerStyle\":[],\"parameters\":[{\"name\":\"MAIN_DATASET\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}}],\"variables\":[],\"pageWidth\":1400,\"pageHeight\":1000,\"orientation\":\"Portrait\",\"columnCount\":1,\"summary\":{\"bandHeight\":500,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"table\":[],\"customVisualization\":[{\"type\":\"chart\",\"componentElementProperties\":{\"X\":0,\"Y\":0,\"height\":500,\"width\":900,\"stretchType\":\"NoStretch\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\",\"positionType\":\"FixRelativeTop\"},\"itemData\":{\"dataSetRun\":{\"dataSetName\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"items\":{\"dim_id\":\"$F{dim_id}\",\"date_key\":\"$F{date_key}\"}},\"data\":[]},\"itemProperties\":{\"type\":\"interval\",\"width\":640,\"height\":480,\"depth\":0,\"padding\":0,\"xAxis\":\"date_key\",\"yAxis\":\"dim_id\",\"color\":\"date_key\",\"shape\":\"rect\",\"interaction\":[{\"type\":\"elementHighlight\",\"background\":true,\"region\":true}]}}]},\"dataSets\":[{\"name\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"isMainDataset\":false,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"fields\":[{\"name\":\"dim_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"date_key\",\"clazz\":\"java.lang.String\"},{\"name\":\"day_number\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_year\",\"clazz\":\"java.sql.Date\"}],\"parameters\":[]}]},\"type\":\"pdf\",\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"generateXML\":true,\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData3);
		JSONObject jsonObject = JSONObject.fromObject(resp);
		int status = jsonObject.getInt("status");
		Assert.assertEquals(1, status);
	}
	
	
	@Test
	public void hcr_cvc_a4_adv_generateCvc_with_g2_customScript() throws Exception {
		String formData3 = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyle\":[],\"parameters\":[{\"name\":\"MAIN_DATASET\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}}],\"variables\":[],\"pageWidth\":1000,\"pageHeight\":600,\"orientation\":\"Portrait\",\"columnCount\":1,\"summary\":{\"bandHeight\":500,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"table\":[],\"customVisualization\":[{\"type\":\"custom\",\"customScript\":{\"script\":\"define(\\\"CVC\\\",[\\\"g2\\\",\\\"renderer\\\"],(function(e){return function(t){window.instanceData=t;const n=document.getElementById(t.id),o=new e.Chart({container:n,theme:t.theme||\\\"classic\\\"});const r=JSON.parse(t.interaction||\\\"[]\\\");console.log(\\\"interaction\\\",r),o.options({type:t.type||\\\"interval\\\",width:+t.width,height:+t.height,autoFit:\\\"true\\\"===(\\\"\\\"+t.autoFit).toLowerCase(),encode:{x:\\\"date_key\\\",y:\\\"dim_id\\\",color:\\\"date_key\\\"},data:t.series&&t.series[0],interaction:[{type:\\\"elementHighlight\\\",background:!0,region:!0}]}),r.length>0&&o.interaction(r),o.render()}}));\",\"css\":\"\",\"module\":\"CVC\",\"dependencies\":{\"g2\":\"g2.min\",\"renderer\":\"renderer\"}},\"componentElementProperties\":{\"X\":0,\"Y\":0,\"height\":500,\"width\":900,\"stretchType\":\"NoStretch\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\",\"positionType\":\"FixRelativeTop\"},\"itemData\":{\"dataSetRun\":{\"dataSetName\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"items\":{\"dim_id\":\"$F{dim_id}\",\"date_key\":\"$F{date_key}\"}},\"data\":[]},\"itemProperties\":{\"type\":\"interval\",\"width\":640,\"height\":480,\"depth\":0,\"padding\":0,\"xAxis\":\"dim_id\",\"yAxis\":\"date_key\",\"color\":\"dim_id\",\"shape\":\"rect\",\"interaction\":[{\"type\":\"elementHighlight\",\"background\":true,\"region\":true}]}}]},\"dataSets\":[{\"name\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"isMainDataset\":false,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"fields\":[{\"name\":\"dim_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"date_key\",\"clazz\":\"java.lang.String\"},{\"name\":\"day_number\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_year\",\"clazz\":\"java.sql.Date\"}],\"parameters\":[]}]},\"type\":\"pdf\",\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"generateXML\":true,\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData3);
		JSONObject jsonObject = JSONObject.fromObject(resp);
		int status = jsonObject.getInt("status");
		Assert.assertEquals(1, status);
	}
	
	
	@Test
	public void hcr_cvc_a5_adv_generateCvc_with_d3_customScript() throws Exception {
		String script = "define(\\\"CVC\\\",[\\\"d3\\\"],(function(t){return function(e){var a=e.width||400,n=e.height||250,r=n/2-40,l=t.format(\\\"s\\\"),i=t.scale.ordinal().range([\\\"#8dd3c7\\\",\\\"#ffffb3\\\",\\\"#bebada\\\",\\\"#fb8072\\\",\\\"#80b1d3\\\",\\\"#fdb462\\\",\\\"#b3de69\\\",\\\"#fccde5\\\",\\\"#d9d9d9\\\",\\\"#bc80bd\\\",\\\"#ccebc5\\\",\\\"#ffed6f\\\"]),s=t.select(\\\"#\\\"+e.id).append(\\\"svg\\\").attr(\\\"id\\\",e.id+\\\"svg\\\").attr(\\\"width\\\",a).attr(\\\"height\\\",n).append(\\\"g\\\").attr(\\\"transform\\\",\\\"translate(\\\"+a/2+\\\",\\\"+n/2+\\\")\\\");const d=e.series[0].map((function(t){return{name:t.date_key,value:t.dim_id}}));d.sort((function(t,e){return e.value-t.value}));var c=t.extent(d,(function(t){return t.value})),o=t.scale.linear().domain(c).range([0,r]),u=d.map((function(t,e){return t.name})),f=u.length,p=t.scale.linear().domain(c).range([0,-r]),h=t.svg.axis().scale(p).orient(\\\"left\\\").ticks(3).tickFormat(l),b=(s.selectAll(\\\"circle\\\").data(p.ticks(3)).enter().append(\\\"circle\\\").attr(\\\"r\\\",(function(t){return o(t)})).style(\\\"fill\\\",\\\"none\\\").style(\\\"stroke\\\",\\\"black\\\").style(\\\"stroke-dasharray\\\",\\\"2,2\\\").style(\\\"stroke-width\\\",\\\".5px\\\"),t.svg.arc().startAngle((function(t,e){return 2*e*Math.PI/f})).endAngle((function(t,e){return 2*(e+1)*Math.PI/f})).innerRadius(0));s.selectAll(\\\"path\\\").data(d).enter().append(\\\"path\\\").each((function(t){t.outerRadius=0})).style(\\\"fill\\\",(function(t){return i(t.name)})).attr(\\\"d\\\",b).transition().ease(\\\"elastic\\\").duration(0).attrTween(\\\"d\\\",(function(e,a){var n=t.interpolate(e.outerRadius,o(+e.value));s.append(\\\"circle\\\").attr(\\\"r\\\",r).classed(\\\"outer\\\",!0).style(\\\"fill\\\",\\\"none\\\").style(\\\"stroke\\\",\\\"black\\\").style(\\\"stroke-width\\\",\\\"1.5px\\\");s.selectAll(\\\"line\\\").data(u).enter().append(\\\"line\\\").attr(\\\"y2\\\",-r-20).style(\\\"stroke\\\",\\\"black\\\").style(\\\"stroke-width\\\",\\\".5px\\\").attr(\\\"transform\\\",(function(t,e){return\\\"rotate(\\\"+360*e/f+\\\")\\\"}));s.append(\\\"g\\\").attr(\\\"class\\\",\\\"x axis\\\").call(h);var l=1.025*r,d=s.append(\\\"g\\\").classed(\\\"labels\\\",!0);return d.append(\\\"defs\\\").append(\\\"path\\\").attr(\\\"id\\\",\\\"label-path\\\").attr(\\\"d\\\",\\\"m0 \\\"+-l+\\\" a\\\"+l+\\\" \\\"+l+\\\" 0 1,1 -0.01 0\\\"),d.selectAll(\\\"text\\\").data(u).enter().append(\\\"text\\\").style(\\\"text-anchor\\\",\\\"middle\\\").style(\\\"font-weight\\\",\\\"bold\\\").style(\\\"fill\\\",(function(t){return i(t)})).append(\\\"textPath\\\").attr(\\\"xlink:href\\\",\\\"#label-path\\\").attr(\\\"startOffset\\\",(function(t,e){return 100*e/f+50/f+\\\"%\\\"})).text((function(t){return t.toUpperCase()})),function(t){return e.outerRadius=n(t),b(e,a)}}))}}));";
		String formData3 = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyle\":[],\"parameters\":[{\"name\":\"MAIN_DATASET\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}}],\"variables\":[],\"pageWidth\":1000,\"pageHeight\":600,\"orientation\":\"Portrait\",\"columnCount\":1,\"summary\":{\"bandHeight\":500,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"table\":[],\"customVisualization\":[{\"type\":\"custom\",\"customScript\":{\"script\":\""+script+"\",\"css\":\"\",\"module\":\"CVC\",\"dependencies\":{\"d3\":\"d3.min\"}},\"componentElementProperties\":{\"X\":0,\"Y\":0,\"height\":500,\"width\":900,\"stretchType\":\"NoStretch\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\",\"positionType\":\"FixRelativeTop\"},\"itemData\":{\"dataSetRun\":{\"dataSetName\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"items\":{\"dim_id\":\"$F{dim_id}\",\"date_key\":\"$F{date_key}\"}},\"data\":[]},\"itemProperties\":{\"type\":\"interval\",\"width\":640,\"height\":480,\"depth\":0,\"padding\":0,\"xAxis\":\"dim_id\",\"yAxis\":\"date_key\",\"color\":\"dim_id\",\"shape\":\"rect\",\"interaction\":[{\"type\":\"elementHighlight\",\"background\":true,\"region\":true}]}}]},\"dataSets\":[{\"name\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"isMainDataset\":false,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"fields\":[{\"name\":\"dim_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"date_key\",\"clazz\":\"java.lang.String\"},{\"name\":\"day_number\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_year\",\"clazz\":\"java.sql.Date\"}],\"parameters\":[]}]},\"type\":\"pdf\",\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"generateXML\":true,\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData3);
		JSONObject jsonObject = JSONObject.fromObject(resp);
		int status = jsonObject.getInt("status");
		Assert.assertEquals(1, status);
	}
	
	@Test
	public void hcr_cvc_a6_dependencyApiTest() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/hcrConfig");
		Map<String, String> map = new HashMap<>();
		map.put("username", "hiadmin");
		map.put("password","hiadmin");
		map.put("formData", "");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		efwMock.perform(builder)
			.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	}
	
	
	
	private String saveDatasourceState(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "hcr");
		map.put("serviceType", "report");
		map.put("service", "saveReportState");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		Assert.assertEquals(200, result.getResponse().getStatus());
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		return  jsonObject.getJSONObject("response").getString("temp_uuid");
	}
}
