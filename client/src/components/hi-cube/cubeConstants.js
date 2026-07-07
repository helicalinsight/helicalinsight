import moment from 'moment';
import { useEffect, useRef, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { Menu, Input,Tooltip  } from 'antd';
import {
	setCubeFieldsData,
	setCubeInintialList,
	setCubeMode,
	setCubeState,
	// setCubeTableDeleteKeys,
	setCubeTableMode,
	modifyHierarchy,
	deleteHierarchy,
	setCubeFieldsDataBackup,
	updateFieldValues
} from '../../redux/actions/cube.actions';
import { CheckOutlined,InfoCircleOutlined,CloseOutlined  } from '@ant-design/icons';
import { getCubeEditorTooltipText } from './cubeEditorTooltips';
import { useCubeEditorBindings } from './cubeEditorContext';

const { TextArea } = Input;
function CubeFieldSemanticLabel({ label }) {
	const { variant } = useCubeEditorBindings();
	const tooltipTitle = getCubeEditorTooltipText(label, variant);
	return (
		<span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
			<label style={{ fontSize: 11, fontWeight: 600 }}>{label}</label>
			{tooltipTitle ? (
				<Tooltip
					title={<div style={{ whiteSpace: 'pre-wrap' }}>{tooltipTitle}</div>}
					placement="top"
				>
					<InfoCircleOutlined
						className="cube-info-icon"
						style={{ fontSize: 11 }}
						onClick={(e) => e.stopPropagation()}
					/>
				</Tooltip>
			) : null}
		</span>
	);
}

export const CUBE_FIELD_SEMANTIC_TEXT_FIELDS = [
	{ fieldName: 'formula', label: 'Formula', placeholder: 'Add Formula. Ex: [Measures].' },
	{ fieldName: 'filter', label: 'Filter (optional)', placeholder: "Add filter. Ex: status = 'completed'." },
	{ fieldName: 'example', label: 'Example', placeholder: 'Add Example. Ex: term -> column or value.' },
	{ fieldName: 'description', label: 'Description', placeholder: 'Add Description. Ex:Business description.' },
];

export const CUBE_FIELD_SEMANTIC_MENU_KEY = 'semantic-fields';

export const CUBE_FIELD_MENU_TEXT_KEYS = [CUBE_FIELD_SEMANTIC_MENU_KEY];

const getSemanticDraftFromRecord = (record) =>
	CUBE_FIELD_SEMANTIC_TEXT_FIELDS.reduce((acc, { fieldName }) => {
		acc[fieldName] = record[fieldName] ?? '';
		return acc;
	}, {});
function CubeFieldSemanticMenuPanel({ draft, onDraftChange, disabled = false }) {
  const { variant } = useCubeEditorBindings();
  const clearFieldTooltip = getCubeEditorTooltipText("Clear field values", variant);
  const stopMenuEvent = (e) => e.stopPropagation();
  return (
    <div
      className="cube-field-semantic-menu-panel"
      onClick={stopMenuEvent}
      onMouseDown={stopMenuEvent}
      onKeyDown={stopMenuEvent}
    >
      {CUBE_FIELD_SEMANTIC_TEXT_FIELDS.map(({ fieldName, label, placeholder }) => (
        <div key={fieldName} style={{ marginBottom: 8 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 2 }}>
            <CubeFieldSemanticLabel label={label} />
            {!disabled && draft[fieldName] && (
              <Tooltip title={clearFieldTooltip} placement="right">
                <CloseOutlined
                  style={{ fontSize: 10 }}
                  onClick={(e) => {
                    e.stopPropagation();
                    onDraftChange({ ...draft, [fieldName]: '' });
                  }}
                />
              </Tooltip>
            )}
          </div>
          <TextArea
            disabled={disabled}
            value={draft[fieldName] ?? ''}
            placeholder={placeholder}
            rows={2}
            onChange={(e) => onDraftChange({ ...draft, [fieldName]: e.target.value })}
          />
        </div>
      ))}
    </div>
  );
}

export function commitCubeFieldSemanticDraft({ dispatch, record, draft, variant = 'cube' }) {
	if (record.isHierarchyChild && variant !== 'agent') {
		return;
	}
	CUBE_FIELD_SEMANTIC_TEXT_FIELDS.forEach(({ fieldName }) => {
		const nextValue = draft[fieldName] ?? '';
		const prevValue = record[fieldName] ?? '';
		if (nextValue !== prevValue) {
			dispatch(
				updateFieldValues({
					updateName: fieldName,
					checkVal: nextValue,
					recordKey: record.key,
					isHierarchyChild: record.isHierarchyChild,
					hierarchyKey: record.parentKey,
				}),
			);
		}
	});
}

function buildCubeFieldsMenuItems({
	dispatch,
	record,
	hierarchyData,
	semanticDraft,
	onSemanticDraftChange,
	semanticFieldsDisabled = false,
	variant = "cube",
}) {
	const removeFieldTooltip = getCubeEditorTooltipText("Remove field", variant);
	let menu = record.isHierarchy
    ? [
        {
          label: "Delete without columns",
          key: "Delete without columns",
          onClick: () => {
            dispatch(
              modifyHierarchy({
                record,
                step: "deleteHierarchyWithOutColumns",
              }),
            );
          },
        },
        {
          label: "Delete with columns",
          key: "Delete with columns",
          onClick: () => {
            dispatch(
              modifyHierarchy({ record, step: "deleteHierarchyWithColumns" }),
            );
          },
        },
      ]
    : record.isHierarchyChild
    ? [
        {
          label: "Remove from hierarchy",
          key: "Remove from hierarchy",
          onClick: () => {
            dispatch(modifyHierarchy({ record, step: "removeFromHierarchy" }));
          },
        },
        // { item: 'Add to an existing Hierarchy' },
        {
          label: "Delete",
          key: "deleteFromHierarchy",
          onClick: () => {
            dispatch(modifyHierarchy({ record, step: "deleteFromHierarchy" }));
          },
        },
      ]
    : [
        // {
        // 	label: 'Add to new hierarchy',
        // 	key: 'Add to new hierarchy',
        // 	onClick: () => {
        // 		dispatch(modifyHierarchy({ record, step: 'addToNewHierarchy' }));
        // 	}
        // },
        // { item: 'Add to an existing Hierarchy' },
        {
          label: (
            <span
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                gap: 8,
              }}
            >
              Delete
              <Tooltip
                title={removeFieldTooltip}
                placement="right"
              >
                <InfoCircleOutlined
                  style={{ fontSize: 12 }}
                  onClick={(e) => e.stopPropagation()}
                />
              </Tooltip>
            </span>
          ),
          key: "Delete",
          onClick: () => {
            dispatch(modifyHierarchy({ record, step: "deleteRow" }));
          },
        },
      ];

	if (!record.isHierarchy) {
		menu.push({
			key: CUBE_FIELD_SEMANTIC_MENU_KEY,
			label: (
				<CubeFieldSemanticMenuPanel
					draft={semanticDraft}
					onDraftChange={onSemanticDraftChange}
					disabled={semanticFieldsDisabled}
				/>
			),
		});
	}
	if (!record.isHierarchyChild && (record.isHierarchy || record.isDimensionCheck)) {
		// if (record.measure.isMeasureCheck) {
		// 	menu.shift();
		// }
		// let isHierarchy = record.isHierarchy;
		let existingHierarchyChildren = hierarchyData.hierarchyList.reduce((acc, ele) => {
			if(ele.hierarchyName !== record.fields) {
				acc.push({label: ele.hierarchyName,
				key: ele.hierarchyKey,
				onClick: () => {
					dispatch(
						modifyHierarchy({
							selectedHierarchy: ele.hierarchyName,
							record,
							step: 'addToExistingHierarchy'
						})
					);
				}})
			}
			return acc;
		}, [])
		if (hierarchyData.isHierarchyPresent && (record.isDimensionCheck || record.isHierarchy) && existingHierarchyChildren.length) {
			menu.splice(1, 0, {
				label: 'Add to an existing Hierarchy',
				key: 'Add to an existing Hierarchy',
				children: existingHierarchyChildren
			});
		}
	}
	return menu;
}

export function CubeFieldsMenu({ dispatch, record, hierarchyData, semanticCommitsRef }) {
	const { variant } = useCubeEditorBindings();
	const semanticDraftRef = useRef(getSemanticDraftFromRecord(record));
	const [semanticDraft, setSemanticDraft] = useState(() => semanticDraftRef.current);

	const updateSemanticDraft = (nextDraft) => {
		semanticDraftRef.current = nextDraft;
		setSemanticDraft(nextDraft);
	};

	useEffect(() => {
		const nextDraft = getSemanticDraftFromRecord(record);
		semanticDraftRef.current = nextDraft;
		setSemanticDraft(nextDraft);
	}, [
		record.key,
		record.parentKey,
		record.isHierarchyChild,
		...CUBE_FIELD_SEMANTIC_TEXT_FIELDS?.map(({ fieldName }) => record[fieldName]),
	]);

	useEffect(() => {
		if (!semanticCommitsRef) {
			return undefined;
		}
		const commitDraft = () => {
			commitCubeFieldSemanticDraft({
				dispatch,
				record,
				draft: semanticDraftRef.current,
			});
		};
		semanticCommitsRef.current.set(record.key, commitDraft);
		return () => {
			semanticCommitsRef.current.delete(record.key);
		};
	}, [semanticCommitsRef, dispatch, record]);

	const handleMenuClick = (e) => {
		// if (e.key !== 'TextArea' && (e.key !== 'deleteFromHierarchy')) {
		if (
			!CUBE_FIELD_MENU_TEXT_KEYS.includes(e.key) &&
			e.key !== 'deleteFromHierarchy'
		) {
			commitCubeFieldSemanticDraft({
				dispatch,
				record,
				draft: semanticDraftRef.current,
			});
			dispatch(
				updateFieldValues({
					updateName: 'fieldsDropdownOpen',
					checkVal: false,
					recordKey: record.key,
					isHierarchyChild: record.isHierarchyChild,
					hierarchyKey: record.parentKey,
				}),
			);
		}
	};

	const menu = buildCubeFieldsMenuItems({
		dispatch,
		record,
		hierarchyData,
		semanticDraft,
		onSemanticDraftChange: updateSemanticDraft,
		variant,
	});

	return <Menu onClick={handleMenuClick} items={menu} />;
}

export const cubeFieldsMenu = (props) => <CubeFieldsMenu {...props} />;

export const cubeSortMenu = ({ dispatch, record }) => {
	const menu = [
		{
			label: 'Ascending',
			key: 'Ascending',
			onClick: () => {
				dispatch(
					updateFieldValues({
						updateName: 'sort',
						key: 'value',
						value: 'Ascending',
						recordKey: record.key,
						isHierarchyChild: record.isHierarchyChild,
						hierarchyKey: record.parentKey
					})
				);
				// dispatch(modifyHierarchy({ record, step: 'deleteHierarchyWithOutColumns' }));
			}
		},
		{
			label: 'Descending',
			key: 'Descending',
			onClick: () => {
				dispatch(
					updateFieldValues({
						updateName: 'sort',
						key: 'value',
						value: 'Descending',
						recordKey: record.key,
						isHierarchyChild: record.isHierarchyChild,
						hierarchyKey: record.parentKey
					})
				);
			}
		},
		{
			label: 'None',
			key: 'None',
			onClick: () => {
				dispatch(
					updateFieldValues({
						updateName: 'sort',
						key: 'value',
						value: 'None',
						recordKey: record.key,
						isHierarchyChild: record.isHierarchyChild,
						hierarchyKey: record.parentKey
					})
				);
				// dispatch(modifyHierarchy({ record, step: 'deleteHierarchyWithColumns' }));
			}
		}
	];

	// menu.forEach((ele) => {
	// 	if (record.sort.value === ele.label) {
	// 		ele.icon = <CheckOutlined />;
	// 	}
	// });

	return <Menu selectedKeys={[ record.sort.value ]} items={menu} />;
};

export const cubeEditorMeasureData = [
	{ dataType: 'Number', format: [ '0.00', '00.00' ] },
	{ dataType: 'Date', format: [ 'DD-MM-YY', 'YYYY-MM-DD' ] }
];

const cubeMeasureFormats = ({ dispatch, record }) => {
	return cubeEditorMeasureData.reduce((acc, cur) => {
		acc[cur.dataType] = cur.format.map((formatType) => ({
			value: formatType,
			callBack: () => {
				dispatch(
					updateFieldValues({
						updateName: 'measure',
						key: 'Format',
						value: formatType,
						recordKey: record.key,
						isHierarchyChild: record.isHierarchyChild,
						hierarchyKey: record.parentKey
					})
				);
			}
		}));
		return acc;
	}, {});
};

export const cubeMeasureMenu = ({ dispatch, record }) => {
	const menu = [
		{
			label: 'Data Type',
			key: 'Data Type',
			children: cubeEditorMeasureData.map((data) => ({
				label: data.dataType,
				key: data.dataType,
				onClick: () => {
					dispatch(
						updateFieldValues({
							updateName: 'measure',
							key: 'DataType',
							value: data.dataType,
							recordKey: record.key,
							isHierarchyChild: record.isHierarchyChild,
							hierarchyKey: record.parentKey
						})
					);
				}
				// icon: <CheckOutlined />
			}))
		},
		{
			label: 'Format',
			key: 'Format',
			children: cubeMeasureFormats({ dispatch, record })[record.measure.DataType]?.map((ele) => ({
				label: ele.value,
				key: ele.value,
				onClick: ele.callBack
			}))
		}
	];
	// menu.forEach((parent) => {
	// 	parent.children.forEach((ele) => {
	// 		if (record.measure.DataType === ele.label) {
	// 			ele.icon = <CheckOutlined />;
	// 		}
	// 		if (record.measure.Format === ele.label) {
	// 			ele.icon = <CheckOutlined />;
	// 		}
	// 	});
	// });
	return <Menu selectedKeys={[ record.measure.Format, record.measure.DataType ]} items={menu} />;
};

export const cubeAggregationMenu = ({ dispatch, record, hierarchyData }) => {
	const menu = [
		{
			label: 'Avg',
			key: 'Avg',
			onClick: () => {
				dispatch(
					updateFieldValues({
						updateName: 'aggregation',
						key: 'value',
						value: 'Avg',
						recordKey: record.key,
						isHierarchyChild: record.isHierarchyChild,
						hierarchyKey: record.parentKey
					})
				);
				// dispatch(modifyHierarchy({ record, step: 'deleteHierarchyWithColumns' }));
			}
		},
		{
			label: 'Count',
			key: 'Count',
			onClick: () => {
				dispatch(
					updateFieldValues({
						updateName: 'aggregation',
						key: 'value',
						value: 'Count',
						recordKey: record.key,
						isHierarchyChild: record.isHierarchyChild,
						hierarchyKey: record.parentKey
					})
				);
				// dispatch(modifyHierarchy({ record, step: 'deleteHierarchyWithColumns' }));
			}
		},
		{
			label: 'Distinct Count',
			key: 'Distinct Count',
			onClick: () => {
				dispatch(
					updateFieldValues({
						updateName: 'aggregation',
						key: 'value',
						value: 'Distinct Count',
						recordKey: record.key,
						isHierarchyChild: record.isHierarchyChild,
						hierarchyKey: record.parentKey
					})
				);
				// dispatch(modifyHierarchy({ record, step: 'deleteHierarchyWithColumns' }));
			}
		},
		{
			label: 'Max',
			key: 'Max',
			onClick: () => {
				dispatch(
					updateFieldValues({
						updateName: 'aggregation',
						key: 'value',
						value: 'Max',
						recordKey: record.key,
						isHierarchyChild: record.isHierarchyChild,
						hierarchyKey: record.parentKey
					})
				);
				// dispatch(modifyHierarchy({ record, step: 'deleteHierarchyWithColumns' }));
			}
		},
		{
			label: 'Min',
			key: 'Min',
			onClick: () => {
				dispatch(
					updateFieldValues({
						updateName: 'aggregation',
						key: 'value',
						value: 'Min',
						recordKey: record.key,
						isHierarchyChild: record.isHierarchyChild,
						hierarchyKey: record.parentKey
					})
				);
				// dispatch(modifyHierarchy({ record, step: 'deleteHierarchyWithColumns' }));
			}
		},
		{
			label: 'Sum',
			key: 'Sum',
			onClick: () => {
				dispatch(
					updateFieldValues({
						updateName: 'aggregation',
						key: 'value',
						value: 'Sum',
						recordKey: record.key,
						isHierarchyChild: record.isHierarchyChild,
						hierarchyKey: record.parentKey
					})
				);
				// dispatch(modifyHierarchy({ record, step: 'deleteHierarchyWithOutColumns' }));
			}
		}
	];
	// menu.forEach((ele) => {
	// 	if (record.aggregation.value === ele.label) {
	// 		ele.icon = <CheckOutlined />;
	// 	}
	// });
	return <Menu selectedKeys={[ record.aggregation.value ]} items={menu} />;
};

// export const handleCubeEditorHeader = ({ dispatch, cubeFieldsDataBackup, cubeFieldsData }) => ({
// 	initial: {
// 		firstCallBack: () => {
// 			dispatch(setCubeState('Schedule'));
// 			dispatch(setCubeFieldsData({ mode: 'reset' }));
// 			// dispatch(setCubeFieldsDataBackup(cubeFieldsData));
// 		},
// 		secondCallBack: () => {
// 			dispatch(setCubeState('Add New'));
// 		}
// 	},
// 	Schedule: {
// 		firstCallBack: () => {
// 			// dispatch(setCubeTableDeleteKeys('reset'));
// 			// cubeFieldsData: {
// 			// 	title: '',
// 			// 	children: [],
// 			// 	key: '',
// 			// 	hierarchyData: { isHierarchyPresent: false, hierarchyList: [] }
// 			// }
// 			dispatch(
// 				setCubeInintialList({
// 					// mode: 'edit',
// 					key: uuidv4(),
// 					timeStamp: moment(new Date()).format('MM/DD/YYYY LTS'),
// 					cubeFieldsData: { ...cubeFieldsDataBackup, title: cubeFieldsData.title, key: cubeFieldsData.key }
// 				})
// 			);
// 			dispatch(setCubeState('initial'));
// 			dispatch(setCubeMode('normal'));
// 		},
// 		secondCallBack: () => {
// 			dispatch(setCubeTableMode());
// 		}
// 	}
// });

// export const cubeList = {};
