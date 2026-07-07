import { MenuOutlined } from '@ant-design/icons';
import { Skeleton, Table } from 'antd';
import { arrayMoveImmutable } from 'array-move';
import { isEqual } from 'lodash';
import React from 'react';
import { SortableContainer, SortableElement, SortableHandle } from 'react-sortable-hoc';
import { VList } from "virtuallist-antd";
import './index.scss';

const DragHandle = SortableHandle(() => <MenuOutlined style={{ cursor: 'grab', color: '#999' }} />);

const SortableItem = SortableElement(props => <tr {...props} />);
const SortableBody = SortableContainer(props => <tbody {...props} />);
const renderSkeleton = () => <div className='hi-p10'><Skeleton title={true} active paragraph={false} /></div>;

class SortableTable extends React.Component {
    state = {
        dataSource: this.props.dataSource,
        selectedKeys: [],
        selectedIndexes: [],
    };

    onSortEnd = ({ oldIndex, newIndex }) => {
        const { dataSource } = this.state;
        if (oldIndex !== newIndex) {
            let newData = arrayMoveImmutable([].concat(dataSource), oldIndex, newIndex).filter(
                el => !!el,
            );
            newData = newData.map((join, index) => {
                join = { ...join, index: index + 1 }
                return join
            })
            newData = newData.sort((a, b) => a.index - b.index)
            this.setState({ dataSource: newData });
            this.props.updateJoinsToRedux([...(newData || []), ...(this.props.joins.filter(ele => ele.action === 'delete') || [])])
        }
    };

    componentDidUpdate() {
        if (!isEqual(this.props.dataSource, this.state.dataSource)) {
            this.setState({ dataSource: this.props.dataSource });
        }

    }
    DraggableContainer = props => (
        <SortableBody
            useDragHandle
            disableAutoscroll
            helperClass="row-dragging"
            onSortEnd={this.onSortEnd}
            {...props}
        />
    );

    DraggableBodyRow = ({ ...restProps }) => {
        const { dataSource } = this.state;
        // function findIndex base on Table rowKey props and should always be a right array index
        const index = dataSource.findIndex(x => x.index === restProps['data-row-key']);
        return <SortableItem index={index} {...restProps} />;
    };
    render() {
        let dataSource = this.state.dataSource;
        if (dataSource) {
            dataSource = (dataSource || []).sort((a, b) => a.index - b.index)
        }
        let __keys = (dataSource || []).map(join => {
            if ((this.state?.selectedKeys || []).indexOf(join.uuid) !== -1) {
                return join.index
            }
            return false
        }).filter(Boolean)
        let properties = {}
        let calculatedHeight = this.props.calculatedHeight || 450
        if (dataSource?.length > 5) {
            properties = {
                scroll: {
                    y: calculatedHeight, x: "100%"
                },
                components:
                    () => {
                        return VList({ height: calculatedHeight, resetTopWhenDataChange: false, vid: 'metadata-page-datasource-section-joins' });
                    }
            }
        }

        // }
        return (
            <div className="hi-sortable-table">
                <Table
                    pagination={false}
                    dataSource={this.props.loading ? [...Array(6).map(() => { })] : dataSource}
                    rowSelection={{
                        selectedRowKeys: __keys,
                        onChange: (selectedRowKeys, selectedRows) => {
                            let keys = []
                            let result = selectedRows.map(row => {
                                keys.push(row.index)
                                return row.uuid
                            })
                            this.props.setSelectedJoins(result)
                            this.setState({ selectedKeys: result, selectedIndexes: keys }, () => {
                            })
                        }
                    }}
                    {...properties}
                    size="small"
                    columns={
                        this.props.loading ? this.props.columns.map(d => ({ ...d, render: renderSkeleton })) :
                            [
                                {
                                    title: 'Sort',
                                    dataIndex: 'sort',
                                    width: 50,
                                    className: 'drag-visible',
                                    render: () => <DragHandle />,
                                },
                                Table.SELECTION_COLUMN,
                                ...this.props.columns
                            ]
                    }
                    rowKey="index"
                    components={{
                        body: {
                            wrapper: this.DraggableContainer,
                            row: this.DraggableBodyRow,
                        },
                    }}
                />
            </div>
        );
    }
}

export default SortableTable