import React from "react";
import "./index.scss";
import { Table } from "antd";
import { SortableContainer, SortableElement } from "react-sortable-hoc";
import { arrayMoveImmutable } from "array-move";
import { isEqual } from "lodash";

const SortableItem = SortableElement((props) => <tr {...props} />);
const SortableBody = SortableContainer((props) => <tbody {...props} />);

class SortableTable extends React.Component {
  state = {
    dataSource: this.props.dataSource,
    selectedKeys: [],
    selectedIndexes: [],
  };

  onSortEnd = ({ oldIndex, newIndex }) => {
    const { dataSource } = this.state;
    if (oldIndex !== newIndex) {
      let newData = arrayMoveImmutable(
        [].concat(dataSource),
        oldIndex,
        newIndex
      ).filter((el) => !!el);
      this.props.onSortEnd(newData);
      this.setState({ dataSource: newData });
    }
  };

  componentDidUpdate() {
    if (!isEqual(this.props.dataSource, this.state.dataSource)) {
      this.setState({ dataSource: this.props.dataSource });
    }
  }
  DraggableContainer = (props) => (
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
    const index = dataSource.findIndex(
      (x) => x.index === restProps["data-row-key"]
    );
    return <SortableItem index={index} {...restProps} />;
  };

  render() {
    let dataSource = this.state.dataSource;
    // if (dataSource) {
    //   dataSource = (dataSource || []).sort((a, b) => a.index - b.index);
    // }

    return (
      <div className="hi-sortable-table">
        <Table
        data-testid = "hi-parameter-sortable-table"
          pagination={false}
          dataSource={dataSource}
          showHeader={false}
          columns={[
            // Table.SELECTION_COLUMN,
            ...this.props.columns,
          ]}
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

export default SortableTable;
