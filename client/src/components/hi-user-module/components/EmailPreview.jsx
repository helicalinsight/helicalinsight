import { Button, Col, Drawer, Row, Spin } from 'antd';
import React, { useState } from 'react';

const isHTML = (content) => {
    const doc = new DOMParser().parseFromString(content, "text/html");
    return Array.from(doc.body.childNodes).some(node => node.nodeType === 1);
};

const EmailPreview = (props = {}) => {
    const { onPreviewClick = () => { }, loading = false, data = {} } = props
    const [open, setOpen] = useState(false);

    const handleClick = () => {
        setOpen(true)
        onPreviewClick()
    }

    const renderValue = (key, value) => {
        if (Array.isArray(value)) {
            return (
                value?.length > 1 ? value?.join(", ") : value[0]
            );
        } else if (typeof value === "string" && key.toLowerCase() === "body") {
            return isHTML(value) ? (
                <div
                    dangerouslySetInnerHTML={{ __html: value }}
                />
            ) : (
                <p>{value}</p>
            );
        } else {
            return <span>{value}</span>;
        }
    };

    const renderItem = (key, value, testId) => {
        return <Row style={{ marginBottom: '16px' }} >
            <Col span={4}><strong>{key}</strong> :</Col>
            <Col span={12}>
                {renderValue(key, value)}
            </Col>
        </Row>
    }

    const DisplayContent = () => {
        return (
            <div>
                {renderItem('To', data?.recipients)}
                {renderItem('Subject', data?.subject)}
                {renderItem('Body', data?.body)}
            </div>
        );
    };

    let popoverContent = null;

    if (loading) {
        popoverContent = (
            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <Spin />
            </div>
        )
    } else {
        if (data) {
            popoverContent = <DisplayContent />
        }
    }

    return (
        <div className='hi-email-preview-container'>
            <Drawer
                data-testid="hi-viewer-email-preview"
                title={<span>Email Preview</span>}
                placement="right"
                onClose={() => setOpen(false)}
                visible={open}
                width={500}
                zIndex={999}
                maskClosable={false}
            >
                {popoverContent}
            </Drawer>
            <Button
                type="primary"
                data-testid="hi-viewer-preview-email-button"
                style={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignSelf: 'flex-end'
                }}
                onClick={handleClick}
            >
                Preview
            </Button>
        </div>
    )
}

export default EmailPreview