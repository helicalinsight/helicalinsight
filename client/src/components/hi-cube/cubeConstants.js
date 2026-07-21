import moment from 'moment';
import { useEffect, useLayoutEffect, useRef, useState } from 'react';
import { createPortal } from 'react-dom';
import { v4 as uuidv4 } from 'uuid';
import { Menu, Input, Tooltip, Dropdown } from 'antd';
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
import { CheckOutlined, InfoCircleOutlined, CloseOutlined, RightOutlined, DownOutlined, CaretDownOutlined, CaretRightOutlined } from '@ant-design/icons';
import { getCubeEditorTooltipText } from './cubeEditorTooltips';
import { useCubeEditorBindings } from './cubeEditorContext';
import {
	AgentSemanticTypeSubmenuRow,
	getDefaultSemanticTypeForRole,
} from './cubeSemanticTypeField';

const { TextArea } = Input;
function CubeFieldSemanticLabel({ label, hideLabel = false, tooltipLabel }) {
	const { variant } = useCubeEditorBindings();
	const tooltipTitle = getCubeEditorTooltipText(tooltipLabel || label, variant);
	const infoIcon = tooltipTitle ? (
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
	) : null;

	if (hideLabel) {
		return infoIcon;
	}

	return (
		<span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
			<label style={{ fontSize: 12, fontWeight: 400 }}>{label}</label>
			{infoIcon}
		</span>
	);
}

export const CUBE_FIELD_SEMANTIC_TEXT_FIELDS = [
	{ fieldName: 'formula', label: 'Formula', placeholder: 'Add Formula (e.g., COUNT(table_name.column_name))' },
	{ fieldName: 'filter', label: 'Filter (optional)', placeholder: "Add filter. Ex: status = 'completed'." },
	{ fieldName: 'example', label: 'Example', placeholder: 'Add Example. Ex: term -> column or value.' },
	{ fieldName: 'description', label: 'Description', placeholder: 'Add Description. Ex:Business description.' },
];

const AGENT_HIDDEN_SEMANTIC_FIELDS = new Set(['filter', 'example']);

export const AGENT_AI_CONTEXT_FIELDS = [
	{
		fieldName: 'instructions',
		label: 'Instructions',
		placeholder: 'How the semantic model should use this field',
	},
	{
		fieldName: 'synonyms',
		label: 'Synonyms',
		placeholder: 'e.g. users, customers, clients',
	},
	{
		fieldName: 'example',
		label: 'Examples',
		placeholder: 'e.g. term → column or value',
	},
];

export function getCubeFieldSemanticTextFields(variant = 'cube') {
	if (variant === 'agent') {
		return CUBE_FIELD_SEMANTIC_TEXT_FIELDS.filter(
			({ fieldName }) => !AGENT_HIDDEN_SEMANTIC_FIELDS.has(fieldName),
		);
	}
	return CUBE_FIELD_SEMANTIC_TEXT_FIELDS;
}

export function getAgentAiContextFields() {
	return AGENT_AI_CONTEXT_FIELDS;
}

export const CUBE_FIELD_SEMANTIC_MENU_KEY = 'semantic-fields';

const AGENT_ROLE_MENU_KEY = 'agent-role-options';
const AGENT_CONVERT_ROLE_MENU_KEY = 'convert-dimension-measure';

export const CUBE_FIELD_MENU_TEXT_KEYS = [
	CUBE_FIELD_SEMANTIC_MENU_KEY,
	AGENT_ROLE_MENU_KEY,
	AGENT_CONVERT_ROLE_MENU_KEY,
];

export const cubeEditorMeasureData = [
	{ dataType: 'Number', format: [ '0.00', '00.00' ] },
	{ dataType: 'Date', format: [ 'DD-MM-YY', 'YYYY-MM-DD' ] }
];

const buildCubeMeasureFormats = ({ dispatch, record }) => {
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

const withCubeFieldsMenuText = (text) => (
	<span className="cube-fields-menu-action-label">{text}</span>
);

export function getAgentMeasureMenuItems({ dispatch, record }) {
	const dataType =
		record.measure?.DataType || cubeEditorMeasureData[0].dataType;
	const formats =
		buildCubeMeasureFormats({ dispatch, record })[dataType] ||
		buildCubeMeasureFormats({ dispatch, record })[
			cubeEditorMeasureData[0].dataType
		] ||
		[];
	const selectedFormat = record.measure?.Format || formats[0]?.value || "";

	const formatMenu = (
		<Menu
			className="cube-agent-format-submenu"
			selectedKeys={
				selectedFormat ? [`measure-format-${selectedFormat}`] : []
			}
			items={formats.map((ele) => ({
				label: withCubeFieldsMenuText(ele.value),
				key: `measure-format-${ele.value}`,
				onClick: ({ domEvent }) => {
					domEvent?.stopPropagation?.();
					if (!record.measure?.DataType) {
						dispatch(
							updateFieldValues({
								updateName: "measure",
								key: "DataType",
								value: dataType,
								recordKey: record.key,
								isHierarchyChild: record.isHierarchyChild,
								hierarchyKey: record.parentKey,
							}),
						);
					}
					ele.callBack();
				},
			}))}
		/>
	);

	return [
		{
			key: "agent-measure-format",
			label: (
				<div
					className="cube-agent-format-menu-row"
					onClick={(e) => e.stopPropagation()}
					onMouseDown={(e) => e.stopPropagation()}
				>
					<span className="cube-fields-menu-action-label">Format</span>
					<Dropdown
						overlay={formatMenu}
						trigger={["click"]}
						placement="rightTop"
						overlayClassName="cube-agent-format-submenu-overlay"
						getPopupContainer={() => document.body}
					>
						<span className="cube-agent-format-trigger">
							{selectedFormat ? (
								<span className="cube-agent-format-value">
									{selectedFormat}
								</span>
							) : null}
							<RightOutlined className="cube-agent-format-arrow" />
						</span>
					</Dropdown>
				</div>
			),
		},
	];
}

const renderAgentRightSubmenuItem = ({
	itemKey,
	label,
	selectedValue,
	menu,
}) => ({
	key: itemKey,
	label: (
		<div
			className="cube-agent-format-menu-row"
			onClick={(e) => e.stopPropagation()}
			onMouseDown={(e) => e.stopPropagation()}
		>
			<span className="cube-fields-menu-action-label">{label}</span>
			<Dropdown
				overlay={menu}
				trigger={["click"]}
				placement="rightTop"
				overlayClassName="cube-agent-format-submenu-overlay"
				getPopupContainer={() => document.body}
			>
				<span className="cube-agent-format-trigger">
					{selectedValue ? (
						<span className="cube-agent-format-value">
							{selectedValue}
						</span>
					) : null}
					<RightOutlined className="cube-agent-format-arrow" />
				</span>
			</Dropdown>
		</div>
	),
});

export function applyFieldMeasureRoleChange(
	dispatch,
	record,
	checked,
	variant = 'agent',
	semanticTypeOptions,
) {
	dispatch(
		updateFieldValues({
			updateName: "measure",
			key: "isMeasureCheck",
			value: checked,
			recordKey: record.key,
			isHierarchyChild: record.isHierarchyChild,
			hierarchyKey: record.parentKey,
		}),
	);
	dispatch(
		updateFieldValues({
			updateName: "isDimensionCheck",
			checkVal: !checked,
			recordKey: record.key,
			isHierarchyChild: record.isHierarchyChild,
			hierarchyKey: record.parentKey,
		}),
	);
	if (checked && variant === 'agent') {
		if (!record.measure?.DataType) {
			dispatch(
				updateFieldValues({
					updateName: "measure",
					key: "DataType",
					value: cubeEditorMeasureData[0].dataType,
					recordKey: record.key,
					isHierarchyChild: record.isHierarchyChild,
					hierarchyKey: record.parentKey,
				}),
			);
		}
		if (!record.measure?.Format) {
			dispatch(
				updateFieldValues({
					updateName: "measure",
					key: "Format",
					value: cubeEditorMeasureData[0].format[0],
					recordKey: record.key,
					isHierarchyChild: record.isHierarchyChild,
					hierarchyKey: record.parentKey,
				}),
			);
		}
	}
	if (!record.isHierarchy) {
		dispatch(
			updateFieldValues({
				updateName: 'semanticType',
				checkVal: getDefaultSemanticTypeForRole(
					checked,
					variant,
					semanticTypeOptions,
				),
				recordKey: record.key,
				isHierarchyChild: record.isHierarchyChild,
				hierarchyKey: record.parentKey,
			}),
		);
	}
}

function toggleAgentFieldMeasure(dispatch, record, checked, semanticTypeOptions) {
	applyFieldMeasureRoleChange(
		dispatch,
		record,
		checked,
		'agent',
		semanticTypeOptions,
	);
}

function AgentRoleMenuTree({ roleLabel, childItems }) {
	const [expanded, setExpanded] = useState(false);
	const { variant } = useCubeEditorBindings();
	const stop = (e) => e.stopPropagation();
	const roleTooltip = getCubeEditorTooltipText("Dimension/Measure", variant);

	return (
		<div
			className="cube-field-agent-role-tree"
			onClick={stop}
			onMouseDown={stop}
			onKeyDown={stop}
		>
			<div
				className="cube-field-agent-role-tree-root"
				role="button"
				tabIndex={0}
				aria-expanded={expanded}
				onClick={(e) => {
					e.stopPropagation();
					setExpanded((prev) => !prev);
				}}
				onKeyDown={(e) => {
					if (e.key === "Enter" || e.key === " ") {
						e.preventDefault();
						e.stopPropagation();
						setExpanded((prev) => !prev);
					}
				}}
			>
				<span className="cube-field-agent-role-tree-caret">
					{expanded ? <CaretDownOutlined /> : <CaretRightOutlined />}
				</span>
				<span className="cube-agent-menu-group-label">{roleLabel}</span>
				{roleTooltip ? (
					<CubeFieldSemanticLabel label="Dimension/Measure" hideLabel />
				) : null}
			</div>
			{expanded ? (
				<div className="cube-field-agent-role-tree-children">
					{childItems.map((item) => (
						<div
							key={item.key}
							className="cube-field-agent-role-tree-item"
						>
							{item.label}
						</div>
					))}
				</div>
			) : null}
		</div>
	);
}

function renderAgentRoleMenuItem({ roleLabel, childItems }) {
	return {
		key: AGENT_ROLE_MENU_KEY,
		label: (
			<AgentRoleMenuTree roleLabel={roleLabel} childItems={childItems} />
		),
	};
}

function renderAgentConvertRoleMenuItem({
	dispatch,
	record,
	isMeasure,
	semanticTypeOptions,
}) {
	const convertLabel = isMeasure
		? "Convert to Dimension"
		: "Convert to Measure";

	return {
		key: AGENT_CONVERT_ROLE_MENU_KEY,
		label: (
			<span className="cube-fields-menu-action-row">
				<span className="cube-fields-menu-action-label">{convertLabel}</span>
			</span>
		),
		onClick: () =>
			toggleAgentFieldMeasure(
				dispatch,
				record,
				!isMeasure,
				semanticTypeOptions,
			),
	};
}

export function getCubeSortMenuItems({ dispatch, record }) {
	const applySortValue = (value) => {
		const isNatural = value === "Natural";
		dispatch(
			updateFieldValues({
				updateName: "sort",
				key: "value",
				value,
				recordKey: record.key,
				isHierarchyChild: record.isHierarchyChild,
				hierarchyKey: record.parentKey,
			}),
		);
		dispatch(
			updateFieldValues({
				updateName: "sort",
				key: "isSortCheck",
				value: !isNatural,
				recordKey: record.key,
				isHierarchyChild: record.isHierarchyChild,
				hierarchyKey: record.parentKey,
			}),
		);
	};

	return [
		{
			label: "Ascending",
			key: "Ascending",
			onClick: ({ domEvent } = {}) => {
				domEvent?.stopPropagation?.();
				applySortValue("Ascending");
			},
		},
		{
			label: "Descending",
			key: "Descending",
			onClick: ({ domEvent } = {}) => {
				domEvent?.stopPropagation?.();
				applySortValue("Descending");
			},
		},
		{
			label: "Natural",
			key: "Natural",
			onClick: ({ domEvent } = {}) => {
				domEvent?.stopPropagation?.();
				applySortValue("Natural");
			},
		},
	];
}

export function getCubeAggregationMenuItems({ dispatch, record }) {
	const applyAggregationValue = (value) => {
		const isNone = value === "None";
		const defaultFunction = isNone
			? "db.generic.aggregate.none"
			: `db.generic.aggregate.${value
					.toLowerCase()
					.replace(/\s+/g, "")}`;
		dispatch(
			updateFieldValues({
				updateName: "aggregation",
				key: "value",
				value,
				recordKey: record.key,
				isHierarchyChild: record.isHierarchyChild,
				hierarchyKey: record.parentKey,
			}),
		);
		dispatch(
			updateFieldValues({
				updateName: "aggregation",
				key: "isAggregationCheck",
				value: !isNone,
				recordKey: record.key,
				isHierarchyChild: record.isHierarchyChild,
				hierarchyKey: record.parentKey,
			}),
		);
		dispatch(
			updateFieldValues({
				updateName: "defaultFunction",
				checkVal: defaultFunction,
				recordKey: record.key,
				isHierarchyChild: record.isHierarchyChild,
				hierarchyKey: record.parentKey,
			}),
		);
	};

	return [
		"None",
		"Avg",
		"Count",
		"Distinct Count",
		"Max",
		"Min",
		"Sum",
	].map((value) => ({
		label: value,
		key: value,
		onClick: ({ domEvent } = {}) => {
			domEvent?.stopPropagation?.();
			applyAggregationValue(value);
		},
	}));
}

export function getAgentSortMenuItems({ dispatch, record }) {
	const selectedValue = record.sort?.value || "";
	const sortMenu = (
		<Menu
			className="cube-agent-format-submenu"
			selectedKeys={selectedValue ? [selectedValue] : []}
			items={getCubeSortMenuItems({ dispatch, record }).map((item) => ({
				...item,
				label: withCubeFieldsMenuText(item.label),
			}))}
		/>
	);

	return [
		renderAgentRightSubmenuItem({
			itemKey: "agent-sort",
			label: "Sort",
			selectedValue,
			menu: sortMenu,
		}),
	];
}

export function getAgentAggregationMenuItems({ dispatch, record }) {
	const selectedValue = record.aggregation?.value || "";
	const aggregationMenu = (
		<Menu
			className="cube-agent-format-submenu"
			selectedKeys={selectedValue ? [selectedValue] : []}
			items={getCubeAggregationMenuItems({ dispatch, record }).map(
				(item) => ({
					...item,
					label: withCubeFieldsMenuText(item.label),
				}),
			)}
		/>
	);

	return [
		renderAgentRightSubmenuItem({
			itemKey: "agent-aggregation",
			label: "Aggregation",
			selectedValue,
			menu: aggregationMenu,
		}),
	];
}

function getCubeMeasureMenuItems({ dispatch, record }) {
	const dataType =
		record.measure?.DataType || cubeEditorMeasureData[0].dataType;
	return [
		{
			label: "Data Type",
			key: "Data Type",
			children: cubeEditorMeasureData.map((data) => ({
				label: data.dataType,
				key: data.dataType,
				onClick: () => {
					dispatch(
						updateFieldValues({
							updateName: "measure",
							key: "DataType",
							value: data.dataType,
							recordKey: record.key,
							isHierarchyChild: record.isHierarchyChild,
							hierarchyKey: record.parentKey,
						}),
					);
				},
			})),
		},
		{
			label: "Format",
			key: "Format",
			children: (
				buildCubeMeasureFormats({ dispatch, record })[dataType] || []
			).map((ele) => ({
				label: ele.value,
				key: ele.value,
				onClick: ele.callBack,
			})),
		},
	];
}

const getSemanticDraftFromRecord = (record, variant = 'cube') => {
	const draft = getCubeFieldSemanticTextFields(variant).reduce((acc, { fieldName }) => {
		acc[fieldName] = record[fieldName] ?? '';
		return acc;
	}, {});
	if (variant === 'agent') {
		draft.semanticType = record.semanticType ?? '';
		getAgentAiContextFields().forEach(({ fieldName }) => {
			draft[fieldName] = record[fieldName] ?? '';
		});
	}
	return draft;
};

const renderSemanticTextField = ({
	fieldName,
	label,
	placeholder,
	draft,
	onDraftChange,
	disabled,
	clearFieldTooltip,
	showLabel = true,
	className = "",
	autoSize = { minRows: 1, maxRows: 5 },
}) => (
	<div key={fieldName} className={`cube-field-semantic-field ${className}`.trim()}>
		{showLabel ? (
			<div className="cube-field-semantic-field-label-row">
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
		) : null}
		<TextArea
			className="cube-field-semantic-textarea"
			disabled={disabled}
			value={draft[fieldName] ?? ''}
			placeholder={placeholder}
			autoSize={autoSize}
			onChange={(e) =>
				onDraftChange({ ...draft, [fieldName]: e.target.value })
			}
		/>
	</div>
);

function getAgentFieldPreview(value, emptyLabel = "Add") {
	const trimmed = (value ?? "").trim();
	if (!trimmed) {
		return { text: emptyLabel, isEmpty: true };
	}
	return {
		text: trimmed.length > 22 ? `${trimmed.slice(0, 22)}…` : trimmed,
		isEmpty: false,
	};
}

function getAgentTextEditorPosition(
	rowEl,
	editorHeight = 70,
	editorWidth = null,
) {
	const gap = 8;
	const minWidth = 200;
	const minHeight = 54;
	const viewportPad = 8;
	const panelChromeHeight = 42;
	const rect = rowEl.getBoundingClientRect();
	const viewportWidth = window.innerWidth;
	const viewportHeight = window.innerHeight;

	const spaceRight = Math.max(0, viewportWidth - rect.right - gap - viewportPad);
	const spaceLeft = Math.max(0, rect.left - gap - viewportPad);
	const openLeftward = spaceRight < minWidth && spaceLeft > spaceRight;
	const maxWidth = Math.max(minWidth, openLeftward ? spaceLeft : spaceRight);
	const preferredWidth =
		editorWidth == null
			? Math.min(maxWidth, Math.max(minWidth, Math.round(viewportWidth * 0.22)))
			: editorWidth;
	const panelWidth = Math.min(Math.max(preferredWidth, minWidth), maxWidth);

	let left = openLeftward
		? Math.max(viewportPad, rect.left - panelWidth - gap)
		: rect.right + gap;
	if (!openLeftward && left + panelWidth > viewportWidth - viewportPad) {
		left = Math.max(viewportPad, viewportWidth - viewportPad - panelWidth);
	}

	const spaceBelow = viewportHeight - rect.top - viewportPad;
	const spaceAbove = rect.bottom - viewportPad;
	const openUpward = spaceBelow < 180 && spaceAbove > spaceBelow;
	const availableHeight = Math.max(96, openUpward ? spaceAbove : spaceBelow);
	const textareaMaxHeight = Math.max(minHeight, availableHeight - panelChromeHeight);
	const panelHeight = Math.min(
		editorHeight + panelChromeHeight,
		availableHeight,
	);
	const top = openUpward
		? Math.max(viewportPad, rect.bottom - panelHeight)
		: Math.max(viewportPad, rect.top);

	return {
		top,
		left,
		width: panelWidth,
		maxWidth,
		maxHeight: availableHeight,
		textareaMaxHeight,
		openUpward,
		openLeftward,
		minWidth,
		minHeight,
		viewportPad,
		panelChromeHeight,
	};
}

export function AgentRightSubmenuTextField({
	fieldKey,
	label,
	tooltipLabel,
	value,
	placeholder,
	disabled,
	clearFieldTooltip,
	onChange,
	openKey = null,
	onOpenKeyChange,
}) {
	const open = openKey === fieldKey;
	const [panelStyle, setPanelStyle] = useState(null);
	const [editorHeight, setEditorHeight] = useState(70);
	const [editorWidth, setEditorWidth] = useState(null);
	const rowRef = useRef(null);
	const panelRef = useRef(null);
	const textAreaRef = useRef(null);
	const isResizingRef = useRef(false);
	const preview = getAgentFieldPreview(value);
	const stop = (e) => e.stopPropagation();

	const setOpen = (nextOpen) => {
		if (!onOpenKeyChange) {
			return;
		}
		onOpenKeyChange(nextOpen ? fieldKey : null);
	};

	const syncPanelPosition = () => {
		if (!rowRef.current) {
			return;
		}
		const nextStyle = getAgentTextEditorPosition(
			rowRef.current,
			editorHeight,
			editorWidth,
		);
		setPanelStyle(nextStyle);
		if (editorWidth == null) {
			setEditorWidth(nextStyle.width);
		}
	};

	const syncTextAreaHeight = () => {
		const textArea =
			textAreaRef.current?.resizableTextArea?.textArea ||
			textAreaRef.current;
		if (!textArea || !panelStyle) {
			return;
		}
		const contentHeight = Math.max(textArea.scrollHeight, 54);
		setEditorHeight((currentHeight) =>
			Math.min(
				Math.max(contentHeight, currentHeight),
				panelStyle.textareaMaxHeight,
			),
		);
	};

	const openEditor = () => {
		if (disabled) {
			return;
		}
		syncPanelPosition();
		setOpen(true);
	};

	const closeEditor = () => {
		if (isResizingRef.current) {
			return;
		}
		setOpen(false);
	};

	useLayoutEffect(() => {
		if (!open) {
			return undefined;
		}
		syncPanelPosition();
		const frame = requestAnimationFrame(() => {
			syncTextAreaHeight();
			const textArea =
				textAreaRef.current?.resizableTextArea?.textArea ||
				textAreaRef.current;
			if (!textArea || typeof textArea.setSelectionRange !== "function") {
				return;
			}
			const len = String(textArea.value ?? "").length;
			textArea.focus();
			textArea.setSelectionRange(len, len);
		});
		const onReposition = () => {
			syncPanelPosition();
			requestAnimationFrame(syncTextAreaHeight);
		};
		window.addEventListener("resize", onReposition);
		window.addEventListener("scroll", onReposition, true);
		return () => {
			cancelAnimationFrame(frame);
			window.removeEventListener("resize", onReposition);
			window.removeEventListener("scroll", onReposition, true);
		};
	}, [open, editorHeight, editorWidth]);

	useEffect(() => {
		if (!open) {
			return undefined;
		}
		requestAnimationFrame(syncTextAreaHeight);
	}, [open, value, editorHeight]);

	useEffect(() => {
		if (!open) {
			return undefined;
		}
		const onPointerDown = (e) => {
			if (isResizingRef.current) {
				return;
			}
			const target = e.target;
			if (panelRef.current?.contains(target) || rowRef.current?.contains(target)) {
				return;
			}
			setOpen(false);
		};
		document.addEventListener("mousedown", onPointerDown, true);
		return () => document.removeEventListener("mousedown", onPointerDown, true);
	}, [open]);

	useEffect(() => {
		if (!open) {
			return undefined;
		}
		const closeIfTriggerGone = () => {
			const el = rowRef.current;
			if (!el || !document.body.contains(el) || el.getClientRects().length === 0) {
				setOpen(false);
			}
		};
		const intervalId = window.setInterval(closeIfTriggerGone, 150);
		return () => window.clearInterval(intervalId);
	}, [open]);

	const onResizeMouseDown = (e) => {
		stop(e);
		e.preventDefault();
		if (!panelRef.current) {
			return;
		}
		isResizingRef.current = true;
		const panelRect = panelRef.current.getBoundingClientRect();
		const growsUpward = Boolean(panelStyle?.openUpward);
		const growsLeftward = Boolean(panelStyle?.openLeftward);
		const viewportPad = panelStyle?.viewportPad ?? 8;
		const minWidth = panelStyle?.minWidth ?? 200;
		const minHeight = panelStyle?.minHeight ?? 54;
		const panelChromeHeight = panelStyle?.panelChromeHeight ?? 42;
		const fixedLeft = panelRect.left;
		const fixedRight = panelRect.right;
		const fixedTop = panelRect.top;
		const fixedBottom = panelRect.bottom;

		const onMove = (moveEvent) => {
			const viewportWidth = window.innerWidth;
			const viewportHeight = window.innerHeight;

			let nextLeft = fixedLeft;
			let nextWidth;
			if (growsLeftward) {
				const minLeft = viewportPad;
				const maxLeft = fixedRight - minWidth;
				nextLeft = Math.min(maxLeft, Math.max(minLeft, moveEvent.clientX));
				nextWidth = fixedRight - nextLeft;
			} else {
				const maxRight = viewportWidth - viewportPad;
				const nextRight = Math.min(
					maxRight,
					Math.max(fixedLeft + minWidth, moveEvent.clientX),
				);
				nextWidth = nextRight - fixedLeft;
			}

			let nextTop = fixedTop;
			let nextHeight;
			if (growsUpward) {
				const minTop = viewportPad;
				const maxTop = fixedBottom - minHeight - panelChromeHeight;
				nextTop = Math.min(maxTop, Math.max(minTop, moveEvent.clientY));
				nextHeight = Math.max(
					minHeight,
					fixedBottom - nextTop - panelChromeHeight,
				);
			} else {
				const maxBottom = viewportHeight - viewportPad;
				const nextBottom = Math.min(
					maxBottom,
					Math.max(fixedTop + minHeight + panelChromeHeight, moveEvent.clientY),
				);
				nextHeight = Math.max(
					minHeight,
					nextBottom - fixedTop - panelChromeHeight,
				);
			}

			setEditorWidth(nextWidth);
			setEditorHeight(nextHeight);
			setPanelStyle((prev) =>
				prev
					? {
							...prev,
							left: nextLeft,
							top: nextTop,
							width: nextWidth,
							maxWidth: growsLeftward
								? fixedRight - viewportPad
								: viewportWidth - viewportPad - fixedLeft,
							textareaMaxHeight: Math.max(
								minHeight,
								(growsUpward
									? fixedBottom - viewportPad
									: viewportHeight - viewportPad - fixedTop) - panelChromeHeight,
							),
					  }
					: prev,
			);
		};

		const endResize = () => {
			isResizingRef.current = false;
			window.removeEventListener("mousemove", onMove);
			window.removeEventListener("pointermove", onMove);
			window.removeEventListener("mouseup", endResize);
			window.removeEventListener("pointerup", endResize);
		};
		window.addEventListener("mousemove", onMove);
		window.addEventListener("pointermove", onMove);
		window.addEventListener("mouseup", endResize);
		window.addEventListener("pointerup", endResize);
	};

	return (
		<div className="cube-agent-accordion-field" data-field={fieldKey}>
			<div
				ref={rowRef}
				className={`cube-agent-format-menu-row cube-agent-text-submenu-row${
					open ? " is-open" : ""
				}`}
				onClick={(e) => {
					stop(e);
					if (open) {
						closeEditor();
					} else {
						openEditor();
					}
				}}
				onMouseDown={stop}
				role="button"
				tabIndex={0}
				aria-expanded={open}
				onKeyDown={(e) => {
					if (e.key === "Enter" || e.key === " ") {
						e.preventDefault();
						stop(e);
						if (open) {
							closeEditor();
						} else {
							openEditor();
						}
					}
				}}
			>
				<span className="cube-agent-accordion-label">
					<CubeFieldSemanticLabel
						label={label}
						tooltipLabel={tooltipLabel}
					/>
				</span>
					<span className="cube-agent-format-trigger">
						<span
							className={`cube-agent-format-value${
								preview.isEmpty ? " cube-agent-format-value--empty" : ""
							}`}
						>
							{preview.text}
						</span>
						{open ? (
							<DownOutlined className="cube-agent-format-arrow" />
						) : (
							<RightOutlined className="cube-agent-format-arrow" />
						)}
					</span>
			</div>
			{open && panelStyle
				? createPortal(
						<div
							ref={panelRef}
							className={`cube-agent-text-submenu-panel cube-agent-text-submenu-panel--fixed${
								!disabled && (value ?? "").trim()
									? " cube-agent-text-submenu-panel--has-clear"
									: ""
							}`}
							style={{
								top: panelStyle.top,
								left: panelStyle.left,
								width: panelStyle.width,
								maxHeight: panelStyle.maxHeight,
							}}
							onClick={stop}
							onMouseDown={stop}
						>
							{!disabled && (value ?? "").trim() ? (
								<div className="cube-agent-accordion-editor-toolbar">
									<span />
									<Tooltip title={clearFieldTooltip} placement="top">
										<CloseOutlined
											className="cube-agent-accordion-clear"
											onClick={(e) => {
												stop(e);
												onChange("");
											}}
										/>
									</Tooltip>
								</div>
							) : null}
							<TextArea
								key={`${fieldKey}-editor`}
								ref={textAreaRef}
								className="cube-agent-text-submenu-textarea"
								disabled={disabled}
								value={value ?? ""}
								placeholder={placeholder}
								rows={2}
								autoFocus
								style={{
									height: editorHeight,
									maxHeight: panelStyle.textareaMaxHeight,
								}}
								onChange={(e) => {
									onChange(e.target.value);
									requestAnimationFrame(syncTextAreaHeight);
								}}
							/>
							<span
								className={`cube-agent-text-resize-handle${
									panelStyle.openUpward
										? " cube-agent-text-resize-handle--upward"
										: ""
								}${
									panelStyle.openLeftward
										? " cube-agent-text-resize-handle--leftward"
										: ""
								}`}
								onMouseDown={onResizeMouseDown}
								onPointerDown={onResizeMouseDown}
								role="presentation"
							/>
						</div>,
						document.body,
				  )
				: null}
		</div>
	);
}

function AgentAiContextTree({
	draft,
	onDraftChange,
	disabled,
	clearFieldTooltip,
	openKey,
	onOpenKeyChange,
}) {
	const aiFields = getAgentAiContextFields();
	const [rootExpanded, setRootExpanded] = useState(false);

	return (
		<div className="cube-field-ai-context-tree">
			<div
				className="cube-field-ai-context-tree-root"
				role="button"
				tabIndex={0}
				aria-expanded={rootExpanded}
				onClick={(e) => {
					e.stopPropagation();
					setRootExpanded((prev) => !prev);
				}}
				onKeyDown={(e) => {
					if (e.key === "Enter" || e.key === " ") {
						e.preventDefault();
						e.stopPropagation();
						setRootExpanded((prev) => !prev);
					}
				}}
			>
				<span className="cube-field-ai-context-tree-caret">
					{rootExpanded ? <CaretDownOutlined /> : <CaretRightOutlined />}
				</span>
				<span className="cube-agent-menu-group-label">AI Context</span>
				<CubeFieldSemanticLabel label="AI Context" hideLabel />
			</div>
			{rootExpanded ? (
				<div className="cube-field-ai-context-tree-children">
					{aiFields.map((field) => (
						<AgentRightSubmenuTextField
							key={field.fieldName}
							fieldKey={field.fieldName}
							label={field.label}
							value={draft[field.fieldName] ?? ""}
							placeholder={field.placeholder}
							disabled={disabled}
							clearFieldTooltip={clearFieldTooltip}
							openKey={openKey}
							onOpenKeyChange={onOpenKeyChange}
							onChange={(nextValue) =>
								onDraftChange({
									...draft,
									[field.fieldName]: nextValue,
								})
							}
						/>
					))}
				</div>
			) : null}
		</div>
	);
}

function CubeFieldSemanticMenuPanel({
  draft,
  onDraftChange,
  disabled = false,
  dispatch,
  record,
  menuVisible = true,
}) {
  const { variant } = useCubeEditorBindings();
  const clearFieldTooltip = getCubeEditorTooltipText("Clear field values", variant);
  const stopMenuEvent = (e) => e.stopPropagation();
  const [openKey, setOpenKey] = useState(null);

  useEffect(() => {
    if (!menuVisible) {
      setOpenKey(null);
    }
  }, [menuVisible]);

  if (variant !== "agent") {
    return (
      <div
        className="cube-field-semantic-menu-panel"
        onClick={stopMenuEvent}
        onMouseDown={stopMenuEvent}
        onKeyDown={stopMenuEvent}
      >
        {getCubeFieldSemanticTextFields(variant).map((field) =>
          renderSemanticTextField({
            ...field,
            draft,
            onDraftChange,
            disabled,
            clearFieldTooltip,
          }),
        )}
      </div>
    );
  }

  const textFields = getCubeFieldSemanticTextFields("agent");

  return (
    <div
      className="cube-field-semantic-menu-panel cube-field-semantic-menu-panel--agent"
      onClick={stopMenuEvent}
      onMouseDown={stopMenuEvent}
      onKeyDown={stopMenuEvent}
    >
      <div className="cube-agent-field-details">
        {textFields.map((field) => (
          <AgentRightSubmenuTextField
            key={field.fieldName}
            fieldKey={field.fieldName}
            label={field.label}
            value={draft[field.fieldName] ?? ""}
            placeholder={field.placeholder}
            disabled={disabled}
            clearFieldTooltip={clearFieldTooltip}
            openKey={openKey}
            onOpenKeyChange={setOpenKey}
            onChange={(nextValue) =>
              onDraftChange({
                ...draft,
                [field.fieldName]: nextValue,
              })
            }
          />
        ))}

        <AgentSemanticTypeSubmenuRow
          label={<CubeFieldSemanticLabel label="Semantic Type" />}
          disabled={disabled}
          value={draft.semanticType ?? ""}
          clearFieldTooltip={clearFieldTooltip}
          openKey={openKey}
          onOpenKeyChange={setOpenKey}
          onChange={(nextValue) => {
            const nextDraft = { ...draft, semanticType: nextValue || "" };
            onDraftChange(nextDraft);
            if (dispatch && record) {
              commitCubeFieldSemanticDraft({
                dispatch,
                record,
                draft: nextDraft,
                variant,
              });
            }
          }}
        />
      </div>

      <AgentAiContextTree
        draft={draft}
        onDraftChange={onDraftChange}
        disabled={disabled}
        clearFieldTooltip={clearFieldTooltip}
        openKey={openKey}
        onOpenKeyChange={setOpenKey}
      />
    </div>
  );
}

export function commitCubeFieldSemanticDraft({ dispatch, record, draft, variant = 'cube' }) {
	if (record.isHierarchyChild && variant !== 'agent') {
		return;
	}
	const textFields = [
		...getCubeFieldSemanticTextFields(variant),
		...(variant === 'agent' ? getAgentAiContextFields() : []),
	];
	// Avoid double-committing `example` when it appears in both lists (agent path).
	const committed = new Set();
	textFields.forEach(({ fieldName }) => {
		if (committed.has(fieldName)) {
			return;
		}
		committed.add(fieldName);
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
	if (variant === 'agent') {
		const nextSemanticType = draft.semanticType ?? '';
		const prevSemanticType = record.semanticType ?? '';
		if (nextSemanticType !== prevSemanticType) {
			dispatch(
				updateFieldValues({
					updateName: 'semanticType',
					checkVal: nextSemanticType,
					recordKey: record.key,
					isHierarchyChild: record.isHierarchyChild,
					hierarchyKey: record.parentKey,
				}),
			);
		}
	}
}

function buildCubeFieldsMenuItems({
	dispatch,
	record,
	hierarchyData,
	semanticDraft,
	onSemanticDraftChange,
	semanticFieldsDisabled = false,
	variant = "cube",
	menuVisible = true,
	semanticTypeOptions,
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
            <span className="cube-fields-menu-action-row">
              <span className="cube-fields-menu-action-label">Delete</span>
              <Tooltip
                title={removeFieldTooltip}
                placement="right"
              >
                <InfoCircleOutlined
                  className="cube-info-icon"
                  style={{ fontSize: 11 }}
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

	if (variant === "agent" && !record.isHierarchy) {
		const insertAt = Math.min(1, menu.length);
		const isMeasure = Boolean(record.measure?.isMeasureCheck);
		menu.splice(
			insertAt,
			0,
			renderAgentConvertRoleMenuItem({
				dispatch,
				record,
				isMeasure,
				semanticTypeOptions,
			}),
			isMeasure
				? renderAgentRoleMenuItem({
						roleLabel: "Measure",
						childItems: [
							...getAgentMeasureMenuItems({ dispatch, record }),
							...getAgentAggregationMenuItems({ dispatch, record }),
						],
					})
				: renderAgentRoleMenuItem({
						roleLabel: "Dimension",
						childItems: [
							...getAgentSortMenuItems({ dispatch, record }),
						],
					}),
		);
	}

	if (!record.isHierarchy) {
		menu.push({
			key: CUBE_FIELD_SEMANTIC_MENU_KEY,
			label: (
				<CubeFieldSemanticMenuPanel
					draft={semanticDraft}
					onDraftChange={onSemanticDraftChange}
					disabled={semanticFieldsDisabled}
					dispatch={dispatch}
					record={record}
					menuVisible={menuVisible}
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
				acc.push({
					label: (
						<span className="cube-fields-menu-action-label">
							{ele.hierarchyName}
						</span>
					),
					key: ele.hierarchyKey,
					className: "cube-fields-menu-hierarchy-option",
					onClick: () => {
						dispatch(
							modifyHierarchy({
								selectedHierarchy: ele.hierarchyName,
								record,
								step: 'addToExistingHierarchy'
							})
						);
					},
				});
			}
			return acc;
		}, [])
		if (hierarchyData.isHierarchyPresent && (record.isDimensionCheck || record.isHierarchy) && existingHierarchyChildren.length) {
			menu.splice(1, 0, {
				label: (
					<span className="cube-fields-menu-action-label">
						Add to an existing Hierarchy
					</span>
				),
				key: "Add to an existing Hierarchy",
				className: "cube-fields-menu-hierarchy-item",
				popupClassName: "cube-fields-context-submenu",
				children: existingHierarchyChildren,
			});
		}
	}
	return menu;
}

export function CubeFieldsMenu({
	dispatch,
	record,
	hierarchyData,
	semanticCommitsRef,
	menuVisible = true,
	semanticTypeOptions,
}) {
	const { variant } = useCubeEditorBindings();
	const semanticDraftRef = useRef(getSemanticDraftFromRecord(record, variant));
	const [semanticDraft, setSemanticDraft] = useState(() => semanticDraftRef.current);

	const updateSemanticDraft = (nextDraft) => {
		semanticDraftRef.current = nextDraft;
		setSemanticDraft(nextDraft);
	};

	useEffect(() => {
		const nextDraft = getSemanticDraftFromRecord(record, variant);
		semanticDraftRef.current = nextDraft;
		setSemanticDraft(nextDraft);
	}, [
		record.key,
		record.parentKey,
		record.isHierarchyChild,
		record.semanticType,
		variant,
		...getCubeFieldSemanticTextFields(variant).map(({ fieldName }) => record[fieldName]),
		...(variant === "agent"
			? getAgentAiContextFields().map(({ fieldName }) => record[fieldName])
			: []),
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
				variant,
			});
		};
		semanticCommitsRef.current.set(record.key, commitDraft);
		return () => {
			semanticCommitsRef.current.delete(record.key);
		};
	}, [semanticCommitsRef, dispatch, record, variant]);

	const handleMenuClick = (e) => {
		const isMeasureFormatPick = String(e.key || "").startsWith(
			"measure-format-",
		);
		// Keep Format submenu usable; don't close when picking a format value
		if (
			!CUBE_FIELD_MENU_TEXT_KEYS.includes(e.key) &&
			e.key !== "deleteFromHierarchy" &&
			e.key !== "agent-measure-format" &&
			!isMeasureFormatPick
		) {
			commitCubeFieldSemanticDraft({
				dispatch,
				record,
				draft: semanticDraftRef.current,
				variant,
			});
			dispatch(
				updateFieldValues({
					updateName: "fieldsDropdownOpen",
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
		menuVisible,
		semanticTypeOptions,
	});

	return (
		<Menu
			className="cube-fields-context-menu"
			onClick={handleMenuClick}
			items={menu}
			selectedKeys={[]}
		/>
	);
}

export const cubeFieldsMenu = (props) => <CubeFieldsMenu {...props} />;

export const cubeSortMenu = ({ dispatch, record }) => {
	const menu = getCubeSortMenuItems({ dispatch, record });
	return <Menu selectedKeys={[ record.sort.value ]} items={menu} />;
};

export const cubeMeasureMenu = ({ dispatch, record }) => {
	const menu = getCubeMeasureMenuItems({ dispatch, record });
	return (
		<Menu
			selectedKeys={[record.measure?.Format, record.measure?.DataType].filter(Boolean)}
			items={menu}
		/>
	);
};

export const cubeAggregationMenu = ({ dispatch, record, hierarchyData }) => {
	const menu = getCubeAggregationMenuItems({ dispatch, record });
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
