import { QuestionCircleOutlined } from "@ant-design/icons";
import { Drawer, Tooltip } from "antd";
import React, { useState } from "react";

import { Typography, Collapse } from "antd";

const { Title, Paragraph, Text } = Typography;

const TabData = () => {
  return (
    <div style={{ padding: "20px" }}>
      <Title level={3}>Tab Properties</Title>

      <Paragraph>
        <Text strong>You can use the below properties in the js editor.</Text>
      </Paragraph>

      <Collapse>
<pre>
{`properties.tab.enable = true;
properties.tab.numberOfTabs = 2;
properties.tab.tabNames = "Tab 1, Tab 2";
properties.tab.tabType = "card";
properties.tab.activeTab = "Tab 1";
properties.tab.centered = true;`}
</pre>
      </Collapse>
    </div>
  );
};

const TextData = () => {
  return (
    <div style={{ padding: "20px" }}>
      <Title level={3}>Text Properties</Title>

      <Paragraph>
        <Text strong>You can use the below properties in the js editor.</Text>
      </Paragraph>

      <Collapse>
<pre>
{`properties.text.enable =true;
properties.text.link = "";
properties.text.text = "";
properties.text.text="<strong>hello</strong>"  //bold text
properties.text.text = "<em>hello</em>"  //emphasized text
properties.text.text = "<u>hello</u>"  //underline text
properties.text.text = "<s>hello</s>" //text with a strikethrough or a line through it .
properties.text.text = "<sub>hello</sub>" //subscript text
properties.text.text = "<sup>hello</sup>" //superscript text
properties.text.text = "<h1>hello</h1>"  //heading 1 (largest size)
properties.text.text = "<h2>hello</h2>"  //heading 2
properties.text.text = "<h3>hello</h3>"  //heading 3
properties.text.text = "<h4>hello</h4>"  //heading 4
properties.text.text = "<h5>hello</h5>"  //heading 5
properties.text.text = "<h6>hello</h6>"  //heading 6  (smallest size)
properties.text.text = "<p>hello</p>"  // normal heading size`}
</pre>
      </Collapse>
    </div>
  );
};

const ImageData = () => {
  return (
    <div style={{ padding: "20px" }}>
      <Title level={3}>Image Properties</Title>

      <Paragraph>
        <Text strong>You can use the below properties in the js editor.</Text>
      </Paragraph>

      <Collapse>
<pre>
{`properties.image.enable =true;
properties.image.imageRepeat = "initial";
properties.image.imageSize = "fill";
properties.image.opacity = 30;
properties.image.url="<Image URL>";`}
</pre>
      </Collapse>
    </div>
  );
};

const DropdownData = () => {
  return (
    <div style={{ padding: "20px" }}>
      <Title level={3}>Access Reprort with Dropdown</Title>

      <Paragraph>
        <Text strong> To access the report with js editor</Text>
      </Paragraph>

      <Collapse>
<pre>
{`if(value === "A") {
open_report("7229/7229_1.hr");
}

if(value === "B") {
open_report("1149_Views/000_Save_time_000/Report_Chart.hr");
} 
`}
</pre>
      </Collapse>

      <Paragraph>
        <Text>
          • If you need to open the report, just get the location from the
          tooltip and use it in the on-change event.
        </Text>
      </Paragraph>
      <Paragraph>
        <Text>• By default the report will open in the dashboard.</Text>
      </Paragraph>

      <Paragraph>
        <Text strong> If you want to open the report in tab component</Text>
      </Paragraph>

      <Collapse>
<pre>
{`//compId is the the tabId
//tabIdx is the index of the tab (if it is not present by default the report will be added in the first tab)

let opts = {compId: "item-G1qUA",tabIdx: 0};

if(value === "A") {
open_report("7229/7229_1.hr", opts);
}

if(value === "B") {
open_report("1149_Views/000_Save_time_000/Report_Chart.hr");
}
`}
        </pre>
      </Collapse>

      <Paragraph>
        <Text>
          • When configuring the options <Text strong>(opts)</Text>, make sure
          to include the component ID of the specific tab where you want to add
          the report. You can find this ID within the Tab component.
        </Text>
      </Paragraph>

      <Paragraph>
        <Text>
          • The variable <Text strong>tabIdx</Text> represents the index of the
          tab where the report should be added. If the user does not provide
          this variable then by default, it will be added to the first tab.
        </Text>
      </Paragraph>

      <Paragraph>
        <Text>
          • If you want to add the report in the second tab then you have to
          give <Text strong>"tabIdx : 1"</Text>, by default it will start from
          0.
        </Text>
      </Paragraph>

      <Paragraph>
        <Text strong> If you want to add multiple reports into different tabs</Text>
      </Paragraph>

      <Collapse>
<pre>
{`//compId is the the tabId
//tabIdx is the index of the tab (if it is not present by default the report will be added in the first tab)

let optionOne = {compId: "item-VOJqQ",tabIdx: 0};

if(value === "A") {
open_report("7229/7229_1.hr", optionOne);
}

let optionTwo = {compId: "item-VOJqQ",tabIdx: 1};

if(value === "B") {
open_report("1149_Views/000_Save_time_000/Report_Chart.hr",optionTwo);
}

let optionThree = {compId: "item-VOJqQ",tabIdx: 2};

if(value === "C") {
open_report("1149_Views/000_Save_time_000/Report_Chart.hr",optionThree);
}
`}
        </pre>
      </Collapse>

      <Paragraph>
        <Text>
          • Instead of <Text strong>opts</Text> you can declare it with any name.
        </Text>
      </Paragraph>
      
      <Paragraph>
        <Text>
          • Just make sure to include the <Text strong>component ID</Text> and the <Text strong>declared variable</Text> in the open report function.
        </Text>
      </Paragraph>
      
      <Paragraph>
        <Text strong> If you want to open the report in the custom position</Text>
      </Paragraph>

      <Collapse>
<pre>
{`const opts = {layout: {x: 2, y:3, h:4, w:5}}

if(value === "A") {
open_report("7229/7229_1.hr", opts);
}

if(value === "B") {
open_report("1149_Views/000_Save_time_000/Report_Chart.hr");
} 

`}
        </pre>
      </Collapse>

      <Paragraph>
        <Text>
          <Paragraph><Text strong>• x </Text> represents Grid-Left</Paragraph>
          <Paragraph><Text strong>• y </Text> represents Grid-Top</Paragraph>
          <Paragraph><Text strong>• h </Text> represents Grid-Height</Paragraph>
          <Paragraph><Text strong>• w </Text> represents Grid-Width</Paragraph>
        </Text>
      </Paragraph>

      



            
      <Paragraph>
        <Text strong> If you want to open the report in the custom position inside the tab</Text>
      </Paragraph>

      <Collapse>
<pre>
{`const opts = {compId: "item-1MS0G" , layout: {x: 2, y:3, h:4, w:1}}

if(value === "A") {
open_report("7229/7229_1.hr", opts);
}

if(value === "B") {
open_report("1149_Views/000_Save_time_000/Report_Chart.hr");
} 
`}
        </pre>
      </Collapse>

      <Paragraph>
        <Text strong>• compId </Text> represents the component id of tab
      </Paragraph>

      <Paragraph>
        <Text>• The <Text strong>default position</Text> we will get , if we do not provide the layout in <Text strong> opts</Text></Text>
      </Paragraph>
      
    </div>
  );
};

const DashboardTabData = () => {
  return (
    <div style={{ padding: "20px" }}>
      <Title level={3}>Tab Properties</Title>

      <Paragraph>
        <Text>
          {" "}
          You can use the below <Text strong>properties</Text> in the js editor
          within in the dashboard.
        </Text>
      </Paragraph>

      <Collapse>
<pre>
{`//item-G1qUA is the tabId

properties.tab["item-G1qUA"].enable =true;
properties.tab["<tab-id>"].numberOfTabs = 2;
properties.tab["<tab-id>"].tabNames = "Tab 1, Tab 2";
properties.tab["<tab-id>"].tabType = "card";
properties.tab["<tab-id>"].activeTab="Tab 1";
properties.tab["<tab-id>"].centered = true;`}
</pre>
      </Collapse>

      <Paragraph>
        <Text>
          • You can find the <Text strong>tab-id</Text> within the Tab
          component.
        </Text>
      </Paragraph>
    </div>
  );
};

const DashboardTextData = () => {
  return (
    <div style={{ padding: "20px" }}>
      <Title level={3}>Text Properties</Title>

      <Paragraph>
        <Text>
          {" "}
          You can use the below <Text strong>properties</Text> in the js editor
          within in the dashboard.
        </Text>
      </Paragraph>

      <Collapse>
<pre>
{`//item-zKDjL is the textId

properties.text["item-zKDjL"].enable =true;
properties.text["<text-id>"].link = "";
properties.text["<text-id>"].text = "";
properties.text["<text-id>"].text="<strong>hello</strong>"  //bold text
properties.text["<text-id>"].text = "<em>hello</em>"  //emphasized text
properties.text["<text-id>"].text = "<u>hello</u>"  //underline text
properties.text["<text-id>"].text = "<s>hello</s>" //text with a strikethrough or a line through it .
properties.text["<text-id>"].text = "<sub>hello</sub>" //subscript text
properties.text["<text-id>"].text = "<sup>hello</sup>" //superscript text
properties.text["<text-id>"].text = "<h1>hello</h1>"  //heading 1 (largest size)
properties.text["<text-id>"].text = "<h2>hello</h2>"  //heading 2
properties.text["<text-id>"].text = "<h3>hello</h3>"  //heading 3
properties.text["<text-id>"].text = "<h4>hello</h4>"  //heading 4
properties.text["<text-id>"].text = "<h5>hello</h5>"  //heading 5
properties.text["<text-id>"].text = "<h6>hello</h6>"  //heading 6  (smallest size)
properties.text["<text-id>"].text = "<p>hello</p>"  // normal heading size`}
</pre>
      </Collapse>

      <Paragraph>
        <Text>
          • You can find the <Text strong>text-id</Text> within the Text
          component.
        </Text>
      </Paragraph>
    </div>
  );
};

const DashboardImageData = () => {
  return (
    <div style={{ padding: "20px" }}>
      <Title level={3}>Image Properties</Title>

      <Paragraph>
        <Text>
          {" "}
          You can use the below <Text strong>properties</Text> in the js editor
          within in the dashboard.
        </Text>
      </Paragraph>

      <Collapse>
<pre>
{`//item-Vf9qy is the imageId

properties.image["item-Vf9qy"].enable =true;
properties.image["<image-id>"].imageRepeat = "initial";
properties.image["<image-id>"].imageSize = "fill";
properties.image["<image-id>"].opacity = 30;
properties.image["<image-id>"].url="<Image URL>";`}
</pre>
      </Collapse>

      <Paragraph>
        <Text>
          • You can find the <Text strong>image-id</Text> within the Image
          component.
        </Text>
      </Paragraph>
    </div>
  );
};

const JsDrawer = ({ compType, isDashboard }) => {
  const [visible, setVisible] = useState(false);

  const showDrawer = () => {
    setVisible(true);
  };
  const onClose = () => {
    setVisible(false);
  };

  let dashboardContent;

  if (isDashboard) {
    dashboardContent = (
      <div>
        <DashboardTabData />
        <DashboardTextData />
        <DashboardImageData />
      </div>
    );
  } else {
    dashboardContent = (
      <>
        {compType === "tab" && <TabData />}
        {compType === "text" && <TextData />}
        {compType === "image" && <ImageData />}
        {compType === "select-dropdown" && <DropdownData />}
      </>
    );
  }

  return (
    <>
      <Tooltip title="Please click here for information">
        <QuestionCircleOutlined
          style={{ marginLeft: "5px", fontSize: "10px" }}
          onClick={showDrawer}
        />
      </Tooltip>

      <Drawer
        title={"JS Editor"}
        placement="right"
        onClose={onClose}
        visible={visible}
        width={"60%"}
      >
        {dashboardContent}
      </Drawer>
    </>
  );
};

export default JsDrawer;
