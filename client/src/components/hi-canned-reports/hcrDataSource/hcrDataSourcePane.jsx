import {
    DeleteOutlined,
    LinkOutlined,
    PlayCircleOutlined,
    PlusOutlined,
    SettingOutlined,
} from '@ant-design/icons';
import {
    Button,
    Card,
    Checkbox,
    Col,
    Collapse,
    Dropdown,
    Input,
    Menu,
    Popover,
    Row,
    Select,
    Space,
    Table,
    Typography,
} from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import {
    dsConnTabData,
    getDsConnTypesCols,
    getTagContent,
} from '../hcrHelperMethods';
import { hcrActions } from '../../../redux/actions';
import requests from '../../../base/requests';
import { hcrDSParameter, hcrDSQuery } from '../hcr-constants';

const { Paragraph } = Typography;

export default function DataSourcePanes({ tabNum, contentTab }) {
    const activeTab =
        useSelector((state) =>
            state.cannedReports.present.hcrTabData.panes.find(
                (pane) =>
                    pane.key ===
                    state.cannedReports.present.hcrTabData.activeKey
            )
        ) || {};
    const { dsPaneTypes } = activeTab;
    const allTypes = useSelector(
        (state) => state.cannedReports.present.hCROldConfigurations.allTypes
    );
    const { connTypes, sqlTypes } = allTypes;
    const parameters = useSelector(
        (state) =>
            state.cannedReports.present.hCROldConfigurations?.HCR?.HCR
                ?.designerProperties?.parameters
    );
    const dispatch = useDispatch();
    const { saveQueryReportState, saveExecuteReportQuery } =
        requests.cannedReport(dispatch);

    const handleChange = (dataSourcePane, itemId, value, setForSql) => {
        let reqKey;
        if (setForSql) {
            reqKey = 'sqlType';
        } else {
            if (dataSourcePane === 'CONNECTIONS') {
                reqKey = 'activeDs';
            } else if (dataSourcePane === hcrDSParameter) {
                reqKey = 'type';
            }
        }
        dispatch(
            hcrActions.handleEditingDsPaneItem({
                dataSourcePane,
                itemId,
                key: reqKey,
                value,
                setForSql,
            })
        );
    };

    const getExistedDsPanes = ({
        record,
        itemId,
        dsPaneToCompare,
        setForSql,
    }) => {
        let reqKey;
        if (dsPaneToCompare === 'CONNECTIONS') {
            reqKey = 'selectedConnection';
        } else if (dsPaneToCompare === hcrDSParameter) {
            reqKey = 'selectedParameter';
        }
        return (
            <Menu selectedKeys={[record.selectedConnection]} selectable={true}>
                {dsPaneTypes
                    .find(
                        (connType) =>
                            connType.dataSourcePane === dsPaneToCompare
                    )
                    ?.menu?.map((conn) => {
                        return (
                            <Menu.Item
                                onClick={() => {
                                    dispatch(
                                        hcrActions.handleEditingDsPaneItem({
                                            dataSourcePane:
                                                record.dataSourcePane,
                                            itemId,
                                            key: reqKey,
                                            value: conn.id,
                                            setForSql,
                                        })
                                    );
                                }}
                                key={conn.name}
                                value={conn.name}
                            >
                                {dsPaneToCompare === hcrDSParameter ? (
                                    <Collapse
                                        defaultActiveKey={['1']}
                                        onChange={() => {}}
                                    >
                                        <Collapse.Panel
                                            header={
                                                <Checkbox onChange={() => {}}>
                                                    {conn.name}
                                                </Checkbox>
                                            }
                                            key="1"
                                        >
                                            <span>{conn.name}</span>
                                        </Collapse.Panel>
                                    </Collapse>
                                ) : (
                                    <span>{conn.name}</span>
                                )}
                            </Menu.Item>
                        );
                    })}
            </Menu>
        );
    };

    const handleRunQuery = (item) => {
        const reqConn = dsPaneTypes
            .find((connType) => connType.dataSourcePane === 'CONNECTIONS')
            ?.menu?.find((conn) => conn.id === item.selectedConnection);
        let reqActDs = reqConn?.dataSource?.find(
            (ele) => ele.dataSourceName === reqConn.activeDs
        );
        const connDetails = {};
        if (reqActDs) {
            const inputString = reqActDs?.config;
            if (reqConn.activeDs === 'Managed DataSource') {
                connDetails.globalId = getTagContent({
                    startTag: '<globalId>',
                    inputString,
                    endTag: '</globalId>',
                });
            } else if (
                reqConn.activeDs === 'Plain Jdbc DataSource' ||
                reqConn.activeDs === 'Groovy Plain Jdbc DataSource'
            ) {
                connDetails.driver = getTagContent({
                    startTag: '<driver>',
                    inputString,
                    endTag: '</driver>',
                });
                connDetails.url = getTagContent({
                    startTag: '<url>',
                    inputString,
                    endTag: '</url>',
                });
                connDetails.user = getTagContent({
                    startTag: '<user>',
                    inputString,
                    endTag: '</user>',
                });
                connDetails.pass = getTagContent({
                    startTag: '<pass>',
                    inputString,
                    endTag: '</pass>',
                });
                if (reqConn.activeDs === 'Groovy Plain Jdbc DataSource') {
                    connDetails.condition = getTagContent({
                        startTag: '<condition>',
                        inputString,
                        endTag: '</condition>',
                    });
                }
            } else if (reqConn.activeDs === 'Adhoc DataSource') {
                connDetails.location = getTagContent({
                    startTag: '<location>',
                    inputString,
                    endTag: '</location>',
                });
                connDetails.metadataFileName = getTagContent({
                    startTag: '<metadataFileName>',
                    inputString,
                    endTag: '</metadataFileName>',
                });
            }
        }
        const formData = {
            name: '_temp_filename',
            efwd: {
                dataSources: {
                    connections: [
                        {
                            connection: {
                                id: reqConn?.id,
                                type: reqActDs?.dataSourceType,
                                connDetails: {
                                    ...connDetails,
                                },
                            },
                        },
                    ],
                },
                dataMaps: [
                    {
                        dataMap: {
                            name: item.name,
                            id: item.id,
                            connection: item.selectedConnection,
                            type: 'sql',
                            query: item.config,
                            parameters: [],
                        },
                    },
                ],
            },
        };
        if (item.temp_uuid) {
            formData.uuid = item.temp_uuid;
        }
        saveQueryReportState(
            formData,
            (res) => {
                dispatch(
                    hcrActions.handleEditingDsPaneItem({
                        dataSourcePane: hcrDSQuery,
                        itemId: item.id,
                        value: res.temp_uuid,
                        key: 'temp_uuid',
                    })
                );
                const executeQueryFormData = {
                    mapJson: {
                        map_id: 1,
                        type: reqActDs?.dataSourceType,
                        temp_uuid: res.temp_uuid,
                    },
                };
                saveExecuteReportQuery(
                    executeQueryFormData,
                    (exeQryRes) => {
                        dispatch(
                            hcrActions.handleEditingDsPaneItem({
                                dataSourcePane: hcrDSQuery,
                                itemId: item.id,
                                value: exeQryRes,
                                key: 'executeQueryData',
                            })
                        );
                    },
                    (exeQryErr) => {
                        console.log(exeQryErr);
                    }
                );
            },
            (err) => {
                console.log(err);
            }
        );
    };

    const parameterMenu = ({ dsPaneToCompare, record, itemId }) => {
        const reqKey = 'selectedParameter';
        return (
            <Menu onClick={() => {}}>
                {dsPaneTypes
                    .find(
                        (connType) =>
                            connType.dataSourcePane === dsPaneToCompare
                    )
                    ?.menu?.map((conn) => {
                        return (
                            conn.id !== itemId && (
                                <div
                                    style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                    }}
                                >
                                    <Popover
                                        // className="parameter-popover"
                                        overlayClassName="parameter-popover"
                                        // visible={true}
                                        content={
                                            <div>
                                                Quotes{' '}
                                                <Input placeholder="Open" />{' '}
                                                <Input placeholder="Close" />
                                            </div>
                                        }
                                        trigger="click"
                                        placement="bottomLeft"
                                    >
                                        <Checkbox
                                            value={conn.name}
                                            onClick={(e) => {
                                                e.stopPropagation();
                                            }}
                                        />
                                        <Button type="text">{conn.name}</Button>
                                    </Popover>
                                </div>
                            )
                        );
                        //         dispatch(hcrActions.handleEditingDsPaneItem({ dataSourcePane: record.dataSourcePane, itemId, key: reqKey, value: conn.id, setForSql }));
                    })}
            </Menu>
        );
    };

    return (
        <Table
            columns={getDsConnTypesCols({ dispatch })}
            // showHeader= {false}
            // bordered
            size="small"
            rowKey={(record) => record.key}
            pagination={false}
            className="validated-table"
            // expandedRowKeys={expandedKeys}
            tableLayout="fixed"
            // {...tableVirtualProps}
            dataSource={[...dsConnTabData({ dsPaneTypes, dispatch })]}
            expandable={{
                expandedRowRender: (record) =>
                    record.dataSourcePane === 'CONNECTIONS'
                        ? record.menu.map((item) => (
                              <div className="ds-pane-actions">
                                  <Paragraph
                                      editable={{
                                          onChange: (value) => {
                                              dispatch(
                                                  hcrActions.handleEditingDsPaneItem(
                                                      {
                                                          dataSourcePane:
                                                              record.dataSourcePane,
                                                          itemId: item.id,
                                                          key: 'name',
                                                          value,
                                                      }
                                                  )
                                              );
                                          },
                                      }}
                                  >
                                      {item.name}
                                  </Paragraph>
                                  <span>
                                      <Space>
                                          <Select
                                              defaultValue={'M'}
                                              style={{
                                                  width: 54,
                                              }}
                                              value={item.activeDs[0].toUpperCase()}
                                              onChange={(value) => {
                                                  handleChange(
                                                      record.dataSourcePane,
                                                      item.id,
                                                      value
                                                  );
                                                  let reqActDs =
                                                      item?.dataSource?.find(
                                                          (ele) =>
                                                              ele.dataSourceName ===
                                                              value
                                                      );
                                                  dispatch(
                                                      hcrActions.editActiveConfig(
                                                          {
                                                              value: reqActDs?.config,
                                                          }
                                                      )
                                                  );
                                              }}
                                              options={connTypes.map((ele) => {
                                                  return {
                                                      value: ele.name,
                                                      label: ele.name,
                                                  };
                                              })}
                                          />
                                          <SettingOutlined
                                              onClick={() => {
                                                  let reqActDs =
                                                      item?.dataSource?.find(
                                                          (ele) =>
                                                              ele.dataSourceName ===
                                                              item.activeDs
                                                      );
                                                  dispatch(
                                                      hcrActions.createActiveConfig(
                                                          {
                                                              dataSourcePane:
                                                                  record.dataSourcePane,
                                                              itemId: item.id,
                                                              value: reqActDs.config,
                                                          }
                                                      )
                                                  );
                                              }}
                                          />
                                          <DeleteOutlined
                                              onClick={() => {
                                                  dispatch(
                                                      hcrActions.handleDeletingDsPaneItem(
                                                          {
                                                              dataSourcePane:
                                                                  record.dataSourcePane,
                                                              itemId: item.id,
                                                          }
                                                      )
                                                  );
                                              }}
                                          />
                                      </Space>
                                  </span>
                              </div>
                          ))
                        : record.dataSourcePane === hcrDSQuery
                        ? record.menu.map((item) => (
                              <div className="ds-pane-actions">
                                  <Paragraph
                                      editable={{
                                          onChange: (value) => {
                                              dispatch(
                                                  hcrActions.handleEditingDsPaneItem(
                                                      {
                                                          dataSourcePane:
                                                              record.dataSourcePane,
                                                          itemId: item.id,
                                                          key: 'name',
                                                          value,
                                                      }
                                                  )
                                              );
                                          },
                                      }}
                                  >
                                      {item.name}
                                  </Paragraph>
                                  <span>
                                      <Space>
                                          <Dropdown
                                              trigger={['click']}
                                              overlay={getExistedDsPanes({
                                                  record,
                                                  itemId: item.id,
                                                  dsPaneToCompare:
                                                      'CONNECTIONS',
                                              })}
                                          >
                                              <Button
                                                  icon={<LinkOutlined />}
                                              ></Button>
                                          </Dropdown>
                                          <Dropdown
                                              trigger={['click']}
                                              overlay={getExistedDsPanes({
                                                  record,
                                                  itemId: item.id,
                                                  dsPaneToCompare:
                                                      hcrDSParameter,
                                              })}
                                          >
                                              <Button
                                                  icon={<>P</>}
                                                  onClick={() => {
                                                      const parameter =
                                                          dsPaneTypes.find(
                                                              (connType) =>
                                                                  connType.dataSourcePane ===
                                                                  hcrDSParameter
                                                          );
                                                      if (
                                                          !parameter.menu.length
                                                      ) {
                                                          dispatch(
                                                              hcrActions.handleAddingDsPaneItem(
                                                                  hcrDSParameter
                                                              )
                                                          );
                                                      }
                                                  }}
                                              ></Button>
                                          </Dropdown>
                                          <PlayCircleOutlined
                                              onClick={() => {
                                                  handleRunQuery(item);
                                              }}
                                          />
                                          <SettingOutlined
                                              onClick={() => {
                                                  dispatch(
                                                      hcrActions.createActiveConfig(
                                                          {
                                                              dataSourcePane:
                                                                  record.dataSourcePane,
                                                              itemId: item.id,
                                                              value: item.config,
                                                          }
                                                      )
                                                  );
                                              }}
                                          />
                                          <DeleteOutlined
                                              onClick={() => {
                                                  dispatch(
                                                      hcrActions.handleDeletingDsPaneItem(
                                                          {
                                                              dataSourcePane:
                                                                  record.dataSourcePane,
                                                              itemId: item.id,
                                                          }
                                                      )
                                                  );
                                              }}
                                          />
                                      </Space>
                                  </span>
                              </div>
                          ))
                        : record.menu.map((item) => (
                              <div>
                                  <div className="ds-pane-actions">
                                      <Paragraph
                                          editable={{
                                              onChange: (value) => {
                                                  dispatch(
                                                      hcrActions.handleEditingDsPaneItem(
                                                          {
                                                              dataSourcePane:
                                                                  record.dataSourcePane,
                                                              itemId: item.id,
                                                              key: 'name',
                                                              value,
                                                          }
                                                      )
                                                  );
                                              },
                                          }}
                                      >
                                          {item.name}
                                      </Paragraph>
                                      <span>
                                          <Space>
                                              <Select
                                                  defaultValue={'S'} // text
                                                  style={{
                                                      width: 54,
                                                  }}
                                                  value={item.type[0].toUpperCase()}
                                                  onChange={(value) => {
                                                      handleChange(
                                                          record.dataSourcePane,
                                                          item.id,
                                                          value
                                                      );
                                                  }}
                                                  options={Object.keys(
                                                      parameters?.classNames
                                                  ).map((ele) => {
                                                      return {
                                                          value: ele,
                                                          label: ele,
                                                      };
                                                  })}
                                              />
                                              <SettingOutlined
                                                  onClick={() => {
                                                      dispatch(
                                                          hcrActions.createActiveConfig(
                                                              {
                                                                  dataSourcePane:
                                                                      record.dataSourcePane,
                                                                  itemId: item.id,
                                                                  value: item.config,
                                                              }
                                                          )
                                                      );
                                                  }}
                                              />
                                              <DeleteOutlined
                                                  onClick={() => {
                                                      dispatch(
                                                          hcrActions.handleDeletingDsPaneItem(
                                                              {
                                                                  dataSourcePane:
                                                                      record.dataSourcePane,
                                                                  itemId: item.id,
                                                              }
                                                          )
                                                      );
                                                  }}
                                              />
                                          </Space>
                                      </span>
                                  </div>
                                  <div className="ds-pane-actions">
                                      <Paragraph>{item.sql.name}</Paragraph>
                                      <span>
                                          <Space>
                                              <Dropdown
                                                  trigger={['click']}
                                                  overlay={getExistedDsPanes({
                                                      record,
                                                      itemId: item.sql.id,
                                                      dsPaneToCompare:
                                                          'CONNECTIONS',
                                                      setForSql: true,
                                                  })}
                                              >
                                                  <Button
                                                      icon={<LinkOutlined />}
                                                  ></Button>
                                              </Dropdown>
                                              <Dropdown
                                                  trigger={['click']}
                                                  // getExistedDsPanes({ record, itemId: item.id, dsPaneToCompare: 'PARAMETER', setForSql: true })
                                                  overlay={parameterMenu({
                                                      record,
                                                      dsPaneToCompare:
                                                          hcrDSParameter,
                                                      record,
                                                      itemId: item.id,
                                                  })}
                                              >
                                                  <Button
                                                      icon={<>P</>}
                                                      onClick={() => {
                                                          // const parameter = dsPaneTypes.find(connType => connType.dataSourcePane === 'PARAMETER');
                                                          // if (!parameter.menu.length) {
                                                          //     dispatch(hcrActions.handleAddingDsPaneItem('PARAMETER'));
                                                          // }
                                                      }}
                                                  ></Button>
                                              </Dropdown>
                                              <Select
                                                  defaultValue={'S'}
                                                  style={{
                                                      width: 54,
                                                  }}
                                                  value={item.sql.sqlType[0].toUpperCase()}
                                                  onChange={(value) => {
                                                      handleChange(
                                                          record.dataSourcePane,
                                                          item.id,
                                                          value
                                                      );
                                                  }}
                                                  options={sqlTypes.map(
                                                      (ele) => {
                                                          return {
                                                              value: ele.name,
                                                              label: ele.name,
                                                          };
                                                      }
                                                  )}
                                              />
                                          </Space>
                                      </span>
                                  </div>
                              </div>
                          )),
            }}
        />
    );
}
