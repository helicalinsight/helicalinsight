import React, { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  Form,
  Input,
  Checkbox,
  Row,
  Tooltip,
  Select,
  DatePicker,
  Radio,
  InputNumber,
} from "antd";
import moment from "moment";
import mtz from "moment-timezone";
import requests from "../../../base/requests";
import notify from "../../hi-notifications/notify";
import { InfoCircleOutlined } from "@ant-design/icons";
import { fileBrowserActions } from "../../../redux/actions";
import "../hi-user-module.scss";
const { Option } = Select;
const repeatOptions = [
  { title: "Daily", value: "daily", text: "day", max: 31 },
  { title: "Weekly", value: "weekly", text: "week", max: 4 },
  { title: "Monthly", value: "monthly", text: "month", max: 12 },
  { title: "Yearly", value: "yearly", text: "year", max: 31 },
];
const days = [
  { value: "Sunday", title: "S", id: 1 },
  { value: "Monday", title: "M", id: 2 },
  { value: "Tuesday", title: "T", id: 3 },
  { value: "Wednesday", title: "W", id: 4 },
  { value: "Thursday", title: "T", id: 5 },
  { value: "Friday", title: "F", id: 6 },
  { value: "Saturday", title: "S", id: 7 },
];

const repeateByMonthOptions = [
  { value: "dayOfTheMonth", title: "day of the month" },
  { value: "dayOfTheWeek", title: "day of the week" },
];
const timezones = mtz.tz.names();
const radioOptions = [
  { value: "never", title: "Never" },
  { value: "after", title: "After" },
  { value: "on", title: "On" },
];
const dateFormat = "DD/MM/YYYY hh:mm A";

const getOrdinalSuffix = (num) => {
  switch (num % 10) {
      case 1: return 'First';
      case 2: return 'Second';
      case 3: return 'Third';
      case 4: return 'Fourth';
      case 5: return 'Fifth';
      default: return '';
  }
};

const getNthWeekday = (date) => {
  const nthWeekday = Math.floor((date.date() - 1) / 7) + 1;
  const ordinalSuffix = getOrdinalSuffix(nthWeekday);
  const weekdayName = date.format('dddd');

  return `${ordinalSuffix} ${weekdayName}`;
};

const SchedulingOptions = (props) => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const { form, submitClicked, setSubmitClicked, buildScheduleFormData, onClose, apiRef } = props;
  const getRepeatsArray = (num) => new Array(num).fill().map((_, i) => i + 1);
  const [summaryText, setSummaryText] = useState("");
  const { ends, startsOn } = form.getFieldsValue();
  const scheduleDrawerVisible = useSelector(
    (state) => state.app.viewerScheduleModalVisible
  );
  const applicationSettingsData = useSelector(
    (state) => state.app.applicationSettingsData
  );

  useEffect(() => {
    if (submitClicked) {
      form
        .validateFields()
        .then(() => {
          if (scheduleDrawerVisible) form.submit();
        })
        .catch((e) => setSubmitClicked(false));
    }
  }, [submitClicked]);

  useEffect(() => {
    form.setFieldsValue({ endsOn: moment(startsOn).add(1, "minutes") });
  }, [startsOn]);

  // //setting initial form values
  // useEffect(() => {
  //   if (form) {
  //     form.setFieldsValue({
  //       repeats: "weekly",
  //       repeatOn: [],
  //       repeatsEvery: 1,
  //       timezone:
  //         mtz.tz.guess() + ` (GMT ${mtz.tz(mtz.tz.guess()).format("Z")})`,
  //       startsOn: moment(new Date(), dateFormat).add(15, "minutes"),
  //       repeatBy: repeateByMonthOptions[0].value,
  //       ends: radioOptions[0].value,
  //       endsAfter: 35,
  //       endsOn: moment(new Date(), dateFormat),
  //     });
  //   }
  // }, []);

  useEffect(() => {
    updateSummary();
  }, [form]);

  const updateSummary = () => {
    if (form) {
      let summary = "Starting ";
      const {
        repeats,
        repeatsEvery,
        startsOn,
        timezone,
        repeatOn,
        repeatBy,
        ends,
        endsAfter,
        endsOn,
      } = form.getFieldsValue();
      const momentDay = moment(startsOn).calendar();

      //adding 'today', 'tomorrow' if the date is today or tomorrow,if not adding date to summary
      if (momentDay.split(" ")[0] === "Today") {
        summary += "today, ";
      } else if (momentDay.split(" ")[0] == "Tomorrow") {
        summary += "tomorrow, ";
      } else {
        summary += `on ${moment(startsOn).format("Do MMMM, YYYY")}, `;
      }

      // adding logic based on repeats every and repeats itself
      if (repeatsEvery === 1) {
        switch (repeats) {
          case "weekly":
            summary += `repeats every 7 days `;
            break;
          case "daily":
            summary += `repeats daily `;
            break;
          case "monthly":
            summary += `repeats monthly `;
            break;
          case "yearly":
            summary += `repeats yearly `;
            break;
        }
      }

      if (repeatsEvery > 1) {
        switch (repeats) {
          case "weekly":
            summary += `repeats every ${repeatsEvery * 7} days `;
            break;
          case "daily":
            summary += `repeats every ${repeatsEvery} day(s) `;
            break;
          case "monthly":
            summary += `repeats every ${repeatsEvery} month(s) `;
            break;
          case "yearly":
            summary += `repeats every ${repeatsEvery} year(s) `;
            break;
        }
      }

      // adding day names if weekly is selected and repeats every for > 1
      if (repeatsEvery === 1 && repeats === "weekly" && repeatOn) {
        summary += `${days
          .filter((e) => repeatOn.includes(e.value))
          .reduce((acc, val, i) => {
            if (i === 0) return "on "+ val.value;
            return acc + ", " + val.value;
          }, "")} `;
      }

      // adding starting time
      if(repeatBy === "dayOfTheWeek" && repeats === "monthly"){
        summary += `on ${getNthWeekday(moment(startsOn))} at ${moment(startsOn).format(  "hh:mm A")}`;
      }else{
        summary += `at ${moment(startsOn).format("Do MMMM, YYYY  hh:mm A")}`;
      }

      // adding ends on if end time is selected
      if (ends === "on") {
        summary += `, until ${moment(endsOn).format("Do MMMM, YYYY  hh:mm A")} `;
      }

      // adding occurences, if any
      if (ends === "after") {
        summary += ` for ${endsAfter} times `;
      }

      // finally adding timezone
      summary += " (" + timezone + ")";

      // form.setFieldsValue({ summaryText: summary });
      setSummaryText(summary);
    }
  };

  const repeateOptionObj = repeatOptions.find(
    (e) => e.value === form.getFieldValue("repeats")
  );
  const repeatsEveryArray = getRepeatsArray(
    repeateOptionObj ? repeateOptionObj.max : 100
  );

  const disabledDate = (current) => {
    // Can not select days before today
    return moment().add(-1, "days") >= current;
  };

  const onSendMail = (formValues) => {
    const sObj = buildScheduleFormData(formValues)
    apiRef.current = requests
      .usermodule(dispatch, applicationSettingsData.settings)
      .postScheduleReport(
        sObj,
        (res) => {
          apiRef.current = null;
          if (res?.data) {
            dispatch(fileBrowserActions.saveFileinFb(res.data));
          }
          // Notify.success({
          //   type: "Network call",
          //   ...res,
          // });
          onClose()
          setSubmitClicked(false);
        },
        (e) => {
          apiRef.current = null;
          // Notify.error({
          //   type: "Network call",
          //   ...e,
          // });
          onClose()
          setSubmitClicked(false);
        }
      );
  };

  const onFinish = (values) => onSendMail(values);

  const layout = {
    labelCol: {
      span: 4,
    }
  };

  return (
    <Form
      {...layout}
      form={form}
      onValuesChange={updateSummary}
      onFinish={onFinish}
      name="control-hooks"
      labelAlign="left"
      initialValues={{
        repeats: "weekly",
        repeatOn: [],
        repeatsEvery: 1,
        timezone:
          mtz.tz.guess() + ` (GMT ${mtz.tz(mtz.tz.guess()).format("Z")})`,
        startsOn: moment(new Date(), dateFormat).add(15, "minutes"),
        repeatBy: repeateByMonthOptions[0].value,
        ends: radioOptions[0].value,
        endsAfter: 35,
        endsOn: moment(new Date(), dateFormat),
      }}
    >
      <Form.Item label="Repeats" name="repeats">
        <Select
          style={{
            width: 120,
          }}
        >
          {repeatOptions.map((option) => (
            <Option value={option.value}>{option.title}</Option>
          ))}
        </Select>
      </Form.Item>
      <Form.Item label="Repeats every">
        <Form.Item name="repeatsEvery" noStyle>
          <Select style={{ width: 120 }}>
            {repeatsEveryArray.map((num) => (
              <Option value={num}>{num}</Option>
            ))}
          </Select>
        </Form.Item>
        <span style={{ fontSize: 15, marginLeft: 8 }}>
          {repeateOptionObj
            ? repeateOptionObj.text +
            `(s) ${repeateOptionObj.text === "week"
              ? "(" + form.getFieldValue("repeatsEvery") + " * 7 days)"
              : ""
            }`
            : ""}
        </span>
      </Form.Item>
      <Form.Item
        label="Repeat On"
        name="repeatOn"
        hidden={
          form.getFieldValue("repeats") !== "weekly" ||
          (form.getFieldValue("repeats") === "weekly" &&
            form.getFieldValue("repeatsEvery") > 1)
        }
      >
        <Checkbox.Group>
          <Row>
            {days.map((day) => (
              <Checkbox key={day.value} value={day.value}>
                {day.title}
              </Checkbox>
            ))}
          </Row>
        </Checkbox.Group>
      </Form.Item>
      <Form.Item
        label="Repeat By"
        name="repeatBy"
        hidden={form.getFieldValue("repeats") !== "monthly"}
      >
        <Radio.Group>
          {repeateByMonthOptions.map((option) => (
            <Radio value={option.value}>{option.title}</Radio>
          ))}
        </Radio.Group>
      </Form.Item>
      <Form.Item label="Timezone" name="timezone">
        <Select showSearch>
          {timezones.map((time) => (
            <Option value={time + " " + `(GMT ${mtz.tz(time).format("Z")})`}>
              {time + " " + `(GMT ${mtz.tz(time).format("Z")})`}
            </Option>
          ))}
        </Select>
      </Form.Item>
      <Form.Item
        label="Starts On"
        name="startsOn"
        rules={[
          {
            validator: (_, value) => {
              const minuteDiff = moment(value).diff(
                moment(new Date()),
                "minutes"
              );
              if (minuteDiff < 0)
                return Promise.reject(
                  "Start date cannot be before current date!"
                );
              return Promise.resolve();
            },
          },
        ]}
      >
        <DatePicker
          allowClear={false}
          showTime
          format={dateFormat}
          disabledDate={disabledDate}
        />
      </Form.Item>
      <Form.Item
        label="Ends"
        name="ends"
        style={{ marginBottom: ends === "never" ? 24 : 10 }}
      >
        <Radio.Group>
          {radioOptions.map((option) => (
            <Radio value={option.value}>{option.title}</Radio>
          ))}
        </Radio.Group>
      </Form.Item>
      <Row
        align="middle"
        style={{
          marginBottom:
            form.getFieldValue("ends") && form.getFieldValue("ends") === "after"
              ? 24
              : 0,
        }}
      >
        <Form.Item
          style={{ margin: 0 }}
          name="endsAfter"
          hidden={
            !(
              form.getFieldValue("ends") &&
              form.getFieldValue("ends") === "after"
            )
          }
        >
          <InputNumber className="schedule-ends-options" min={1} max={99} />
        </Form.Item>
        {form.getFieldValue("ends") &&
          form.getFieldValue("ends") === "after" && (
            <div style={{ marginLeft: 8 }}>Occurrences</div>
          )}
      </Row>
      <Form.Item
        name="endsOn"
        className="schedule-ends-options"
        hidden={
          !(form.getFieldValue("ends") && form.getFieldValue("ends") === "on")
        }
        rules={[
          {
            validator: (_, value) => {
              const minuteDiff = moment(value).diff(
                form.getFieldValue("startsOn"),
                "minutes"
              );
              if (minuteDiff < 1)
                return Promise.reject("End date cannot be before start date!");
              return Promise.resolve();
            },
          },
        ]}
      >
        <DatePicker
          // className="schedule-ends-options"
          allowClear={false}
          showTime
          format={dateFormat}
          disabledDate={disabledDate}
        />
      </Form.Item>
      <Form.Item
        label={[
          "Summary",
          <Tooltip title="Resets at 1st of every month and January 1st of every year.">
            <InfoCircleOutlined
              style={{ marginLeft: 5, fontSize: 11, cursor: "pointer" }}
            />
          </Tooltip>,
        ]}
        labelWrap={true}
        fontSize={20}
      >
        {summaryText}
      </Form.Item>
    </Form>
  );
};

export { SchedulingOptions };