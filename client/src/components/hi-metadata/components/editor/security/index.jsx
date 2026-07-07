import { CloseOutlined, InfoCircleFilled, LoadingOutlined, SearchOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import { Button, Empty, Form, Input, Modal, Popconfirm, Radio, Space, Spin, Tag, Tooltip, Tree, Typography } from 'antd';
import "codemirror/lib/codemirror.css";
import "codemirror/theme/dracula.css";
import "codemirror/theme/material.css";
import { useEffect, useState } from 'react';
import { Controlled as ControlledEditor } from "react-codemirror2";
import { useDispatch, useSelector } from 'react-redux';
import metadataRequests from '../../../../../base/requests/metadata.requests';
import { useDebounce } from '../../../../../hooks';
import { handleAddOneMoreSecurity, metadataSecurityData, saveExpressionData, setAccessType, setEntityNames, setExecutionType, setExpressionName, setExpressionType, setFirstRender, setFormData, setIsApplyDisabled, setIsInfoShow, setSecurityEdit, setSecurityTableData, setSelectedTableOrColumnKey, setShowValidatedTable, setSecurityKeysChecked, setDoResetFormData, setConditionAndFilterValue, updateSecurityItemsOnExpEdit } from '../../../../../redux/actions';
import notify from '../../../../hi-notifications/notify';
import { entityNameHandler, handleGetSecurityData, mapColumnsWithIds, mapTablesColumnsWithUniqueKey, tablesToTableDataConverter } from './securityHelperMethods';
import { securityValidations } from './securityValidations';
import ValidatedTable from './validatedTable/validatedTable';
require('codemirror/mode/clike/clike');

const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;
const { Search } = Input;
const { Text, Paragraph } = Typography;
// const conditionAndFilterInitVal = {
//     groovy: {
//         condition: {
//             value: '',
//         },
//         filter: {
//             value: ''
//         }
//     },
//     conditionIf: {
//         condition: {
//             value: ""
//         },
//         filter: {
//             value: ''
//         }
//     }
// }

let prevCheckedKeys = [];
let applyTimeOutId = '';
let validateTimeOutId = '';

export default function Security() {
    const [form] = Form.useForm();
    const [isModalVisible, setIsModalVisible] = useState(false);
    const { tables, selectedTableOrColumnKey, securityConstants, mode, edit, isValidatedTableShow, securityTableData, addOneMoreSecurity, saveDetails, isFirstRender, securityFormData: formData, accessType, entityNames, executionType, expressionName, expressionType, isApplyDisabled, isInfoShow, securityKeysChecked, doResetFormData, isGetSecurityCallDone, conditionAndFilterValue } = useSelector(state => state.metadata.present);
    const dispatch = useDispatch();
    const { getSecurityConstants, ValidatorCheck, applyService, } = metadataRequests(dispatch);
    const { category } = selectedTableOrColumnKey;
    const [keysExpanded, setKeysExpanded] = useState([]);
    const expressionChildren = { table: securityConstants.expressionType?.filter(ele => ele !== 'column') || [], column: ['column'] };
    const filteredExpressions = (expressionChildren[category] || (!isInfoShow && !securityKeysChecked.length && !edit ? expressionChildren.table : (formData["Expression Type"] === 'column' ? ['column'] : securityConstants.expressionType?.filter(ele => ele !== 'column'))));
    const Notify = notify(dispatch);
    const [searchedVal, setSearchedVal] = useState('');
    const [isSearching, setIsSearching] = useState(true);
    const [isModalTreeExist, setIsModalTreeExist] = useState(true);
    const { setFieldsValue } = form;
    const [showContent, setShowContent] = useState(false);
    const [expNameInput, setExpNameInput] = useState(expressionName);
    const expNameDebounceValue = useDebounce(expNameInput, 500);
    const [expTypeInput, setExpTypeInput] = useState(expressionType);
    const expTypeDebounceValue = useDebounce(expTypeInput, 500);
    const [accTypeInput, setAccTypeInput] = useState(accessType);
    const accTypeDebounceValue = useDebounce(accTypeInput, 500);
    const [exeTypeInput, setExeTypeInput] = useState(executionType);
    const exeTypeDebounceValue = useDebounce(exeTypeInput, 500);
    const [treeData, setTreeData] = useState([]);
    const [isApplyServiceProcessing, setIsApplyServiceProcessing] = useState(false);
    const [isValidateServiceProcessing, setIsValidateServiceProcessing] = useState(false);


    useEffect(() => {
        ((expNameDebounceValue !== expressionName)) && dispatch(setExpressionName(expNameDebounceValue));
    }, [expNameDebounceValue])

    useEffect(() => {
        (expTypeDebounceValue && (expTypeDebounceValue !== expressionType)) && dispatch(setExpressionType(expTypeDebounceValue));
    }, [expTypeDebounceValue])

    useEffect(() => {
        (accTypeDebounceValue && (accTypeDebounceValue !== accessType)) && dispatch(setAccessType(accTypeDebounceValue));
    }, [accTypeDebounceValue])

    useEffect(() => {
        (exeTypeDebounceValue && (exeTypeDebounceValue !== executionType)) && dispatch(setExecutionType(exeTypeDebounceValue));
    }, [exeTypeDebounceValue])

    useEffect(() => {
        if (Object.keys(securityConstants).length && !edit) {
            if (isFirstRender) {
                if (category === 'column') {
                    setExpTypeInput('column');
                } else {
                    if (expressionType === 'column' || !expressionType) {
                        setExpTypeInput('global')
                    }
                }
                // (category !== 'column' || !category) ? setExpTypeInput('global') : setExpTypeInput('column');
            } else {
                if (category === 'column' && (expTypeInput !== 'column' || !addOneMoreSecurity)) {
                    setExpTypeInput('column');
                } else if (((category !== 'column') && ((expressionType === 'column') || !expressionType)) || !category) {
                    setExpTypeInput('global');
                }
            }
        }
        if (addOneMoreSecurity && !securityKeysChecked.length) {
            entityNames?.length && dispatch(setEntityNames([]));
        }
    }, [category, addOneMoreSecurity])

    useEffect(() => {
        !Object.keys(securityConstants).length && getSecurityConstants(
            { action: "getSettings" },
            '',
            (res) => {
                dispatch(metadataSecurityData(res));
                setExpNameInput('');
                category === 'column' ? setExpTypeInput(category) : setExpTypeInput(res.expressionType[0])
                setAccTypeInput(res.access[1]);
                setExeTypeInput(res.type[0]);
                dispatch(setConditionAndFilterValue([{ key: 'conditionIfCondition', value: res.conditionIf.conditionTemplate }, { key: 'groovyCondition', value: res.groovy.conditionTemplate }, { key: 'conditionIfFilter', value: res.conditionIf.filterTemplate }, { key: 'groovyFilter', value: res.groovy.filterTemplate }]));
            },
            (err) => {
                // Notify.error({ ...err, type: "Network Call" })
            }
        );
        if (mode === 'create') {
            !showContent && setShowContent(true);
        } else {
            if (isGetSecurityCallDone) {
                if (securityTableData?.length) {
                    isInfoShow && dispatch(setIsInfoShow(false));
                }
                !showContent && setShowContent(true);
            }
        }
    }, [])

    useEffect(() => {
        if (['edit'].includes(mode)) {
            if (saveDetails && Object.keys(tables) && !isGetSecurityCallDone) {
                handleGetSecurityData({
                    saveDetails,
                    dispatch,
                    setSecurityTableData,
                    setIsInfoShow,
                    setShowValidatedTable,
                    setShowContent,
                    tables,
                    mode,
                    isGetSecurityCallDone,
                    isInfoShow,
                    isValidatedTableShow
                })
            }
        }
    }, [tables])

    const handleApplyCall = () => {
        !isApplyServiceProcessing && setIsApplyServiceProcessing(true);
        let formInfo;
        let currentSecurityData = { ...formData };
        formInfo = {
            "uuid": true,
            "expression": [
                {
                    "expressionName": currentSecurityData["Expression Name"],
                    "expressionType": currentSecurityData["Expression Type"],
                    "accessType": currentSecurityData["Access Type"],
                    "executionType": currentSecurityData["Execution Type"],
                    "on": entityNames.reduce((acc, ele) => {
                        (ele.key) && acc.push(ele.key);
                        return acc;
                    }, []),
                    "condition": currentSecurityData["Condition"],
                    "filter": currentSecurityData["Filter"],
                }
            ]
        }
        if (!edit) {
            formInfo.expression[0].action = "add";
        } else {
            formInfo.expression[0].action = "edit";
            formInfo.expression[0].expressionId = currentSecurityData['expressionId'];
        }
        clearTimeout(applyTimeOutId);
        applyTimeOutId = setTimeout(() => {
            applyService(
                formInfo,
                '',
                (res) => {
                    setIsApplyServiceProcessing(false);
                    isApplyDisabled && dispatch(setIsApplyDisabled(false));
                    if (!edit) {
                        if (addOneMoreSecurity) {
                            dispatch(setSecurityTableData([...securityTableData, { ...formData, expressionId: res.expressionId, tooltipInfo: entityNames.map(ele => ele.tooltipInfoObj) }]))
                        } else {
                            dispatch(setSecurityTableData([{ ...formData, expressionId: res.expressionId, tooltipInfo: entityNames.map(ele => ele.tooltipInfoObj) }]));
                        }
                        dispatch(saveExpressionData({
                            "expressionId": res.expressionId,
                            "action": "add"
                        }));
                    } else {
                        dispatch(setSecurityTableData(securityTableData.map(rec => rec.key === formData.key ? ({ ...formData, tooltipInfo: entityNames.map(ele => ele.tooltipInfoObj) }) : rec)))
                        dispatch(saveExpressionData({
                            "expressionId": formData.expressionId,
                            "action": "edit"
                        }));
                    }
                    // dispatch(setDoResetFormData());
                    addOneMoreSecurity && dispatch(handleAddOneMoreSecurity(false));
                    edit && dispatch(setSecurityEdit(false));
                    // securityKeysChecked.length && dispatch(setSecurityKeysChecked([]));
                    !isValidatedTableShow && dispatch(setShowValidatedTable(true));
                    // res.message && Notify.success({
                    //     type: "Network Call",
                    //     ...res,
                    // });

                },
                (err) => {
                    // Notify.error({ ...err, type: "Network Call" })
                }
            )
        }, 500)
    }

    const handleOk = () => {
        setKeysExpanded([]);
        setIsSearching(true);
        setSearchedVal('');
        prevCheckedKeys = [];
        setIsModalTreeExist(true);
        setIsModalVisible(false);
    };

    const handleCancel = () => {
        setKeysExpanded([]);
        setIsSearching(true);
        setSearchedVal('')
        dispatch(setSecurityKeysChecked([...prevCheckedKeys]));
        prevCheckedKeys = [];
        setIsModalTreeExist(true);
        setIsModalVisible(false);
    };

    useEffect(() => {
        setFieldsValue({
            "Expression Name": expNameInput,
            "Execution Type": exeTypeInput,
            "Access Type": accTypeInput,
            "Entity Names": entityNames.length ? entityNames.reduce((acc, cur) => (acc ? `${acc},` : '') + cur.entityName, '') : '',
            "Expression Type": expTypeInput
        })
    }, [exeTypeInput, accTypeInput, entityNames, expNameInput, expTypeInput])

    useEffect(() => {
        (expNameInput !== expressionName) && setExpNameInput(expressionName);
        (expTypeInput !== expressionType) && setExpTypeInput(expressionType);
        (accTypeInput !== accessType) && setAccTypeInput(accessType);
        (exeTypeInput !== executionType) && setExeTypeInput(executionType);
    }, [accessType, executionType, expressionName, expressionType])

    useEffect(() => {
        if (Object.keys(securityConstants).length) {
            // expressionName && dispatch(setExpressionName(''));
            // (expTypeInput !== 'column') && dispatch(setExpressionType(securityConstants.expressionType[0]));
            // dispatch(setAccessType(securityConstants.access[1]));
            // dispatch(setExecutionType(securityConstants.type[0]));

            // setExpNameInput("");
            // (expTypeInput !== 'column') && setExpTypeInput(securityConstants.expressionType[0]);
            // setAccTypeInput(securityConstants.access[1]);
            // setExeTypeInput(securityConstants.type[0]);

            // setConditionAndFilterValue(preVal => {
            //     preVal["conditionIf"]["condition"].value = securityConstants.conditionIf?.conditionTemplate;
            //     preVal["groovy"]["condition"].value = securityConstants.groovy?.conditionTemplate;
            //     preVal["conditionIf"]["filter"].value = securityConstants.conditionIf?.filterTemplate;
            //     preVal["groovy"]["filter"].value = securityConstants.groovy?.filterTemplate;
            //     return { ...preVal }
            // });
            !isApplyDisabled && dispatch(setIsApplyDisabled(true));
        }
    }, [doResetFormData])

    useEffect(() => {
        if (formData && Object.keys(formData || {}).length) {
            dispatch(updateSecurityItemsOnExpEdit(formData))
            // setExpTypeInput(formData["Expression Type"]);
            // dispatch(setExpressionType(formData["Expression Type"]));
            // setAccTypeInput(formData["Access Type"]);
            // dispatch(setAccessType(formData["Access Type"]));
            // setExeTypeInput(formData["Execution Type"]);
            // dispatch(setExecutionType(formData["Execution Type"]));
            // setExpNameInput(formData["Expression Name"]);
            // dispatch(setExpressionName(formData["Expression Name"]));
            let conditionIfConditionVal = ''
            let groovyConditionVal = ''
            let conditionIfFilterVal = ''
            let groovyFilterVal = ''
            if (formData["Execution Type"] === 'groovy') {
                if (formData["Filter"]) {
                    groovyFilterVal = formData["Filter"];
                } else {
                    groovyFilterVal = securityConstants.groovy?.filterTemplate;
                }
                if (formData["Condition"]) {
                    groovyConditionVal = formData["Condition"];
                } else {
                    groovyConditionVal = securityConstants.groovy?.conditionTemplate;
                }
                dispatch(setConditionAndFilterValue([{ key: 'groovyCondition', value: groovyConditionVal }, { key: 'groovyFilter', value: groovyFilterVal }]));
            } else {
                if (formData["Filter"]) {
                    conditionIfFilterVal = formData["Filter"];
                } else {
                    conditionIfFilterVal = securityConstants.conditionIf?.filterTemplate;
                }
                if (formData["Condition"]) {
                    conditionIfConditionVal = formData["Condition"];
                } else {
                    conditionIfConditionVal = securityConstants.conditionIf?.conditionTemplate;
                }
                dispatch(setConditionAndFilterValue([{ key: 'conditionIfCondition', value: conditionIfConditionVal }, { key: 'conditionIfFilter', value: conditionIfFilterVal }]));
            }
            // if (formData["Filter"]) {
            //     if (exeTypeInput === 'groovy') {
            //         groovyFilterVal = formData["Filter"];
            //         // conditionIfFilterVal = securityConstants.conditionIf?.filterTemplate;
            //     } else {
            //         conditionIfFilterVal = formData["Filter"];
            //         // groovyFilterVal = securityConstants.groovy?.filterTemplate;
            //     }
            // } 
            // else {
            //     conditionIfFilterVal = securityConstants.conditionIf?.filterTemplate;
            //     groovyFilterVal = securityConstants.groovy?.filterTemplate;
            // }
            // if (formData["Condition"]) {
            //     if (exeTypeInput === 'groovy') {
            //         groovyConditionVal = formData["Condition"];
            //         // conditionIfConditionVal = securityConstants.conditionIf?.conditionTemplate;
            //     } else {
            //         conditionIfConditionVal = formData["Condition"];
            //         // groovyConditionVal = securityConstants.groovy?.conditionTemplate;
            //     }
            // } 
            // else {
            //     groovyConditionVal = securityConstants.groovy?.conditionTemplate;
            //     conditionIfConditionVal = securityConstants.conditionIf?.conditionTemplate;
            // }
            // dispatch(setSecurityKeysChecked(formData.securityKeysToBeCheck)); // keys pending
            dispatch(setSelectedTableOrColumnKey({ category: formData["Expression Type"] === 'column' ? 'column' : 'table' }));
        }
    }, [formData, securityConstants])

    const securityFormFinishHandler = (formValues) => {
        let data = {
            "condition": conditionAndFilterValue[exeTypeInput]?.condition.value,
        };
        (accTypeInput !== 'deny') && (data.filter = conditionAndFilterValue[exeTypeInput]?.filter.value);
        if (!isValidateServiceProcessing) {
            setIsValidateServiceProcessing(true)
        }
        clearTimeout(validateTimeOutId);
        validateTimeOutId = setTimeout(() => {
            ValidatorCheck(
                {
                    "executionType": exeTypeInput,
                    "data": data
                },
                '',
                (res) => {
                    setIsValidateServiceProcessing(false)
                    formValues = {
                        ...formValues, Condition: data.condition, securityKeysToBeCheck: [...securityKeysChecked]
                    };
                    ("filter" in data) && (formValues.Filter = data.filter);
                    if (!edit) {
                        formValues.key = securityTableData.length ? securityTableData[securityTableData.length - 1].key + 1 : 0;
                    } else {
                        formValues.key = formData.key;
                        formValues.expressionId = formData.expressionId;
                    }
                    dispatch(setFormData(formValues));
                    isApplyDisabled && dispatch(setIsApplyDisabled(false));
                    // res.message && Notify.success({
                    //     type: "Network Call",
                    //     ...res,
                    // });
                },
                (err) => {
                    setIsValidateServiceProcessing(false)
                    !isApplyDisabled && dispatch(setIsApplyDisabled(true));
                    // err.message && Notify.error({ ...err, type: "Network Call" })
                }
            )
        }, 500)

    }
    // let treeData;
    useEffect(() => {
        setTreeData(tablesToTableDataConverter({ tables, isSearching, searchedVal, setIsModalTreeExist, isModalTreeExist, category, formData, edit, mode, setKeysExpanded }))
    }, [tables, isSearching, category, edit])

    const getEntityDetails = (key, tree) => {
        let parentTitle, childTitle, Name, Id, Datasource, Alias, Original;
        for (let i = 0; i < tree.length; i++) {
            const node = tree[i];
            if (key !== node.key) {
                if (node.children) {
                    let child = node.children.find(item => item.key === key)
                    if (child) {
                        parentTitle = node.title;
                        childTitle = child.title;
                        Name = child.Name;
                        Id = node.Id;
                        Datasource = node.Datasource;
                        Alias = child.title;
                        Original = child.Original;
                        // TableAlias = node.title;
                    } else if (getEntityDetails(key, node.children)) {
                        parentTitle = getEntityDetails(key, node.children);
                    }
                }
            } else {
                return { parentTitle: node.title, childTitle: '', key, tooltipInfoObj: { Name: node.Name, Id: node.Id, Datasource: node.Datasource, Alias: node.title, key: node.key, Original: node.Original } }
            }
            if (parentTitle && childTitle) {
                return { parentTitle, childTitle, key, tooltipInfoObj: { Name, Id, Datasource, Alias, key, Original, Table: parentTitle } }
            }
        }
        return null;
    }

    useEffect(() => {
        let entityValues = [];
        treeData.length && securityKeysChecked.forEach((ele) => {
            let entityObj = getEntityDetails(ele, treeData);
            entityObj && entityValues.push(entityObj);
        })
        entityNameHandler({ entityValues, dispatch });
    }, [securityKeysChecked, treeData])

    const confirm = (val) => {
        let conditionIfConditionVal = ''
        let groovyConditionVal = ''
        let conditionIfFilterVal = ''
        let groovyFilterVal = ''
        // setExeTypeInput(val);
        if (executionType !== val) {
            dispatch(setExecutionType(val));
        }
        if (val === 'groovy') {
            groovyFilterVal = securityConstants.groovy?.filterTemplate;
            groovyConditionVal = securityConstants.groovy?.conditionTemplate;
            dispatch(setConditionAndFilterValue([{ key: 'groovyCondition', value: groovyConditionVal }, { key: 'groovyFilter', value: groovyFilterVal }]));
        } else {
            conditionIfConditionVal = securityConstants.conditionIf?.conditionTemplate;
            conditionIfFilterVal = securityConstants.conditionIf?.filterTemplate;
            dispatch(setConditionAndFilterValue([{ key: 'conditionIfCondition', value: conditionIfConditionVal }, { key: 'conditionIfFilter', value: conditionIfFilterVal }]));
        }
    }

    // useEffect(() => {
    //     if (key) {
    //         if (isInfoShow) {
    //             dispatch(setIsInfoShow(false));
    //         }
    //     }
    // }, [key])

    return (
        showContent ? (isInfoShow ? (
            <Tag className='security-info' icon={<InfoCircleFilled className='security-info-icon' />} color="#D9EDF7">Please select Table/Column/View to apply security.</Tag>
        ) : (Object.keys(securityConstants).length) ? ((edit || addOneMoreSecurity || !isValidatedTableShow) ? (
            <>
                <Form data-testid="hi-metadata-security" className="edit-security-form" labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} form={form} autoComplete="off" onFinish={(formValues) => {
                    let makeCall = false;
                    if (accTypeInput === 'grant') {
                        if (conditionAndFilterValue[exeTypeInput]?.condition.value?.trim() && conditionAndFilterValue[exeTypeInput]?.filter.value?.trim()) {
                            makeCall = true;
                        }
                    } else {
                        if (conditionAndFilterValue[exeTypeInput]?.condition.value?.trim()) {
                            makeCall = true;
                        }
                    }
                    makeCall && securityFormFinishHandler(formValues);
                }}
                    onValuesChange={(changedValues, allValues) => {
                        if ('Execution Type' in changedValues) {
                            setFieldsValue({ 'Execution Type': exeTypeInput });
                        }
                        setExpNameInput(allValues["Expression Name"]);
                        setExpTypeInput(allValues["Expression Type"]);
                        setAccTypeInput(allValues["Access Type"]);
                    }}
                >
                    <Form.Item className="security-form-item" tooltip="Provide a unique name to identify this expression." label={["Expression Name"]} name="Expression Name" rules={[{ validator: securityValidations }]}>
                        <Input placeholder='Please enter expression name' />
                    </Form.Item>
                    <Form.Item className="security-form-item entity-name-err-wrap mb-6" tooltip="Select any entity (table, column or view) from metadata pane. On the selected entities the security will be applied except in the case of global expression type." label={["Entity Name(s)"]} name="Entity Names" htmlFor={null} rules={[{ validator: (rule, value) => securityValidations(rule, value, edit) }]}>
                        <Input disabled={true} placeholder={!entityNames.length ? ((edit) ? 'Please select entity name by clicking Add more...' : 'Please select Table/Column/View to apply security') : ''} value={entityNames.length ? entityNames.reduce((acc, cur) => (acc ? `${acc},` : '') + cur.entityName, '') : ''} />
                        <Button size='small' className={`security-form-add-more`} onClick={() => { prevCheckedKeys = [...securityKeysChecked]; setIsModalVisible(true); }}>
                            Add more...
                        </Button>
                    </Form.Item>
                    <Form.Item className="security-form-item mb-3" tooltip="Choose the type of expression from global, table or column. If global type is selected then irrespective of entity selection the security will be applied globally. In case of table and column the security will be applied on table entities." label={["Expression Type"]} name="Expression Type" htmlFor={null}>
                        <Radio.Group name="expressionGroup">
                            {filteredExpressions?.map((ele) => <Radio key={ele} value={ele}>{ele}</Radio>)}
                        </Radio.Group>
                    </Form.Item>
                    <Form.Item className="security-form-item mb-3" tooltip="Choose required expression to either allow or deny access." label={["Access Type"]} name="Access Type" htmlFor={null}>
                        <Radio.Group name="accessGroup">
                            {securityConstants.access?.map(ele => <Radio key={ele} value={ele}>{ele}</Radio>)}
                        </Radio.Group>
                    </Form.Item>
                    <Form.Item className="security-form-item mb-3" tooltip={{
                        title: "Choose between different options to provide the expression. For more information click on this help icon.",
                        icon: <QuestionCircleOutlined style={{ cursor: "pointer" }} />,
                        onClick: () => window.open("https://www.helicalinsight.com/metadata-security-overview/")
                    }}
                        label={["Execution Type"]} name="Execution Type" htmlFor={null}>
                        <Radio.Group name="executionGroup">
                            {securityConstants.type?.map(ele => <Popconfirm
                                overlayClassName='execution-popconfirmation'
                                disabled={exeTypeInput === ele}
                                title='Changing the Execution Type will discard any changes in Filter and/or Condition. Is that what you want to do?'
                                onConfirm={() => confirm(ele)}
                                onCancel={() => {
                                    setFieldsValue({
                                        "Execution Type": exeTypeInput,
                                    })
                                }}
                                okText="Yes"
                                cancelText="No"
                            ><Radio key={ele} value={ele}>{ele}</Radio>
                            </Popconfirm>)}
                        </Radio.Group>
                    </Form.Item>
                    <Form.Item className="security-form-item mb-4" tooltip={{
                        title: "Provide the condition, If the condition is met then the expression will be applied. For more information click on this help icon",
                        icon: <QuestionCircleOutlined style={{ cursor: "pointer" }} />,
                        onClick: () => window.open("https://www.helicalinsight.com/metadata-security-overview/")
                    }} label={["Condition"]} >
                        <ControlledEditor
                            onBeforeChange={(a, b, val) => {
                                let key = '';
                                if (exeTypeInput === 'groovy') {
                                    key = 'groovyCondition';
                                } else {
                                    key = 'conditionIfCondition';
                                }
                                dispatch(setConditionAndFilterValue([{ key, value: val }]));
                            }}
                            value={conditionAndFilterValue[exeTypeInput]?.condition.value}
                            className="security-condition-code-editor"
                            options={{
                                lineWrapping: true,
                                lint: true,
                                mode: "text/x-java",
                                lineNumbers: true,
                                theme: "dracula",
                                autoCloseTags: true,
                                autoCloseBrackets: true,
                            }}
                        />
                        {!conditionAndFilterValue[exeTypeInput]?.condition.value?.trim() && <p className="code-validator">Please enter condition expression</p>}
                    </Form.Item>
                    {(accTypeInput !== 'deny') && <Form.Item className="security-form-item mb-4" tooltip={{
                        title: "Provide the filter condition which will be applied if the condition is met. This is helpful when you are interested in restricting the data (row level). The filter expression will filter the data before presenting it to the end user. For more information click on this help icon",
                        icon: <QuestionCircleOutlined style={{ cursor: "pointer" }} />,
                        onClick: () => window.open("https://www.helicalinsight.com/metadata-security-overview/")
                    }} label={["Filter"]}>
                        <ControlledEditor
                            onBeforeChange={(a, b, val) => {
                                let key = '';
                                if (exeTypeInput === 'groovy') {
                                    key = 'groovyFilter';
                                } else {
                                    key = 'conditionIfFilter';
                                }
                                dispatch(setConditionAndFilterValue([{ key, value: val }]));
                            }}
                            value={conditionAndFilterValue[exeTypeInput]?.filter.value}
                            className="security-filter-code-editor"
                            options={{
                                lineWrapping: true,
                                lint: true,
                                mode: "text/x-java",
                                lineNumbers: true,
                                theme: "dracula",
                                autoCloseTags: true,
                                autoCloseBrackets: true,
                                // autofocus: true,
                            }}
                        />
                        {!conditionAndFilterValue[exeTypeInput]?.filter.value?.trim() && <p className="code-validator">Please enter filter expression</p>}
                    </Form.Item>}
                    <Form.Item className="security-form-item security-form-actions">
                        <Space>
                            <Button className='cancel-btn' onClick={() => {
                                !isApplyDisabled && dispatch(setIsApplyDisabled(true));
                                if (edit || addOneMoreSecurity) {
                                    !isValidatedTableShow && dispatch(setShowValidatedTable(true));
                                    edit && dispatch(setSecurityEdit(false));
                                    addOneMoreSecurity && dispatch(handleAddOneMoreSecurity(false));
                                } else {
                                    dispatch(setSecurityKeysChecked([]));
                                    dispatch(setDoResetFormData());
                                    dispatch(setSelectedTableOrColumnKey({}));
                                    !isInfoShow && dispatch(setIsInfoShow(true));
                                }
                            }}
                            >
                                Cancel
                            </Button>
                            <Button className='reset-btn' onClick={() => { dispatch(setDoResetFormData()) }}>
                                Reset
                            </Button>
                            <Button className='validate-btn' loading={isValidateServiceProcessing} onClick={form.submit}>
                                Validate
                            </Button>
                            <Button type='primary' loading={isApplyServiceProcessing} className={`apply-btn ${isApplyDisabled && 'apply-disable'}`} disabled={isApplyDisabled} onClick={() => {
                                handleApplyCall();
                            }}>
                                Apply
                            </Button>
                        </Space>
                    </Form.Item>
                </Form>
                <Modal className='security-modal' title="Select Entity Name(s)" visible={isModalVisible} onOk={handleOk} onCancel={handleCancel}>
                    <Form className="security-modal-form" labelCol={{ span: 5 }} wrapperCol={{ span: 16 }} autoComplete="off" >
                        <Form.Item className="security-modal-form-item" label={["Search"]}>
                            <Search className='security-modal-search' placeholder='Search Phrase...' onChange={(e) => {
                                if (!e.target.value) {
                                    setIsSearching(true)
                                }
                                setSearchedVal(e.target.value);
                            }} value={searchedVal} enterButton={(!isSearching && searchedVal) ? <CloseOutlined style={{ fontSize: '16px' }} onClick={() => { setSearchedVal(''); setIsSearching(true); setIsModalTreeExist(true); setKeysExpanded([]); }} /> : <SearchOutlined style={{ fontSize: '16px' }} onClick={() => {
                                if (searchedVal) {
                                    setIsSearching(false)
                                } else {
                                    Notify.warning({
                                        type: "Frontend Notification",
                                        message: "please provide the search phrase",
                                    });
                                }
                            }} />} />
                        </Form.Item>
                        <Form.Item className="security-modal-form-item">
                            {isModalTreeExist ? <Tree
                                checkable
                                showIcon={true}
                                expandedKeys={keysExpanded}
                                checkedKeys={securityKeysChecked}
                                titleRender={(nodeData) => {
                                    let tooltipArr = ['Name', 'Alias'];
                                    if (nodeData.category === 'table')
                                        tooltipArr = [...tooltipArr, ...['Datasource', 'Id']];
                                    if (nodeData.Original)
                                        tooltipArr.push('Original');
                                    return <Tooltip title={<table>
                                        <tbody>
                                            {tooltipArr.map(ele => <tr key={nodeData.key}>
                                                <td>{ele} :&nbsp;</td>
                                                <td>{ele === 'Alias' ? nodeData.title : nodeData[ele]}</td>
                                            </tr>)}
                                        </tbody>
                                    </table>}
                                    >
                                        <Text>
                                            {nodeData.title}
                                        </Text>
                                    </Tooltip>
                                }}
                                onCheck={(checkedKeys, action) => {
                                    let selectedKeys = [...securityKeysChecked];
                                    if (action.checked) {
                                        selectedKeys = [...selectedKeys, action.node.key];
                                    } else {
                                        selectedKeys = selectedKeys.filter(checkKey => checkKey !== action.node.key)
                                    }
                                    dispatch(setSecurityKeysChecked(selectedKeys));
                                }}
                                onExpand={(expandedKeys) => {
                                    setKeysExpanded(() => {
                                        return [...expandedKeys];
                                    });
                                }}
                                treeData={treeData}
                            /> : <Text data-testid="hi-metadata-security-text">No entities found.</Text>}
                        </Form.Item>
                    </Form>
                </Modal>
            </>) : (
            <ValidatedTable setIsInfoShow={setIsInfoShow} setShowContent={setShowContent} setIsApplyDisabled={setIsApplyDisabled} setFormData={setFormData} securityKeysChecked={securityKeysChecked} addOneMoreSecurity={addOneMoreSecurity} selectedTableOrColumnKey={selectedTableOrColumnKey} />
        )
        ) : <></>) : <>
            <Empty className='edit-section-lazy-loading-spinner' image={null} description={null}>
                <Spin indicator={antIcon} />;
            </Empty></>
    )
}
