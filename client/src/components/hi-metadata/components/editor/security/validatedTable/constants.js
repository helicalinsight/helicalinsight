import { Table, message, Popconfirm, Tooltip, Typography } from 'antd';

import {
	EditOutlined,
	DeleteOutlined,
	PlusCircleOutlined,
	MinusCircleOutlined,
	QuestionCircleOutlined
} from '@ant-design/icons';
import {
	metadataSecurityDeleteSelectedAll,
	saveExpressionData,
	setSecurityEdit,
	setSecurityTableData,
	setShowValidatedTable
} from '../../../../../../redux/actions';
import notify from '../../../../../hi-notifications/notify';
import constants from '../../../../../../constants';
import { mapTablesColumnsWithUniqueKey } from '../securityHelperMethods';
import PopconfirmBody from '../../../../../common/components/Hi-Popconfirm';

const { Paragraph } = Typography;

export const deleteAllConfirm = ({
	setSelectedKeys,
	selectedKeys,
	dispatch,
	securityTableData,
	setSecurityTableData,
	setExpandedKeys
}) => {
	const deletedExps = [];
	let nonDeletedRows = securityTableData.filter((ele) => {
		if(selectedKeys.includes(ele.key)) {
			deletedExps.push({
				expressionId: ele.expressionId,
				action: 'delete'
			});
			return false;
		}
		return true
	});
	dispatch(setSecurityTableData(nonDeletedRows));
	setExpandedKeys((prevVal) => {
		let newExpandedKeys = prevVal.filter((no) => !selectedKeys.includes(no));
		return newExpandedKeys;
	});
	dispatch(metadataSecurityDeleteSelectedAll(deletedExps));
	selectedKeys.length && message.success('Expressions deleted successfully.');
	setSelectedKeys([]);
};

const deleteConfirm = ({
	ele,
	setSelectedKeys,
	dispatch,
	setSecurityTableData,
	setExpandedKeys,
	securityTableData
}) => {
	setSelectedKeys((prevVal) => {
		let newSelectedKeys = prevVal.filter((no) => no !== ele.key);
		return newSelectedKeys;
	});
	let newFormData = securityTableData.filter((obj) => obj.key !== ele.key);
	dispatch(setSecurityTableData(newFormData));
	setExpandedKeys((prevVal) => {
		let newExpandedKeys = prevVal.filter((no) => no !== ele.key);
		return newExpandedKeys;
	});
	dispatch(
		saveExpressionData({
			expressionId: ele.expressionId,
			action: 'delete'
		})
	);
	// message.success('Expression deleted successfully.');
	notify(dispatch).success({
		type: 'Network Call',
		message: 'Expression Deleted Successfully.'
	});
};

const deleteCancel = () => {
	// console.log(e);
	// message.error('Click on No');
};

export const handleExpressionTabColDelete = ({mappedTablesColumns, record }) => {
	let namesSplitArr = record['Entity Names'].split(',');
	return record.securityKeysToBeCheck.some(id => {
		let aliasNameObj = mappedTablesColumns.get(id);
		if (record['Expression Type'] !== 'column') { 
			if(!(aliasNameObj && namesSplitArr.includes(aliasNameObj.tableAlias))) {
				return true;
			}
		} else {
			if(!(aliasNameObj && namesSplitArr.includes(`${aliasNameObj.tableAlias}.${aliasNameObj.columnAlias}`))) {
				return true; // deleted
			}
		}
		return false;
	})
}

export const columns = ({ tables, getSecurityTableData, mode }) => {
	let columnsData = [ Table.SELECTION_COLUMN ];
	[ 'Expression Name', 'Entity Names', 'Expression Type', 'Access Type', 'Execution Type' ].forEach((ele) => {
		columnsData.push({
			title: () => <Tooltip mouseEnterDelay={constants.mouseEnterDelay} title={ele}>{ele}</Tooltip>,
			dataIndex: ele,
			key: ele,
			className: 'table-ellipsis',
			render: (name, record) => {
				let tooltipItems = ['Table Name', 'Alias', 'Datasource', 'Id', 'Original'];
				if (record['Expression Type'] === 'column') { 
					tooltipItems.splice(1,0,'Column Name');
				}
				let divStyle = {};
				let isTabColDelete = false;
				if (ele === 'Entity Names') {
					const mappedTablesColumns = mapTablesColumnsWithUniqueKey({tables, mode})
					isTabColDelete = handleExpressionTabColDelete({mappedTablesColumns, record });
					if (isTabColDelete) {
						divStyle = {
							backgroundColor: '#F2DEDE',
							position: 'absolute',
							inset: 0,
							display: 'flex',
							alignItems: 'center',
							justifyContent: 'space-between'
						};
					}
				}
				// if(tooltipInfoObj['Original']) {
				// 	tooltipItems.push('Original')
				// }
				return (
					<div style={divStyle}>
						<Tooltip
						// visible= {true}
						// placement= 'top'
						overlayClassName= 'security-validateTable-tooltip'
						// overlayStyle = {{height: 20}}
							// placement="top"
							title={ele === 'Entity Names' ?  <table className='security-validate-tooltip-table'>
							<tbody>
								 <tr>
								 {tooltipItems.map(tooltipItem => <th>{tooltipItem}</th>)}
								</tr>
								{record.tooltipInfo?.map(tooltipInfoObj => <tr key={tooltipInfoObj?.key}>
									{tooltipItems.map(tooltipItem => <td key={tooltipInfoObj?.key}>{tooltipInfoObj[tooltipItem === 'Table Name' ? (record['Expression Type'] === 'column' ? 'Table' : 'Name') : (tooltipItem === 'Column Name' ? 'Name' : tooltipItem)] || (tooltipItem === 'Original' ? tooltipInfoObj['Name'] : '')}</td>)}
								</tr>)}
							</tbody>
						</table> :  name}
							//color="#000"
							// overlayInnerStyle={{ height: "100%", fontSize: 14 }}
						>
							<Paragraph style={{ maxWidth: 120, marginBottom: 0 }} ellipsis={true}>
								{name}
							</Paragraph>
						</Tooltip>
						{isTabColDelete && (
							<Tooltip
								placement="top"
								title="The following table/column is not included in the metadata panel, Please include it or discard this expression."
							>
								<QuestionCircleOutlined />
							</Tooltip>
						)}
					</div>
				);
			}
		});
	});
	columnsData.push({
		title: () => <Tooltip mouseEnterDelay={constants.mouseEnterDelay} title="Actions">Actions</Tooltip>, //'Actions',
		dataIndex: 'Actions',
		key: 'Actions',
		width: 100,
		className: 'actions-col table-ellipsis'
		// ellipsis: true
	});
	return columnsData;
};

export const validatedTableData = ({
	expandedKeys,
	securityTableData = [],
	setExpandedKeys,
	setIsApplyDisabled,
	setFormData,
	searchVal = '',
	filterbyData = [],
	setSelectedKeys,
	dispatch,
	edit, isValidatedTableShow, isApplyDisabled, tables, mode
}) => {
	// return [];
	let data = [];
	let filter = filterbyData.map((ele) => {
		if (ele.isChecked) {
			return ele.value.toLowerCase();
		}
	});
	securityTableData.forEach((ele) => {
		(Object.values(ele || {}).some((objVal) => objVal.toString().toLowerCase().match(searchVal.toLowerCase())) ||
			searchVal === '') &&
			filter.includes(ele['Expression Type'].toLowerCase()) &&
			data.push({
				// key: ele.key,
				...ele,
				Actions: (
					<div className="table-actions">
						{expandedKeys.includes(ele.key) ? (
							<MinusCircleOutlined
								className="table-action"
								onClick={() => {
									setExpandedKeys((prev) => {
										return prev.filter((no) => no !== ele.key);
									});
								}}
							/>
						) : (
							<PlusCircleOutlined
								className="table-action"
								onClick={() => {
									setExpandedKeys((prev) => {
										return [ ...prev, ele.key ];
									});
								}}
							/>
						)}
						<EditOutlined
							onClick={() => {
								const newRef = {...ele};
								const mappedTablesColumns = mapTablesColumnsWithUniqueKey({tables, mode})
								let namesSplitArr = newRef['Entity Names'].split(',');
								newRef.securityKeysToBeCheck = newRef.securityKeysToBeCheck.filter(id => {
									let aliasNameObj = mappedTablesColumns.get(id);
									if (newRef['Expression Type'] !== 'column') { 
										if(!(aliasNameObj && namesSplitArr.includes(aliasNameObj.tableAlias))) {
											return false; // removing deleted securityKeysToBeCheck keys
										}
									} else {
										if(!(aliasNameObj && namesSplitArr.includes(`${aliasNameObj.tableAlias}.${aliasNameObj.columnAlias}`))) {
											return false; 
										}
									}
									return true;
								})
								dispatch(setFormData(newRef));
								isValidatedTableShow && dispatch(setShowValidatedTable(false));
								!edit && dispatch(setSecurityEdit(true));
								!isApplyDisabled && dispatch(setIsApplyDisabled(true));
								// dispatch(setSelectedTableOrColumnKey({}));
							}}
							className="table-action"
						/>
						<Popconfirm
						 title ={<PopconfirmBody intent="delete" description={"Are you sure you want to delete security expression? You cannot undo the action."} />}
							onConfirm={() =>
								deleteConfirm({
									ele,
									setSelectedKeys,
									dispatch,
									securityTableData,
									setSecurityTableData,
									setExpandedKeys
								})}
							onCancel={deleteCancel}
							okText="Ok"
							cancelText="Cancel"
							placement='left'
						>
							<DeleteOutlined className="table-action" />
						</Popconfirm>
					</div>
				)
			});
	});

	return data;
};
