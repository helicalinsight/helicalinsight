import { Table, message, Popconfirm, Tooltip, Typography, Dropdown, Button, Space, Menu, Input, Skeleton } from 'antd';
import {
	EditOutlined,
	DeleteOutlined,
	PlusCircleOutlined,
	MinusCircleOutlined,
	QuestionCircleOutlined,
	DownOutlined,
	ScheduleOutlined,
	ReloadOutlined,
	SearchOutlined,
	CloseOutlined
} from '@ant-design/icons';
import notify from '../../../../../hi-notifications/notify';
import metadataRequests from '../../../../../../base/requests/metadata.requests';
import Highlighter from 'react-highlight-words';
import { diceStorageMetadataDumpList, loadSkeleton, setDiceStorageFieldSearchText, setDiceStorageSearchedColumn, setDiceStorageTableData, updateDiceStorageTableRow } from '../../../../../../redux/actions/admin.actions';
import moment from "moment";
import LoadingBar from '../../../../../common/components/hi-loading-bar';
import admin from '../../../../../../base/requests/admin.request';

const { Paragraph } = Typography;
const { Search } = Input;

const handleSearch = ({value, confirm, dataIndex, dispatch}) => {
	// handleSearch({value, confirm, dataIndex, dispatch});
	confirm();
	dispatch(setDiceStorageFieldSearchText(value));
	dispatch(setDiceStorageSearchedColumn(dataIndex));
};

const handleReset = ({ confirm, dataIndex, clearFilters, dispatch }) => {
	clearFilters();
	confirm();
	dispatch(setDiceStorageFieldSearchText(""));
	dispatch(setDiceStorageSearchedColumn(dataIndex));
};

const getColumnSearchProps = ({dataIndex, diceStorageFieldSearchText, diceStorageSearchedColumn, dispatch, searchInput}) => ({
	filterDropdown: ({
		setSelectedKeys,
		selectedKeys,
		confirm,
		clearFilters
	}) => {
		// console.log({diceStorageFieldSearchText, diceStorageSearchedColumn})
		return (
			<div
				style={{
					padding: 8
				}}
			>
				<Search
					ref={searchInput}
					value={selectedKeys[0]}
					onChange={(e) =>
						setSelectedKeys(e.target.value ? [e.target.value] : [])
					}
					placeholder={`Search ${dataIndex}`}
					onSearch={(value) => {
						// console.log(value)
						handleSearch({value, confirm, dataIndex, dispatch});
					}}
					enterButton={
						diceStorageFieldSearchText ? (
							<CloseOutlined
								onClick={(e) => {
									handleReset({
										selectedKeys,
										confirm,
										dataIndex,
										clearFilters,
										dispatch
									});
									e.stopPropagation();
								}}
							/>
						) : (
							<SearchOutlined />
						)
					}
				/>
			</div>
		);
	},
	filterIcon: (filtered) => (
		<SearchOutlined
			style={{
				color: filtered ? "#1890ff" : undefined
			}}
		/>
	),
	onFilter: (value, record) =>
		record[dataIndex].toString().toLowerCase().includes(value.toLowerCase()),
	onFilterDropdownVisibleChange: (visible) => {
		if (visible) {
			setTimeout(() => {
				return searchInput.current?.select();
			}, 100);
		}
	},
	render: (text) =>
	diceStorageSearchedColumn === dataIndex ? (
			<Highlighter
				highlightStyle={{
					backgroundColor: "#ffc069",
					padding: 0
				}}
				searchWords={[diceStorageFieldSearchText]}
				autoEscape
				textToHighlight={text ? text.toString() : ""}
			/>
		) : (
			text
		)
});

export const handleDumpRefreshList = ({dispatch, fetchMetadataDumpList, setLoading, apiRef}) => {
	apiRef.current = fetchMetadataDumpList({}, 'adhoc/metadata/listMetadataDump', (res) => {
		dispatch(diceStorageMetadataDumpList({ key: 'metadata', value: res.data }));
		setLoading(false);
	}, (err) => {
		// console.log(err);
		setLoading(false);
	})
}

export const diceStorageColumns = ({ tabName, diceStorageFieldSearchText, diceStorageSearchedColumn, searchInput, dispatch, skeletonRowKeys }) => {
	let columnsData = [],
		columnTitles = [
			'S No',
			`${tabName[0].toUpperCase() + tabName.slice(1)} Title`,
			'Dump Type',
			'Dump Status',
			'Last Updated'
		]; // 'Metadata Name'
	// columnTitles.splice(1, 0, tabName === 'metadata' ? 'Metadata Name' : 'Cube Name');
	columnTitles.forEach((ele) => {
		let obj = {
			title: () => <Tooltip title={ele}>{ele}</Tooltip>,
			dataIndex: ele,
			key: ele,
			className: 'table-ellipsis',
			render: (name, record) => {
				// console.log({name, record})
				return (
					<>
					<Skeleton
						className='dice-storage-skeleton'
						loading={skeletonRowKeys.includes(record.key)}
						title={false}
						paragraph={{
						rows: 1,
						width: "100%"
						}}
					>
						<Tooltip  className='dice-storage-table-row-tooltip' title={[ 'Cube Title', 'Metadata Title' ].includes(ele) ? <table className={`dice-storage-table-popover`}>
								<tbody>
										<tr key={'Name'}>
											<td>{'Name:'}&nbsp;</td>
											<td>{record.fileDetails.name}</td>
										</tr>
										<tr key={'Path'}>
											<td>{'Path:'}&nbsp;</td>
											<td>{record.fileDetails.path}</td>
										</tr>
								</tbody>
							</table> : name}>
							<Paragraph style={{ maxWidth: '80%', marginBottom: 0 }} ellipsis={true}>
								{name}
							</Paragraph>
						</Tooltip>
					</Skeleton>
					</>
				);
			}
		};
		obj = [ 'Cube Title', 'Metadata Title' ].includes(ele) ? { ...getColumnSearchProps({dataIndex: ele, diceStorageFieldSearchText, diceStorageSearchedColumn, searchInput, dispatch}), ...obj } : obj;
		columnsData.push(obj);
	});
	columnsData.push({
		title: () => <Tooltip title="Actions">Actions</Tooltip>, //'Actions',
		dataIndex: 'Actions',
		key: 'Actions',
		// width: 100,
		className: 'actions-col table-ellipsis'
	});
	// console.log(columnsData)
	return columnsData;
};

export const deleteAllConfirm = ({ setSelectedKeys, selectedKeys, dispatch, setSecurityTableData }) => {
	// let nonDeletedRows = securityTableData.filter((ele) => !selectedKeys.includes(ele.key));
	// dispatch(setSecurityTableData(nonDeletedRows));
	// setExpandedKeys((prevVal) => {
	// 	let newExpandedKeys = prevVal.filter((no) => !selectedKeys.includes(no));
	// 	return newExpandedKeys;
	// });
	// setSelectedKeys([]);
	// selectedKeys.length && message.success('Expressions deleted successfully.');
};

const deleteConfirm = ({
	ele,
	dispatch,
	tableData,
	tabName
	// setExpandedKeys,
	// securityTableData
}) => {
	let newTableData = tableData.filter((obj) => obj.key !== ele.key);
	// dispatch(setSecurityTableData(newFormData));
	dispatch(setDiceStorageTableData({ key: tabName, value: newTableData }));
	dispatch(loadSkeleton(ele.key));
	// dispatch(
	// 	saveExpressionData({
	// 		expressionId: ele.expressionId,
	// 		action: 'delete'
	// 	})
	// );
	// notify(dispatch).success({
	// 	type: 'Network Call',
	// 	message: 'Deleted Successfully.'
	// });
};

const deleteCancel = () => {
	// console.log(e);
	// message.error('Click on No');
};

export const handleDump = ({ dumpType = 'sample', fullPath, dispatch, objKey, apiRef }) => {
	// console.log(dumpType);
	// sample FD { "location": "1629715571483", "metadataFileName": "7c436b9a-9e51-4ce0-b7d6-d943be7684bf.metadata", "dumpType": "sample" }
	let fileDtls = fullPath.split('/');
	let fileName = fileDtls.pop();
	let path = fileDtls.join('/');
	if (!(fileName && path)) {
		notify(dispatch).warning({
			type: 'Validation',
			message: 'Please save metadata before dumping'
		});
		return;
	}
	if (![ 'sample', 'deep' ].includes(dumpType)) return;
	let formData = {
		location: path,
		metadataFileName: fileName,
		dumpType
	};
	dispatch(loadSkeleton(objKey));
	apiRef.current = metadataRequests(dispatch).dumpMetadata(
		formData,
		(res) => {
			// notify(dispatch).success({
			// 	type: 'Network Call',
			// 	...res
			// });
			dispatch(loadSkeleton(objKey));
			const extension = res.data.name.split('.').pop();
			let ephocTime = res.data.lastUpdatedTime || Date.now();
			const value = {
				[`${extension === 'metadata'? 'Metadata': 'Cube'} Title`]: res.data.title,
				fileDetails: {
					path: res.data.path,
					extension,
					permissionLevel: "",
					name: res.data.name,
					description: res.data.name,
					lastModified: ephocTime,
					type: 'file',
					title: res.data.title,
					options: {}
				},
				'Dump Status': res.data.status,
				'Last Updated': moment(new Date(JSON.parse(ephocTime))).format("dddd, MMMM Do, YYYY, h:mm:ss a"),
				'Dump Type': res.data.dumpType,
				key: objKey,
				id: res.data.id
			}
			dispatch(updateDiceStorageTableRow({key: extension, value}))
		},
		(err) => {
			dispatch(loadSkeleton(objKey));
			// notify(dispatch).error({
			// 	type: 'Network Call',
			// 	message: err.message
			// });
		}
	);
};

// const handleMenuClick = ({ e, dispatch, fullPath }) => {
// 	// use e.key
// 	handleDump({ dumpType: e.key, fullPath, dispatch });
// };

const dumpMenu = ({ dispatch, fullPath, key: objKey, apiRef }) => (
	<Menu
		onClick={(e) => {
			handleDump({ dumpType: e.key, fullPath, dispatch, objKey, apiRef });
			// handleMenuClick({ e, dispatch, fullPath });
		}}
		items={[
			{
				label: 'Sample Dump',
				key: 'sample'
				//   icon: <UserOutlined />,
			},
			{
				label: 'Deep Dump',
				key: 'deep'
				//   icon: <UserOutlined />,
			}
		]}
	/>
);

const handleCancelDumpService = ({dispatch}) => {
	metadataRequests(dispatch).handleDumpMetadataServiceCancel({}, (res) => {
		console.log(res, 'succ')
	}, (err) => {
		console.log(err, 'err')
	});
}

export const diceStorageDataSource = ({ tabName, tableData = [], searchVal = '', dispatch, apiRef, handleAbort, skeletonRowKeys }) => {
	let data = [];
	const callBack = (prop) => {
		handleCancelDumpService({dispatch});
		// handleAbort(prop);
	}
	tableData.forEach((ele, index) => {
		data.push({
			...ele,
			'S No': index + 1,
			Actions: (
				<>{skeletonRowKeys.includes(ele.key) && <LoadingBar handleClick={callBack} />}
				<div className="table-actions">
					<Dropdown overlay={dumpMenu({ dispatch, fullPath: ele.fileDetails.path, key: ele.key, apiRef })}>
						<Space>
							Create
						</Space>
						{/* <Button>
							<Space>
								Create
								<DownOutlined />
							</Space>
						</Button> */}
					</Dropdown>
					{/* <Tooltip title="Schedule"><ScheduleOutlined /></Tooltip> */}
					{/* <Tooltip title="Refresh"><ReloadOutlined /></Tooltip> */}
					<Popconfirm
						title="Are you sure you want to delete?"
						onConfirm={() =>
							deleteConfirm({
								ele,
								dispatch,
								tableData,
								tabName
								// setSecurityTableData
							})}
						onCancel={deleteCancel}
						okText="Ok"
						cancelText="Cancel"
					>
						<Tooltip title="Delete"><DeleteOutlined className="table-action" /></Tooltip>
					</Popconfirm>
				</div></>
			)
		});
	});

	return data;
};
