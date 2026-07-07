import { Input } from "antd";
import { useDispatch, useSelector } from "react-redux";
import {
  loadDerivedFormData,
  updateSearchValue,
} from "../../../../redux/actions/instant-bi.actions";
import { generateReportBasedOnQueryStringAPI } from "../../utils/instant-bi-requests";
import Dictaphone from "./hi-speech-recognition";
import "./hi-search-component.scss";
import notify from "../../../hi-notifications/notify";

const { Search } = Input;

const suffix = (
  // <AudioOutlined
  //   // style={{
  //   //   fontSize: 16,
  //   //   color: '#1890ff',
  //   // }}
  //   className="hi-audio-icon"
  // >
  <Dictaphone />
);

const HISearchComponent = (props) => {
  const searchValue = useSelector((state) => state.instantBI.searchValue);
  const metadata = useSelector((state) => state.instantBI.metadata);
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const mode = useSelector((state) => state.instantBI.mode);
  const isOpenMode = mode === "open";

  const onSearch = (value) => {
    if (metadata?.location && metadata?.metadataFileName) {
      generateReportBasedOnQueryStringAPI({
        dispatch,
        formData: {
          nlpString: value,
          dir: metadata.location,
          file: metadata.metadataFileName,
        },
        successCB: (res) => {
          dispatch(
            loadDerivedFormData({
              ...res,
              searchButtonClicked: new Date().toTimeString().toString(),
            })
          );
        },
        errorCB: (e) => {},
      });
    } else {
      Notify.warning({ type: "Frontend", message: "Please connect metadata" });
    }
  };
  return (
    <div className="hi-instant-bi-search-component">
      <Search
        disabled={isOpenMode}
        data-testid="hi-instant-bi-search"
        placeholder="Ask your query"
        className="hi-instant-bi-search"
        allowClear
        value={searchValue}
        onChange={(e) => {
          dispatch(updateSearchValue(e.target.value));
        }}
        size="large"
        onSearch={onSearch}
        suffix={isOpenMode ? null : suffix}
      />
    </div>
  );
};
export default HISearchComponent;
