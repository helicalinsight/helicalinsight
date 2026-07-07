import { InfoCircleOutlined, CloseOutlined } from '@ant-design/icons';
import Editor from '@monaco-editor/react';
import { Button, Carousel, Drawer, Row, Space, Tooltip, Typography } from 'antd';
import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { updateCustomChart } from '../../../../redux/actions/hreport.actions';
import { generateReport } from '../../utils/base';
import { monacoReactCodeEditorOptions } from '../../utils/constants';
import CustomChartInfo from './custom-chart-info';

const { Text } = Typography

const CustomChartEditor = ({ open, code, report, getApi = () => { } }) => {
    const [slide, setSlide] = useState(1)
    const dispatch = useDispatch();
    const carouselRef = React.useRef(null);

    const handleClose = () => {
        dispatch(updateCustomChart({ drawer: false }));
        setSlide(1)
    };

    const handleReset = () => {
        dispatch(updateCustomChart({ code: "" }));
    };

    const handleApply = () => {
        if (!code?.length) return;
        const customChartOptions = { applied: true, drawer: false }
        dispatch(updateCustomChart(customChartOptions));
        generateReport(
            { ...report, customChart: { ...report?.customChart, ...customChartOptions }, selectedType: '' },
            dispatch,
            getApi
        );
    };

    const handleChange = (value) => {
        dispatch(updateCustomChart({ code: value }));
    };

    const handleSave = () => { };

    const getTitle = (currentSlide) => {
        let title = currentSlide === 1 ? "VF Editor" : "VF Editor Info";

        return (
            <Space className='custom-chart-title'>
                <Text>{title}</Text>
                <Tooltip title={currentSlide === 1 ? "Click to see the info" : "Click to go back to editor"}>
                    <span className="custom-chart-info-icon" data-testid="custom-chart-info-icon" onClick={() => {
                        if (currentSlide === 1) {
                            if (carouselRef && carouselRef.current) {
                                carouselRef.current.next();
                            }
                            setSlide(2);
                        } else {
                            if (carouselRef && carouselRef.current) {
                                carouselRef.current.prev();
                            }
                            setSlide(1);
                        }
                    }}>
                        {currentSlide === 1 ? <InfoCircleOutlined /> : <CloseOutlined />}
                    </span>
                </Tooltip>
            </Space>
        );
    };

    const getFooter = () => {
        return (
            <Row justify="end" data-testid="custom-chart-footer">
                {/* <Button onClick={handleSave} type="primary" data-testid="custom-chart-save-button">
                    Save
                </Button> */}
                <Space size={16}>
                    <Button onClick={handleApply} type="primary" data-testid="custom-chart-apply-button">
                        Apply
                    </Button>
                    <Button onClick={handleReset} data-testid="custom-chart-reset-button">Reset</Button>
                </Space>
            </Row>
        )
    }

    return (
        <Drawer
            title={getTitle(slide)}
            placement="right"
            width="45%"
            onClose={handleClose}
            visible={open}
            footer={slide === 2 ? null : getFooter()}
            className='hi-custom-chart-editor-drawer'
            closeIcon={<CloseOutlined data-testid="hi-custom-chart-editor-close-icon" />}
        >
            <Carousel ref={carouselRef} dots={false}>
                <div className="hi-custom-chart-editor-container" data-testid="hi-custom-chart-editor-container">
                    <Editor
                        value={code}
                        onChange={handleChange}
                        options={monacoReactCodeEditorOptions}
                        height="70vh"
                        defaultLanguage="javascript"
                    />
                    {!code?.length ? <div className='hi-custom-chart-placeholder-container'>
                        <span className='placeholder-text'>Please write your script here</span>
                    </div> : null}
                </div>
                {slide === 2 && <div>
                    <CustomChartInfo report={report} />
                </div>}
            </Carousel>
        </Drawer>
    );
};

export default CustomChartEditor
