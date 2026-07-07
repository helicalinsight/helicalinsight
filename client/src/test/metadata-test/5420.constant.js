export const store5420 = {
	'sw2k-1kff-8pm1-gbdl-qn': {
		name: 'View 1',
		alias: 'View 1',
		columns: {},
		key: 'sw2k-1kff-8pm1-gbdl-qn',
		uuid: 'sw2k-1kff-8pm1-gbdl-qn',
		query: 'select * from "dimdate"',
		queryType: 'conditionIf',
		labels: [
			{
				name: 'dim_id',
				type: 'numeric',
				checked: true
			},
			{
				name: 'fiscal_year',
				type: 'date',
				checked: true
			},
			{
				name: 'modified_date',
				type: 'dateTime',
				checked: true
			},
			{
				name: 'date_key',
				type: 'text',
				checked: true
			},
			{
				name: 'day_number',
				type: 'text',
				checked: true
			},
			{
				name: 'fiscal_month_name',
				type: 'text',
				checked: true
			},
			{
				name: 'fiscal_month_label',
				type: 'text',
				checked: true
			},
			{
				name: 'created_date',
				type: 'text',
				checked: true
			},
			{
				name: 'created_time',
				type: 'text',
				checked: true
			},
			{
				name: 'rating',
				type: 'text',
				checked: true
			}
		],
		dataSource: {
			id: '1',
			type: 'dynamicDataSource',
			baseType: 'global.jdbc',
			catSchemaPredicted: false,
			sync: false,
			catalog: '',
			schema: 'HIUSER',
			connId: 'sgpb8',
			classifier: 'db.workflow',
			datasourceName: 'SampleTravelDataDerby',
			dsKeyPath: 'wyot-6tq3-rv2k-22sa-18/yysb-klvt-04rl-cugc-y3/ebma-oreq-2pni-kdhj-pz',
			driverType: 'Derby',
			database: 'HIUSER'
		},
		error: false,
		validate: true,
		processedQuery: 'select * from (select * from "dimdate") foo fetch first 1 rows only'
	}
};
