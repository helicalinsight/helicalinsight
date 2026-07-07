import { useEffect } from "react";
import { Row, Col, Typography, Button, Skeleton, Tooltip, List } from "antd";
import { useSelector, useDispatch } from "react-redux";
import {
  updateIsFetched,
  toggleMetadataContents,
  updateEntityId,
} from "../../../../../redux/actions/admin.actions";
import "../index.scss";
// import { List, AutoSizer } from "react-virtualized";
import { fetchMetadataAdministration } from "../utils/fetch-metadata-administration-api";

const { Title, Text } = Typography;

const HIMetaDataAdministration = () => {
  const isFetched = useSelector(
    (state) => state.admin.isFetched.metadataAdministration
  );
  const metadataAdministrationData = useSelector(
    (state) => state.admin.metadataAdministrationData
  );

  const defaultLocalServiceProviders =
    isFetched &&
    metadataAdministrationData?.defaultLocalServiceProviders?.filter(
      (item) => item !== "-"
    );
  const serviceProviders =
    isFetched &&
    metadataAdministrationData?.serviceProviders?.filter(
      (item) => item !== "-"
    );
  const identityProviders =
    isFetched &&
    metadataAdministrationData?.identityProviders?.filter(
      (item) => item !== "-"
    );
  const metadataProviders =
    isFetched &&
    metadataAdministrationData?.metadataProviders?.filter(
      (item) => item !== "-"
    );

  const dispatch = useDispatch();
  useEffect(() => {
    fetchMetadataAdministration(false, isFetched, dispatch);
  }, []);

  // Made these changes as virtual list is not needed right now
  // To implement Cell Measurer if virtual list is implemented - to do

  return (
    <Skeleton active loading={!isFetched} paragraph={{ rows: 13 }}>
      <Row justify="center">
        <Col>
          <Title level={4}>Metadata Administration</Title>
        </Col>
        <Col className="hi-height-70vh" span={24}>
          <Row gutter={[8, 8]} className="hi-height-100 hi-overflow">
            <Col sm={24} lg={12} className="hi-height-100">
              <Row align="top" className="hi-height-100">
                <Col className="hi-height-30" span={24}>
                  <Text strong>Default local service provider</Text>
                  {/* <div className="hi-height-100 padding-left-15">
                  <AutoSizer>
                    {({ height, width }) => (
                      <List
                        width={width}
                        height={height}
                        rowCount={
                          defaultLocalServiceProviders?.length
                            ? defaultLocalServiceProviders?.length
                            : 0
                        }
                        rowHeight={20}
                        rowRenderer={defaultLocalServiceProvidersRowRenderer}
                      />
                    )}
                  </AutoSizer>
                </div> */}
                  <List
                    dataSource={defaultLocalServiceProviders}
                    renderItem={(item) => (
                      <List.Item>
                        <Text
                          className="hi-metadata-administration-link"
                          onClick={() => {
                            dispatch(
                              toggleMetadataContents({
                                page: "metadataPreview",
                                status: true,
                              })
                            );
                            dispatch(updateEntityId(item));
                          }}
                        >
                          {item}
                        </Text>
                      </List.Item>
                    )}
                  />
                </Col>
                <Col className="hi-height-30" span={24}>
                  <Text strong>Service providers</Text>
                  {/* <div className="hi-height-100 padding-left-15">
                  <AutoSizer>
                    {({ height, width }) => (
                      <List
                        width={width}
                        height={height}
                        rowCount={
                          serviceProviders?.length
                            ? serviceProviders?.length
                            : 0
                        }
                        rowHeight={20}
                        rowRenderer={serviceProvidersRowRenderer}
                      />
                    )}
                  </AutoSizer>
                </div> */}
                  <List
                    dataSource={serviceProviders}
                    renderItem={(item) => (
                      <List.Item>
                        <Text
                          className="hi-metadata-administration-link"
                          onClick={() => {
                            dispatch(
                              toggleMetadataContents({
                                page: "metadataPreview",
                                status: true,
                              })
                            );
                            dispatch(updateEntityId(item));
                          }}
                        >
                          {item}
                        </Text>
                      </List.Item>
                    )}
                  />
                </Col>
                <Col className="hi-height-30" span={24}>
                  <Text strong>Identity providers</Text>
                  {/* <div className="hi-height-100  padding-left-15">
                  <AutoSizer>
                    {({ height, width }) => (
                      <List
                        width={width}
                        height={height}
                        rowCount={
                          identityProviders?.length
                            ? identityProviders?.length
                            : 0
                        }
                        rowHeight={20}
                        rowRenderer={identityProvidersRowRenderer}
                      />
                    )}
                  </AutoSizer>
                </div> */}
                  <List
                    dataSource={identityProviders}
                    renderItem={(item) => (
                      <List.Item>
                        <Text
                          className="hi-metadata-administration-link"
                          onClick={() => {
                            dispatch(
                              toggleMetadataContents({
                                page: "metadataPreview",
                                status: true,
                              })
                            );
                            dispatch(updateEntityId(item));
                          }}
                        >
                          {item}
                        </Text>
                      </List.Item>
                    )}
                  />
                </Col>
              </Row>
            </Col>
            <Col sm={24} md={12}>
              <Text strong>Metadata providers</Text>
              {/* <div className="hi-height-90 padding-left-15">
              <AutoSizer>
                {({ height, width }) => (
                  <List
                    width={width}
                    height={height}
                    rowCount={
                      metadataProviders?.length ? metadataProviders?.length : 0
                    }
                    rowHeight={20}
                    rowRenderer={metadataProvidersRowRenderer}
                  />
                )}
              </AutoSizer>
            </div> */}
              <List
                pagination={{ pageSize: 15 }}
                dataSource={metadataProviders}
                renderItem={(item) => (
                  <List.Item>
                    <Text>{item}</Text>
                  </List.Item>
                )}
              />
            </Col>
          </Row>
        </Col>
        <Col span={24}>
          <Row justify="end" gutter={[16, 16]}>
            <Col>
              <Button
                onClick={() => {
                  dispatch(
                    updateIsFetched({
                      type: "metadataAdministration",
                      value: false,
                    })
                  );
                  fetchMetadataAdministration(true, isFetched, dispatch);
                }}
              >
                Refresh metadata
              </Button>
            </Col>
            <Col>
              <Tooltip title="Generate new service provider metadata">
                <Button
                  onClick={() => {
                    dispatch(
                      toggleMetadataContents({
                        page: "metadataGeneration",
                        status: true,
                      })
                    );
                  }}
                >
                  Generate metadata
                </Button>
              </Tooltip>
            </Col>
          </Row>
        </Col>
      </Row>
    </Skeleton>
  );
};

export { HIMetaDataAdministration };

// function defaultLocalServiceProvidersRowRenderer({
//   key, // Unique key within array of rows
//   index, // Index of row within collection
//   isScrolling, // The List is currently being scrolled
//   isVisible, // This row is visible within the List (eg it is not an overscanned row)
//   style, // Style object to be applied to row (to position it)
// }) {
//   return (
//     <div key={key} style={style}>
//       <Text
//         className="hi-metadata-administration-link"
//         // ellipses="true"
//         onClick={() => {
//           dispatch(
//             toggleMetadataContents({
//               page: "metadataPreview",
//               status: true,
//             })
//           );
//           dispatch(updateEntityId(defaultLocalServiceProviders[index]));
//         }}
//       >
//         {defaultLocalServiceProviders[index] !== "-" &&
//           defaultLocalServiceProviders[index]}
//       </Text>
//     </div>
//   );
// }
// function serviceProvidersRowRenderer({
//   key, // Unique key within array of rows
//   index, // Index of row within collection
//   isScrolling, // The List is currently being scrolled
//   isVisible, // This row is visible within the List (eg it is not an overscanned row)
//   style, // Style object to be applied to row (to position it)
// }) {
//   return (
//     <Text
//       key={key}
//       style={style}
//       ellipses="true"
//       className="hi-metadata-administration-link"
//       onClick={() => {
//         dispatch(
//           toggleMetadataContents({
//             page: "metadataPreview",
//             status: true,
//           })
//         );
//         dispatch(updateEntityId(serviceProviders[index]));
//       }}
//     >
//       {serviceProviders[index] !== "-" && serviceProviders[index]}
//       <br />
//     </Text>
//   );
// }
// function identityProvidersRowRenderer({
//   key, // Unique key within array of rows
//   index, // Index of row within collection
//   isScrolling, // The List is currently being scrolled
//   isVisible, // This row is visible within the List (eg it is not an overscanned row)
//   style, // Style object to be applied to row (to position it)
// }) {
//   return (
//     <Text
//       key={key}
//       className="hi-metadata-administration-link"
//       // ellipses="true"
//       onClick={() => {
//         dispatch(
//           toggleMetadataContents({
//             page: "metadataPreview",
//             status: true,
//           })
//         );
//         dispatch(updateEntityId(identityProviders[index]));
//       }}
//     >
//       {identityProviders[index] !== "-" && identityProviders[index]}
//       <br />
//     </Text>
//   );
// }
// function metadataProvidersRowRenderer({
//   key, // Unique key within array of rows
//   index, // Index of row within collection
//   isScrolling, // The List is currently being scrolled
//   isVisible, // This row is visible within the List (eg it is not an overscanned row)
//   style, // Style object to be applied to row (to position it)
// }) {
//   return (
//     <Text
//       key={key}
//       style={style}
//       // className="hi-metadata-administration-link"
//       onClick={() => {
//         // dispatch(
//         //   toggleMetadataContents({
//         //     page: "metadataPreview",
//         //     status: true,
//         //   })
//         // );
//         // dispatch(updateEntityId(metadataProviders[index]));
//       }}
//     >
//       {metadataProviders[index] !== "-" && metadataProviders[index]}
//     </Text>
//   );
// }
