import { Menu } from 'antd';
import { useDispatch } from 'react-redux';
import { allConditions } from '../../utils/constants';
import { changeFilterCondition } from '../../../../../redux/actions/hreport.actions';
import { getFilterDataType } from '../../../../../utils/filter-utils';

// This component is not used in the entire applicaton.
const Conditions = (props) => {
	const dispatch = useDispatch();
	let { filter } = props;
	let { uid } = filter;
	let conditionList = allConditions[getFilterDataType(filter)];
	const handleSelect = (condition) => {
		dispatch(changeFilterCondition({ uid, condition }));
	};
	
	return (
		<Menu className="filter-conditions-list">
			{sortedList.map((item) => {
				return (
					<Menu.Item key={item.key} onClick={(e) => handleSelect(item.key)}>
						{item.display}
					</Menu.Item>
				);
			})}
		</Menu>
	);
};

export default Conditions;
