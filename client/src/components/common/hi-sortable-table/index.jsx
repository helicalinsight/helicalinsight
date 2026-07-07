import React from 'react';
import './index.scss';
import { Table } from 'antd';
import { SortableContainer, SortableElement, SortableHandle } from 'react-sortable-hoc';
import { MenuOutlined } from '@ant-design/icons';
import { arrayMoveImmutable } from 'array-move';
import {isEqual} from 'lodash';

const DragHandle = SortableHandle(() => <MenuOutlined style={{ cursor: 'grab', color: '#999' }} />);


const SortableItem = SortableElement(props => <tr {...props} />);
const SortableBody = SortableContainer(props => <tbody {...props} />);

class SortableTable extends React.Component {
    state = {
        dataSource: this.props.dataSource,
    };

    onSortEnd = ({ oldIndex, newIndex }) => {
        const { dataSource } = this.state;
        if (oldIndex !== newIndex) {
            let newData = arrayMoveImmutable([].concat(dataSource), oldIndex, newIndex).filter(
                el => !!el,
            );
            newData = newData.map((join, index) => {
                join.index = index + 1
                return join
            })
            // this.props.onSortEnd(newData)
            newData = newData.sort((a,b) => a.index - b.index)
            console.log('Sorted items: ', newData);
            this.setState({ dataSource: newData });
        }
    };

    componentDidUpdate(){
        console.log('in componentDidUpdate6 -- sortable table', !isEqual(this.props.dataSource, this.state.dataSource))
        if(!isEqual(this.props.dataSource, this.state.dataSource)){
            this.setState({ dataSource: this.props.dataSource }, () => {console.log('in state updated')});
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

    DraggableBodyRow = ({ className, style, ...restProps }) => {
        const { dataSource } = this.state;
        // function findIndex base on Table rowKey props and should always be a right array index
        const index = dataSource.findIndex(x => x.index === restProps['data-row-key']);
        return <SortableItem index={index} {...restProps} />;
    };

    render() {
        const { dataSource } = this.state;
        console.log('in hi -sortable table render', dataSource);

        return (
            <div className="hi-sortable-table">
                <Table
                    pagination={false}
                    // dataSource={this.props.dataSource}
                    dataSource={dataSource}
                    rowSelection={{}}
                    // dataSource = {}
                    // columns={this.props.columns}
                    columns={
                        [
                            {
                                title: 'Sort',
                                dataIndex: 'sort',
                                width: 30,
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