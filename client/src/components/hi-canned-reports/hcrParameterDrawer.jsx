import { Button, Drawer, Form, Input, InputNumber, Space, Tooltip } from 'antd';
import { hcrActions } from '../../redux/actions';
import { hcrDSParameter } from './hcr-constants';
import { handleRunQuery } from './hcrHelperMethods';
import requests from '../../base/requests';
import { useEffect } from 'react';
import { InfoCircleOutlined } from '@ant-design/icons';

const { TextArea } = Input;

export default function HcrParameterDrawer({ setIsDrawerOpen, isDrawerOpen, parametersList = [], subDataParametersList = [], Notify, dispatch, reqQuery, selectedDS, dsPaneTypes, resetQueryuuids = () => { } }) {
    const [form] = Form.useForm();
    const { setFieldsValue } = form;
    const onClose = () => {
        setIsDrawerOpen(false);
    };
    const { saveQueryReportState, saveExecuteReportQuery } = requests.cannedReport(dispatch);

    const onFinishFailed = (errorInfo) => {
        // console.log('Failed:', errorInfo);
        Notify.warning({ message: 'Please fill up all the fields' });
    };

    useEffect(() => {
        if (isDrawerOpen) {
            const obj = {};
            reqQuery.parameterList?.forEach(item => {
                obj[item.id] = item.value;
            })
            setFieldsValue(obj);
        }
    }, [isDrawerOpen])

    const onFinish = (parameterObj) => {
        const list = [];
        let parametersMenu = dsPaneTypes.find(ele => ele.dataSourcePane === hcrDSParameter)?.menu;
        if (subDataParametersList?.length) {
            parametersMenu = [...parametersMenu, ...(subDataParametersList)]
        }
        parametersMenu.forEach(para => {
            if (parameterObj[para.id]) {
                list.push({ id: para.id, type: para.type, name: para.name, value: parameterObj[para.id] });
            }
        })
        dispatch(hcrActions.handleEditingDsPaneItem({ dataSourcePane: selectedDS?.dataSourcePane, value: list.filter((p) => !subDataParametersList.find((param) => param.id === p.id)), key: 'parameterList', itemId: reqQuery.id }));
        handleRunQuery({ reqQuery: { ...reqQuery, parameterList: list }, isEditedFile: false, dispatch, selectedDS, saveQueryReportState, saveExecuteReportQuery, Notify, paraList: parametersMenu, resetQueryuuids });
        setIsDrawerOpen(false);
    };

    return <Drawer
        title="Parameter Values"
        onClose={onClose}
        visible={isDrawerOpen}
        size={'small'}
        extra={
            <Space>
                <Button onClick={onClose}>Cancel</Button>
                <Button onClick={form.submit} className='hcr-drawer-ok' htmlType="submit">
                    OK
                </Button>
            </Space>
        }
    >
        <Form
            form={form}
            layout="vertical"
            name="parameterlist-form"
            autoComplete="off"
            // labelCol={{
            //     span: 5,
            // }}
            // wrapperCol={{
            //     span: 20,
            // }}
            style={{
                maxWidth: 300,
            }}
            onFinish={onFinish}
            onFinishFailed={onFinishFailed}
        >
            {
                parametersList.map(para => {
                    return <Form.Item
                        label={
                            <>
                                <b>{para.name} </b>
                                {para?.isSubDSParameter && <Tooltip title={"Sub Dataset parameter"}><InfoCircleOutlined style={{ fontSize: 10, borderRadius: 4, marginLeft: 8, cursor: "pointer" }} /></Tooltip>}
                            </>
                        }
                        name={para.id}
                    // rules={[
                    //     {
                    //         required: true,
                    //         message: 'Please input your value!',
                    //     },
                    // ]}
                    >
                        {para.type.toLowerCase().includes('string') ? <Input /> : (para.type.toLowerCase().includes('integer') ? <InputNumber /> :
                            <TextArea rows={1} />)}
                    </Form.Item>
                })
            }

            {/* <Form.Item
                wrapperCol={{
                    offset: 4,
                    span: 8,
                }}
            >
                <Button type="primary" htmlType="submit">
                    Apply
                </Button>
            </Form.Item> */}
        </Form>
    </Drawer>
}