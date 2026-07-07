import { Col, Row, Space, Tooltip, Typography } from 'antd';
import { HrCubeDrag } from './hr-cube-drag';
import { cloneDeep, isEqual } from 'lodash-es';
import MetadataTable from './table';
import MetadataField from './metadata-field';
import { v4 as uuidv4 } from 'uuid';

const { Text, Paragraph } = Typography;

export function handleCubeToTableDataSource(cubes) {
	let data = [],
		dimensions = [],
		measures = [];
	if ('dimensions' in cubes[0]) {
		dimensions = cubes[0].dimensions.map((ele) => {
			let column = {
				alias: ele.dimensionName,
				fullyQualifiedColumn: `${ele.table.name}.${ele.columnName}`,
				columnId: ele.columnId,
				defaultFunction: ele.column.defaultFunction,
				type: ele.dimensionType
			};
			let rowData = {
				key: ele.id,
				alias: ele.dimensionName,
				dimensionId: ele.id,
				dimensionVisible: ele.visible,
				table: {
					...ele.table,
					id: ele.tableId,
					columns: { [ele.columnName]: { ...column } },
					key: uuidv4()
				},
				tableId: ele.tableId,
				columnName: ele.columnName,
				columnId: ele.columnId,
				column,
				type: ele.dimensionType,
				tables: { [ele.table.name]: { ...ele.table } },
				defaultFunction: ele.column.defaultFunction,
				dataType: Object.values(ele.dimensionType)[0],
				tableName: ele.table.name,
				tableAlias: ele.table.alias
			};
			let { hierarchies } = ele;
			let { levels } = hierarchies[0];
			if (
				levels.length >= 2 ||
				levels[0].columnId !== hierarchies[0].primaryColumnId ||
				levels[0].levelName !== hierarchies[0].hierarchyName
			) {
				rowData.isDimension = true;
				const hierarchyCol = {
					alias: hierarchies[0].hierarchyName,
					fullyQualifiedColumn: `${hierarchies[0].table.name}.${hierarchies[0].columnName}`,
					columnId: hierarchies[0].primaryColumnId,
					defaultFunction: hierarchies[0].column.defaultFunction,
					type: hierarchies[0].hierarchyType
				};
				rowData.children = [
					{
						isHierarchy: true,
						key: hierarchies[0].id,
						alias: hierarchies[0].hierarchyName,
						hierarchyId: hierarchies[0].id,
						hierarchyVisible: hierarchies[0].visible,
						tableId: hierarchies[0].tableId,
						columnId: hierarchies[0].primaryColumnId,
						table: {
							...hierarchies[0].table,
							id: hierarchies[0].tableId,
							columns: { [hierarchies[0].columnName]: { ...hierarchyCol } },
							key: uuidv4()
						},
						tables: { [hierarchies[0].table.name]: { ...hierarchies[0].table } },
						columnName: hierarchies[0].columnName,
						defaultFunction: hierarchies[0].column.defaultFunction,
						column: { ...hierarchyCol },
						type: hierarchies[0].hierarchyType,
						dataType: Object.values(hierarchies[0].hierarchyType)[0],
						tableName: hierarchies[0].table.name,
						tableAlias: hierarchies[0].table.alias,
						children: levels.map((level) => {
							const levelCol = {
								alias: level.levelName,
								fullyQualifiedColumn: `${level.table.name}.${level.columnName}`,
								columnId: level.columnId,
								defaultFunction: level.column.defaultFunction,
								type: level.levelType
							};
							return {
								key: level.id,
								isLevel: true,
								alias: level.levelName,
								levelId: level.id,
								levelVisible: level.visible,
								tableId: level.tableId,
								columnId: level.columnId,
								table: {
									...level.table,
									id: level.tableId,
									columns: { [level.columnName]: { ...levelCol } },
									key: uuidv4()
								},
								tables: { [level.table.name]: { ...level.table } },
								columnName: level.columnName,
								column: { ...levelCol },
								type: level.levelType,
								dataType: Object.values(level.levelType)[0],
								defaultFunction: level.column.defaultFunction,
								tableName: level.table.name,
								tableAlias: level.table.alias
							};
						})
					}
				];
			} else {
				rowData = {
					...rowData,
					isAll: true,
					tableId: hierarchies[0].tableId,
					columnId: hierarchies[0].primaryColumnId,
					hierarchyId: hierarchies[0].id,
					levelId: levels[0].id
				};
			}
			return rowData;
		});
		data = [ ...dimensions ];
	}
	if ('measures' in cubes[0]) {
		measures = cubes[0].measures.map((ele) => {
			const measureCol = {
				alias: ele.measureName,
				fullyQualifiedColumn: `${ele.table.name}.${ele.columnName}`,
				columnId: ele.columnId,
				defaultFunction: ele.column.defaultFunction,
				type: ele.measureType
			};
			let rowData = {
				key: ele.id,
				isMeasure: true,
				alias: ele.measureName,
				measureId: ele.id,
				measureVisible: ele.visible,
				tableId: ele.tableId,
				columnId: ele.columnId,
				columnName: ele.columnName,
				table: {
					...ele.table,
					id: ele.tableId,
					columns: { [ele.columnName]: { ...measureCol } },
					key: uuidv4()
				},
				tables: { [ele.table.name]: { ...ele.table } },
				column: { ...measureCol },
				type: ele.measureType,
				dataType: Object.values(ele.measureType)[0],
				defaultFunction: ele.column.defaultFunction,
				tableName: ele.table.name,
				tableAlias: ele.table.alias
			};
			return rowData;
		});
		data = [ ...data, ...measures ];
	}
	return data;
}

export function hrCubeColumns({}) {
	let columnsData = [];
	[ 'alias' ].forEach((ele) => {
		columnsData.push({
			title: ele,
			dataIndex: ele,
			key: ele,
			className: 'table-ellipsis',
			render: (name, record) => {
				// console.log(name, record);
				return <MetadataField item={record} isHrSidebarCube={true} />;
			}
		});
	});
	return columnsData;
}

export const handleHrCubeDataSource = ({ datasourceData, searchText = '' }) => {
	let dupDataSource = cloneDeep(datasourceData),
		data = [];
	data = dupDataSource.filter((rec) => {
		if (rec.alias.toLowerCase().includes(searchText.toLowerCase())) {
			return true;
		} else if (rec.children) {
			let childData = handleHrCubeDataSource({ datasourceData: rec.children, searchText });
			if (childData.length) {
				rec.children = childData;
				return true;
			}
		}
		return false;
	});
	// console.log(data);
	return data;
};
