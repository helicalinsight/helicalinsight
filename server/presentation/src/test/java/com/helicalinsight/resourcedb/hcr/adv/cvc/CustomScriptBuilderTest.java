package com.helicalinsight.resourcedb.hcr.adv.cvc;

import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.jasperintegration.HCRCustomScriptCompiler;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

public class CustomScriptBuilderTest {
	
	private static final String TEMP_DIR = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
	
	@Test
	public void compileG2ChartJs() {

		String jsAsString = "define(\"CVC\",[\"g2\",\"renderer\"],(function(e){return function(t){window.instanceData=t;const n=document.getElementById(t.id),o=new e.Chart({container:n,theme:t.theme||\"classic\"});const r=JSON.parse(t.interaction||\"[]\");console.log(\"interaction\",r),o.options({type:t.type||\"interval\",width:+t.width,height:+t.height,autoFit:\"true\"===(\"\"+t.autoFit).toLowerCase(),encode:{x:\"genre\",y:\"sold\",color:\"genre\"},data:t.series&&t.series[0]||[{genre:\"Sports\",sold:275},{genre:\"Strategy\",sold:115},{genre:\"Action\",sold:120},{genre:\"Shooter\",sold:350},{genre:\"Other\",sold:150}],interaction:[{type:\"elementHighlight\",background:!0,region:!0}]}),r.length>0&&o.interaction(r),o.render()}}));";
		String moduleName = "CVC";
		String deps = "{\"g2\":\"g2.min\",\"renderer\":\"renderer\"}";
		HCRCustomScriptCompiler compiler = new HCRCustomScriptCompiler();
		String dir = Paths.get(TEMP_DIR, String.valueOf(System.currentTimeMillis())).toString();
		String outputFilePath = compiler.compile(jsAsString, moduleName, deps,dir);
		Assert.assertNotEquals("", outputFilePath);
	}
	
	@Test
	public void compileD3ChartJs() {

		String jsAsString = "define(\"CVC\",[\"d3\"],(function(t){return function(e){var a=e.width||400,n=e.height||250,r=n/2-40,l=t.format(\"s\"),s=t.scale.ordinal().range([\"#8dd3c7\",\"#ffffb3\",\"#bebada\",\"#fb8072\",\"#80b1d3\",\"#fdb462\",\"#b3de69\",\"#fccde5\",\"#d9d9d9\",\"#bc80bd\",\"#ccebc5\",\"#ffed6f\"]),i=t.select(\"#\"+e.id).append(\"svg\").attr(\"id\",e.id+\"svg\").attr(\"width\",a).attr(\"height\",n).append(\"g\").attr(\"transform\",\"translate(\"+a/2+\",\"+n/2+\")\");const d=[{CATEGORY:\"Electronics\",VALUE:15230.75},{CATEGORY:\"Home Appliances\",VALUE:11300.5},{CATEGORY:\"Books\",VALUE:8900},{CATEGORY:\"Clothing\",VALUE:7450.2},{CATEGORY:\"Toys\",VALUE:6825.6}].map((function(t){return{name:t.CATEGORY,value:t.VALUE}}));d.sort((function(t,e){return e.value-t.value}));var c=t.extent(d,(function(t){return t.value})),o=t.scale.linear().domain(c).range([0,r]),u=d.map((function(t,e){return t.name})),f=u.length,p=t.scale.linear().domain(c).range([0,-r]),h=t.svg.axis().scale(p).orient(\"left\").ticks(3).tickFormat(l),A=(i.selectAll(\"circle\").data(p.ticks(3)).enter().append(\"circle\").attr(\"r\",(function(t){return o(t)})).style(\"fill\",\"none\").style(\"stroke\",\"black\").style(\"stroke-dasharray\",\"2,2\").style(\"stroke-width\",\".5px\"),t.svg.arc().startAngle((function(t,e){return 2*e*Math.PI/f})).endAngle((function(t,e){return 2*(e+1)*Math.PI/f})).innerRadius(0));i.selectAll(\"path\").data(d).enter().append(\"path\").each((function(t){t.outerRadius=0})).style(\"fill\",(function(t){return s(t.name)})).attr(\"d\",A).transition().ease(\"elastic\").duration(0).attrTween(\"d\",(function(e,a){var n=t.interpolate(e.outerRadius,o(+e.value));i.append(\"circle\").attr(\"r\",r).classed(\"outer\",!0).style(\"fill\",\"none\").style(\"stroke\",\"black\").style(\"stroke-width\",\"1.5px\");i.selectAll(\"line\").data(u).enter().append(\"line\").attr(\"y2\",-r-20).style(\"stroke\",\"black\").style(\"stroke-width\",\".5px\").attr(\"transform\",(function(t,e){return\"rotate(\"+360*e/f+\")\"}));i.append(\"g\").attr(\"class\",\"x axis\").call(h);var l=1.025*r,d=i.append(\"g\").classed(\"labels\",!0);return d.append(\"defs\").append(\"path\").attr(\"id\",\"label-path\").attr(\"d\",\"m0 \"+-l+\" a\"+l+\" \"+l+\" 0 1,1 -0.01 0\"),d.selectAll(\"text\").data(u).enter().append(\"text\").style(\"text-anchor\",\"middle\").style(\"font-weight\",\"bold\").style(\"fill\",(function(t){return s(t)})).append(\"textPath\").attr(\"xlink:href\",\"#label-path\").attr(\"startOffset\",(function(t,e){return 100*e/f+50/f+\"%\"})).text((function(t){return t.toUpperCase()})),function(t){return e.outerRadius=n(t),A(e,a)}}))}}));";
		String moduleName = "CVC";
		String deps = "{\"d3\":\"d3.min\"}";
		HCRCustomScriptCompiler compiler = new HCRCustomScriptCompiler();
		String dir = Paths.get(TEMP_DIR, String.valueOf(System.currentTimeMillis())).toString();
		String outputFilePath = compiler.compile(jsAsString, moduleName, deps,dir);
		Assert.assertNotEquals("", outputFilePath);
	}
	
	@Test
	public void getAllDependencies() {
		List<String> dependencies =  HCRUtils.findAllDependencies();
		Assert.assertNotNull(dependencies);
		Assert.assertTrue(!dependencies.isEmpty());
	}
}
