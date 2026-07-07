import React, { useRef } from "react";
import { HelicalReports } from "../../../../pages";
import { derivedFormDataConvertorToReportProps } from "../../utils/instant-bi-utilities";
import HIInstantBIReportToolbar from "../hi-instant-bi-report-toolbar/hi-instant-bi-report-toolbar";
import "./hi-report-component.scss";

const renderCheckProps = (prevProps, nextProps) => {
  if (prevProps.searchButtonClicked !== nextProps.searchButtonClicked) {
    return false;
  } else {
    return true;
  }
};

const HIReportComponent = React.memo((props) => {
  const { mode, derivedFormdata, metadata, searchButtonClicked } = props;
  const isOpenMode = mode === "open";
  const isEditMode = mode === "edit";
  let reportProps = derivedFormDataConvertorToReportProps({
    derivedFormData: derivedFormdata,
    metadata: metadata,
  });
  const searchButtonClickedRef = useRef(null);

  // const metadata = {
  //   // "classifier": "db.generic",
  //   // "name": "HIUSER",
  //   // "dataSource": {
  //   //     "sync": false,
  //   //     "id": "1",
  //   //     "catSchemaPredicted": false,
  //   //     "catalog": "",
  //   //     "schema": "HIUSER",
  //   //     "type": "dynamicDataSource",
  //   //     "baseType": "global.jdbc"
  //   // },
  //   // "uniqueId": `Metadata_1.metadata`,
  //   // "tables": {
  //   //     "travel_details": {
  //   //         "id": "8a28627d07d04ef096d9935f12e0c7e9",
  //   //         "alias": "travel_details",
  //   //         "columns": {
  //   //             "booking_platform": {
  //   //                 "alias": "booking_platform",
  //   //                 "fullyQualifiedColumn": "travel_details.booking_platform",
  //   //                 "columnId": "fd2485db-11d7-431b-ae57-673960e5cd7f",
  //   //                 "defaultFunction": "db.generic.groupBy.group",
  //   //                 "type": {
  //   //                     "java.lang.String": "text"
  //   //                 }
  //   //             }
  //   //         },
  //   //         "name": "travel_details"
  //   //     }
  //   // },
  //   // "sets": [
  //   //     [
  //   //         "travel_details",
  //   //     ]
  //   // ],
  //   metadataFileName: `Metadata_1.metadata`,
  //   location: "naresh",
  // };
  // const reportProps = {
  //   mode: "instant-bi",
  //   metadata,
  //   columns: [],
  //   rows: [{ table: "travel_details", column: "booking_platform" }],
  //   // filters:[{table:"travel_details",column:"booking_platform",values:"Agent"}] ,
  //   // marks: [
  //   //   {
  //   //     table: "travel_details",
  //   //     column: "booking_platform",
  //   //     markType: "color",
  //   //   },
  //   // ],
  //   visualisationType: "Table",
  // };

  let report = (
    <div className="hi-instant-bi-report-component">
      {!isOpenMode && <HIInstantBIReportToolbar />}
      <HelicalReports {...reportProps} />
    </div>
  );
  console.log(
    searchButtonClickedRef.current,
    "searchButtonClickedRef.current",
    searchButtonClicked,
    "searchButtonClicked",
    derivedFormdata,
    "derivedFormdata"
  );
  const renderCheck =
    (searchButtonClickedRef.current !== searchButtonClicked &&
      reportProps.metadata.metadataFileName &&
      derivedFormdata) ||
    isEditMode ||
    isOpenMode;
  if (renderCheck) {
    searchButtonClickedRef.current = searchButtonClicked;
    return report;
  }
  return null;
}, renderCheckProps);

export default HIReportComponent;
